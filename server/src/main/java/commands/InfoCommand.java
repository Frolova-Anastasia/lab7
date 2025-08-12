package commands;

import db.AuthManager;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.sql.SQLException;

public class InfoCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;

    public InfoCommand(CollectionManager collectionManager, AuthManager authManager) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "показывает информацию о коллекции";
    }

    @Override
    public Response execute(Request request) throws SQLException {
        if (!authManager.login(request.getUsername(), request.getPasswordHash())) {
            return new ErrorResponse("Ошибка авторизации.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Тип коллекции: ").append(collectionManager.getCollectionType()).append("\n");
        sb.append("Количество элементов: ").append(collectionManager.getProducts().size()).append("\n");
        sb.append("Время инициализации: ").append(collectionManager.getInitTime()).append("\n");
        return new SuccessResponse(sb.toString());
    }
}
