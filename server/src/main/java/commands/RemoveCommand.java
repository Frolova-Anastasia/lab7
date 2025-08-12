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

public class RemoveCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;
    private final ProductDAO productDAO;

    public RemoveCommand(CollectionManager collectionManager, AuthManager authManager, ProductDAO productDAO) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
        this.productDAO = productDAO;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "удалить элемент коллекции по его id (remove {id})";
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
            if (args == null || args.length != 1) {
                return new ErrorResponse("Команда требует один аргумент — id продукта.");
            }

            int id;
            try {
                id = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                return new ErrorResponse("ID должен быть целым числом.");
            }

            if (id <= 0) {
                return new ErrorResponse("ID должен быть положительным числом.");
            }

            // Проверка существования продукта в памяти
            Product toRemove = collectionManager.getProducts().stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (toRemove == null) {
                return new ErrorResponse("Продукт с ID " + id + " не найден.");
            }

            int userId = authManager.getUserId(username);

            // Проверка прав
            if (toRemove.getOwnerId() != userId) {
                return new ErrorResponse("Вы не можете удалить продукт, который вам не принадлежит.");
            }


            boolean deleted = productDAO.deleteProductById(id, userId);
            if (!deleted) {
                return new ErrorResponse("Не удалось удалить продукт из базы данных.");
            }

            collectionManager.reload();

            return new SuccessResponse("Продукт с ID " + id + " успешно удалён.");

        } catch (SQLException e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка при работе с базой данных: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка: " + e.getMessage());
        }
    }
}

