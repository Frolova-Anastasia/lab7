package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;

public class ShuffleCommand implements ClientCommand{
    private final CommandSender sender;
    private final String username;
    private final String passwordHash;

    public ShuffleCommand(CommandSender sender, String username, String passwordHash) {
        this.sender = sender;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "перемешивает обьекты коллекции в случайном порядке";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 0);
            Request request = new Request(username, passwordHash); // без аргументов
            sender.send("shuffle", request);
            return sender.receive();
        } catch (WrongNumberOfArgsException e) {
            return new ErrorResponse("Команда shuffle не принимает аргументов");
        } catch (Exception e) {
            return new ErrorResponse("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}
