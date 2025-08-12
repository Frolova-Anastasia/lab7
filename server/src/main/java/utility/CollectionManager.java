package utility;

import data.Product;
import db.ProductDAO;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CollectionManager {
    private final ProductDAO productDAO;
    private final List<Product> products;
    private final ZonedDateTime initTime;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public CollectionManager(ProductDAO productDAO) {
        this.productDAO = productDAO;
        this.products = new LinkedList<>();
        this.initTime = ZonedDateTime.now();
        loadFromDatabase();
    }

    public List<Product> getProducts() {
        lock.readLock().lock();
        try {
            return new LinkedList<>(products); // возвращаем копию
        } finally {
            lock.readLock().unlock();
        }
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public ZonedDateTime getInitTime() {
        return initTime;
    }

    public String getCollectionType() {
        return products.getClass().getSimpleName();
    }

    public boolean add(Product product, int userId) throws SQLException {
        boolean inserted = productDAO.insertProduct(product, userId, null);
        if (inserted) {
            reload();
        }
        return inserted;
    }

    public boolean insertAtPosition(Product product, int userId, int index) throws SQLException {
        // Вставляем продукт в БД на нужную позицию
        boolean inserted = productDAO.insertProduct(product, userId, index);
        if (inserted) {
            reload(); // Перезагружаем коллекцию из БД, чтобы в памяти порядок совпадал
        }
        return inserted;
    }



    public void updatePositionsInDB() throws SQLException {
        for (int i = 0; i < products.size(); i++) {
            productDAO.updatePositionIndex(products.get(i).getId(), i);
        }
    }


    public void reload() {
        lock.writeLock().lock();
        try {
            List<Product> freshProducts = productDAO.getAllProducts(); // всегда новый список
            products.clear();
            products.addAll(freshProducts);
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке коллекции из БД: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void loadFromDatabase() {
        reload();
        System.out.println("Коллекция загружена из базы данных.");
    }
    
}

