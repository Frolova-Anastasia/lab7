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
import java.time.ZonedDateTime;

public class InsertCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;
    private final ProductDAO productDAO;

    public InsertCommand(CollectionManager collectionManager, AuthManager authManager, ProductDAO productDAO) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
        this.productDAO = productDAO;
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент по заданному индексу (insert {index})";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException, SQLException {
        try {
            String username = request.getUsername();
            String passwordHash = request.getPasswordHash();


            if (!authManager.login(username, passwordHash)) {
                return new ErrorResponse("Ошибка авторизации: неверный логин или пароль.");
            }

            String[] args = request.getArgs();
            Product product = request.getProduct();


            if (args == null || args.length != 1) {
                return new ErrorResponse("Команда требует 1 аргумент — индекс позиции для вставки");
            }
            if (product == null) {
                return new ErrorResponse("Продукт не передан");
            }

            int index;
            try {
                index = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return new ErrorResponse("Индекс должен быть целым числом");
            }

            // Проверка диапазона индекса
            if (index < 0 || index > collectionManager.getProducts().size()) {
                return new ErrorResponse("Недопустимый индекс. Должен быть от 0 до " + collectionManager.getProducts().size());
            }

            int userId = authManager.getUserId(username);


            product.setCreationDate(ZonedDateTime.now());


            boolean success = collectionManager.insertAtPosition(product, userId, index);
            if (success) {
                return new SuccessResponse("Продукт успешно вставлен по индексу " + index);
            } else {
                return new ErrorResponse("Не удалось вставить продукт");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка работы с базой данных: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка: " + e.getMessage());
        }
    }
}
