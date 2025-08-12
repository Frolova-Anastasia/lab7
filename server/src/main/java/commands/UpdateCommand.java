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

public class UpdateCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;
    private final ProductDAO productDAO;

    public UpdateCommand(CollectionManager collectionManager, AuthManager authManager, ProductDAO productDAO) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
        this.productDAO = productDAO;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции по id (update {id})";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        try {
            String username = request.getUsername();
            String passwordHash = request.getPasswordHash();

            // Авторизация
            if (!authManager.login(username, passwordHash)) {
                return new ErrorResponse("Ошибка авторизации: неверный логин или пароль.");
            }

            String[] args = request.getArgs();
            Product newProduct = request.getProduct();

            // Проверка аргументов
            if (args == null || args.length != 1) {
                return new ErrorResponse("Команда принимает 1 аргумент — id продукта, который нужно обновить.");
            }

            int id;
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return new ErrorResponse("ID должен быть целым числом.");
            }

            // Поиск существующего продукта в памяти
            Product existing = collectionManager.getProducts().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                return new ErrorResponse("Продукт с ID " + id + " не найден.");
            }

            int userId = authManager.getUserId(username);

            // Проверка прав
            if (existing.getOwnerId() != userId) {
                return new ErrorResponse("Вы не можете обновлять чужой продукт.");
            }

            // Перенос неизменяемых полей
            newProduct.setId(id);
            newProduct.setCreationDate(existing.getCreationDate());

            //  Обновляем в БД
            boolean updated = productDAO.updateProduct(newProduct, userId);
            if (!updated) {
                return new ErrorResponse("Не удалось обновить продукт в базе данных.");
            }

            // Обновляем в памяти
            collectionManager.reload(); // Перезагружаем коллекцию из БД

            return new SuccessResponse("Продукт с ID " + id + " успешно обновлён.");

        } catch (SQLException e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка при работе с базой данных: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка: " + e.getMessage());
        }
    }
}
