package commands;

import data.Product;
import db.AuthManager;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class ShuffleCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;

    public ShuffleCommand(CollectionManager collectionManager, AuthManager authManager) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "перемешивает элементы коллекции в случайном порядке";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException, SQLException {
        String username = request.getUsername();
        String passwordHash = request.getPasswordHash();

        if (!authManager.login(username, passwordHash)) {
            return new ErrorResponse("Ошибка авторизации: неверный логин или пароль.");
        }
        // Перемешивание коллекции в памяти
        List<Product> products = collectionManager.getProducts();
        Collections.shuffle(products);

        // Обновляем порядок в БД
        collectionManager.updatePositionsInDB();

        collectionManager.reload();

        return new SuccessResponse("Коллекция успешно перемешана");
    }
}
