package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CommandSender;
import utility.PasswordHasher;
import utility.UserSession;

import java.io.IOException;

public class RegisterCommand implements ClientCommand{
    private final CommandSender sender;

    public RegisterCommand(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "регистрация пользователя в системе";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        if (args.length != 2) return new ErrorResponse("Использование: register <username> <password>");
        String username = args[0];
        String password = args[1];

        String passwordHash = PasswordHasher.hash(password);

        // Создаём запрос
        Request request = new Request(username, passwordHash, args);

        // Отправляем на сервер
        sender.send("register", request);
        Response response = sender.receive();

        // Если логин успешен — сохраняем в сессию
        if (response instanceof SuccessResponse) {
            UserSession.authorize(username, passwordHash);
        }

        return response;
    }
}
