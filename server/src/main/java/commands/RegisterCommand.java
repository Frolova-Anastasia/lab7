package commands;

import db.AuthManager;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;

import java.sql.SQLException;

public class RegisterCommand implements Command{
    private final AuthManager authManager;

    public RegisterCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "регистрация в системе";
    }

    @Override
    public Response execute(Request request) {
        try {
            boolean success = authManager.register(request.getUsername(), request.getPasswordHash());
            if (success) {
                return new SuccessResponse("Успешная регистрация");
            } else {
                return new ErrorResponse("Пользователь с таким именем уже существует");
            }
        } catch (SQLException e) {
            return new ErrorResponse("Ошибка базы данных: " + e.getMessage());
        }
    }
}
