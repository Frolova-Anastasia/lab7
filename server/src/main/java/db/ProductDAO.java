package db;
import data.Coordinates;
import data.Organization;
import data.Product;
import data.UnitOfMeasure;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;


public class ProductDAO {
    private final Connection connection;
    private final OrganizationDAO organizationDAO;

    public ProductDAO(Connection connection, OrganizationDAO organizationDAO) {
        this.connection = connection;
        this.organizationDAO = organizationDAO;
    }



    public boolean insertProduct(Product product, int userId, Integer positionIndex) throws SQLException {
        connection.setAutoCommit(false);
        try {
            if (positionIndex != null) {
                // Сдвигаем все продукты, начиная с указанной позиции
                String shiftSql = "UPDATE products SET position_index = position_index + 1 WHERE position_index >= ?";
                try (PreparedStatement shiftStmt = connection.prepareStatement(shiftSql)) {
                    shiftStmt.setInt(1, positionIndex);
                    shiftStmt.executeUpdate();
                }
            } else {
                // Определяем следующий индекс в конце
                String maxIndexSql = "SELECT COALESCE(MAX(position_index), -1) + 1 FROM products";
                try (PreparedStatement maxStmt = connection.prepareStatement(maxIndexSql);
                     ResultSet rs = maxStmt.executeQuery()) {
                    if (rs.next()) {
                        positionIndex = rs.getInt(1);
                    }
                }
            }

            // Вставка продукта
            String sql = """
            INSERT INTO products (name, x, y, price, unit_of_measure, creation_date, user_id, position_index)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, product.getName());
                stmt.setInt(2, product.getCoordinates().getX());
                stmt.setLong(3, product.getCoordinates().getY());

                if (product.getPrice() != null) {
                    stmt.setFloat(4, product.getPrice());
                } else {
                    stmt.setNull(4, Types.REAL);
                }

                if (product.getUnitOfMeasure() != null) {
                    stmt.setString(5, product.getUnitOfMeasure().name());
                } else {
                    stmt.setNull(5, Types.VARCHAR);
                }

                stmt.setObject(6, product.getCreationDate().toOffsetDateTime());

                stmt.setInt(7, userId);
                stmt.setInt(8, positionIndex);

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        int productId = keys.getInt(1);
                        if (product.getManufacturer() != null) {
                            organizationDAO.insertOrganization(product.getManufacturer(), productId);
                        }
                    }
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }




    public boolean addProduct(Product product, int userId) throws SQLException {
        int maxIndex = 0;
        String maxSql = "SELECT COALESCE(MAX(position_index), 0) FROM products";
        try (PreparedStatement stmt = connection.prepareStatement(maxSql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maxIndex = rs.getInt(1);
            }
        }
        return insertProduct(product, userId, maxIndex + 1);
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new LinkedList<>();
        String sql = "SELECT * FROM products ORDER BY position_index";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("id");
                String name = rs.getString("name");
                int x = rs.getInt("x");
                long y = rs.getLong("y");
                Float price = rs.getObject("price") != null ? rs.getFloat("price") : null;
                String unitStr = rs.getString("unit_of_measure");
                UnitOfMeasure unit = unitStr != null ? UnitOfMeasure.valueOf(unitStr) : null;


                Timestamp ts = rs.getTimestamp("creation_date");
                ZonedDateTime creationDate = ts != null
                        ? ts.toInstant().atZone(ZoneId.systemDefault())
                        : null;

                Organization organization = organizationDAO.getByProductId(productId);

                Product product = new Product(
                        productId,
                        name,
                        new Coordinates(x, y),
                        price,
                        unit,
                        organization,
                        creationDate
                );

                // Добавляем владельца
                product.setOwnerId(rs.getInt("user_id"));

                products.add(product);
            }
        }
        return products;
    }



    public boolean deleteProductById(int productId, int userId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Получаем индекс удаляемого продукта
            Integer removedIndex = null;
            String getIndexSql = "SELECT position_index FROM products WHERE id = ? AND user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(getIndexSql)) {
                stmt.setInt(1, productId);
                stmt.setInt(2, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        removedIndex = rs.getInt("position_index");
                    } else {
                        return false; // нет такого продукта или он не принадлежит пользователю
                    }
                }
            }

            // Удаляем сам продукт
            String sql = "DELETE FROM products WHERE id = ? AND user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, productId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }

            // Сдвигаем все элементы ниже удалённого вверх на 1
            String shiftSql = "UPDATE products SET position_index = position_index - 1 WHERE position_index > ?";
            try (PreparedStatement stmt = connection.prepareStatement(shiftSql)) {
                stmt.setInt(1, removedIndex);
                stmt.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }


    public int clearUserProducts(int userId) throws SQLException {
        String sql = "DELETE FROM products WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate(); // возвращает количество удалённых строк
        }
    }

    public boolean updateProduct(Product product, int userId) throws SQLException {
        // Проверка принадлежности
        String checkSql = "SELECT user_id FROM products WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, product.getId());
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                return false; // продукта нет
            }
            if (rs.getInt("user_id") != userId) {
                return false; // не принадлежит пользователю
            }
        }

        // Обновление основного продукта
        String updateSql = """
        UPDATE products
        SET name = ?, x = ?, y = ?, price = ?, unit_of_measure = ?
        WHERE id = ?
    """;
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            updateStmt.setString(1, product.getName());
            updateStmt.setInt(2, product.getCoordinates().getX());
            updateStmt.setLong(3, product.getCoordinates().getY());
            if (product.getPrice() != null) {
                updateStmt.setFloat(4, product.getPrice());
            } else {
                updateStmt.setNull(4, Types.REAL);
            }
            if (product.getUnitOfMeasure() != null) {
                updateStmt.setString(5, product.getUnitOfMeasure().name());
            } else {
                updateStmt.setNull(5, Types.VARCHAR);
            }
            updateStmt.setInt(6, product.getId());

            updateStmt.executeUpdate();
        }

        // Обновление организации
        if (product.getManufacturer() != null) {
            organizationDAO.updateOrganization(product.getManufacturer(), product.getId());
        }

        return true;
    }

    public void updatePositionIndex(int productId, int newIndex) throws SQLException {
        String sql = "UPDATE products SET position_index = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newIndex);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }




}



