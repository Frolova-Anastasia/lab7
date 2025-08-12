package commands;

import data.Product;
import db.AuthManager;
import db.ProductDAO;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.sql.SQLException;
import java.util.List;

public class RemoveGreaterCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;
    private final ProductDAO productDAO;

    public RemoveGreaterCommand(CollectionManager collectionManager, AuthManager authManager, ProductDAO productDAO) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
        this.productDAO = productDAO;
    }

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, превышающие заданный";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException, SQLException {
        try {
            String username = request.getUsername();
            String passwordHash = request.getPasswordHash();

            // Авторизация
            if (!authManager.login(username, passwordHash)) {
                return new ErrorResponse("Ошибка авторизации: неверный логин или пароль.");
            }

            int userId = authManager.getUserId(username);

            Product product = request.getProduct();
            if (product == null) {
                return new ErrorResponse("Продукт не передан.");
            }

            // Находим все продукты пользователя, которые больше переданного
            List<Product> toRemove = collectionManager.getProducts().stream()
                    .filter(p -> p.getOwnerId() == userId)
                    .filter(p -> p.compareTo(product) > 0)
                    .toList();

            if (toRemove.isEmpty()) {
                return new SuccessResponse("Нет элементов, превышающих заданный.");
            }

            // Удаляем из БД
            int removedCount = 0;
            for (Product p : toRemove) {
                if (productDAO.deleteProductById(p.getId(), userId)) {
                    removedCount++;
                }
            }

            // Обновляем коллекцию в памяти
            collectionManager.reload();

            return new SuccessResponse("Удалено элементов: " + removedCount);

        } catch (SQLException e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка при работе с базой данных: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка: " + e.getMessage());
        }
    }
}
