package commands;



import db.AuthManager;
import db.ProductDAO;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.sql.SQLException;

public class ClearCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;
    private final ProductDAO productDAO;

    public ClearCommand(CollectionManager collectionManager, AuthManager authManager, ProductDAO productDAO) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
        this.productDAO = productDAO;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очищение коллекции";
    }



    @Override
    public Response execute(Request request) throws SQLException {
        try {
            String username = request.getUsername();
            String passwordHash = request.getPasswordHash();

            // Проверка авторизации
            if (!authManager.login(username, passwordHash)) {
                return new ErrorResponse("Ошибка авторизации: неверные логин или пароль.");
            }

            int userId = authManager.getUserId(username);

            // Удаляем все продукты пользователя из БД
            productDAO.clearUserProducts(userId);

            // Обновляем коллекцию в памяти
            collectionManager.reload();

            return new SuccessResponse("Все ваши объекты были удалены из коллекции.");

        } catch (SQLException e) {
            return new ErrorResponse("Ошибка при удалении объектов: " + e.getMessage());
        } catch (Exception e) {
            return new ErrorResponse("Ошибка: " + e.getMessage());
        }
    }
}
