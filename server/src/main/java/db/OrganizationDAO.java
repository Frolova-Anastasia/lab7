package db;

import data.Organization;
import data.OrganizationType;

import java.sql.*;

public class OrganizationDAO {
    private final Connection connection;

    public OrganizationDAO(Connection connection) {
        this.connection = connection;
    }

    public int insertOrganization(Organization org, int productId) throws SQLException {
        String sql = """
            INSERT INTO organizations (name, full_name, type, product_id)
            VALUES (?, ?, ?, ?) RETURNING id
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, org.getName());
            stmt.setString(2, org.getFullName());
            stmt.setString(3, org.getType() != null ? org.getType().name() : null);
            stmt.setInt(4, productId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Не удалось вставить организацию.");
        }
    }

    public Organization getByProductId(int productId) throws SQLException {
        String sql = "SELECT * FROM organizations WHERE product_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Organization(
                        rs.getString("name"),
                        rs.getString("full_name"),
                        rs.getString("type") != null ? OrganizationType.valueOf(rs.getString("type")) : null,
                        rs.getInt("id")
                );
            } else {
                return null;
            }
        }
    }

    public void updateOrganization(Organization org, int productId) throws SQLException {
        String sql = """
        UPDATE organizations
        SET name = ?, full_name = ?, type = ?
        WHERE product_id = ?
    """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, org.getName());
            stmt.setString(2, org.getFullName());
            if (org.getType() != null) {
                stmt.setString(3, org.getType().name());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            stmt.setInt(4, productId);
            stmt.executeUpdate();
        }
    }

}
