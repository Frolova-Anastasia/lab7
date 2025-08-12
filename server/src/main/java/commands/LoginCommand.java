package commands;

import db.AuthManager;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;

import java.sql.SQLException;

public class LoginCommand implements Command{
    private final AuthManager authManager;

    public LoginCommand(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getDescription() {
        return "вход в систему";
    }

    @Override
    public Response execute(Request request) {
        try {
            boolean success = authManager.login(request.getUsername(), request.getPasswordHash());
            if (success) {
                return new SuccessResponse("Успешный вход");
            } else {
                return new ErrorResponse("Если вы не зарегистрированы: Неверный логин или пароль");
            }
        } catch (SQLException e) {
            return new ErrorResponse("Ошибка базы данных: " + e.getMessage());
        }
    }
}
