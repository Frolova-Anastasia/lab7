package commands;

import data.Product;
import db.AuthManager;
import db.ProductDAO;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;
import java.time.ZonedDateTime;


public class AddCommand implements Command {
    private final CollectionManager collectionManager;
    private final AuthManager authManager;
    private final ProductDAO productDAO;

    public AddCommand(CollectionManager collectionManager, AuthManager authManager, ProductDAO productDAO) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
        this.productDAO = productDAO;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавить новый товар";
    }

    @Override
    public Response execute(Request request) {
        try {
            String username = request.getUsername();
            String passwordHash = request.getPasswordHash();

            if (!authManager.login(username, passwordHash)) {
                return new ErrorResponse("Ошибка авторизации: неверный логин или пароль.");
            }

            Product product = request.getProduct();
            if (product == null) {
                return new ErrorResponse("Продукт не передан.");
            }


            int userId = authManager.getUserId(username);

            // Устанавливаем дату создания на стороне сервера
            product.setCreationDate(ZonedDateTime.now());

            // Добавляем в коллекцию (и в БД внутри метода)
            boolean inserted = collectionManager.add(product, userId);
            if (!inserted) {
                return new ErrorResponse("Не удалось добавить продукт.");
            }

            return new SuccessResponse("Продукт добавлен.");

        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorResponse("Ошибка добавления: " + e.getMessage());
        }
    }
}

