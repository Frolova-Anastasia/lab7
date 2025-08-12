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

public class LoginCommand implements ClientCommand{
    private final CommandSender sender;

    public LoginCommand(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getDescription() {
        return "войти в систему";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        if (args.length != 2) return new ErrorResponse("Использование: login <username> <password>");
        String username = args[0];
        String password = args[1];

        String passwordHash = PasswordHasher.hash(password);


        // Создаём запрос
        Request request = new Request(username, passwordHash, args);

        // Отправляем на сервер
        sender.send("login", request);
        Response response = sender.receive();

        // Если логин успешен — сохраняем в сессию
        if (response instanceof SuccessResponse) {
            UserSession.authorize(username, passwordHash);
        }

        return response;
    }
}
