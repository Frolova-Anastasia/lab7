package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;

public class InfoCommand implements ClientCommand{
    private final CommandSender sender;
    private final String username;
    private final String passwordHash;

    public InfoCommand(CommandSender sender, String username, String passwordHash) {
        this.sender = sender;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "выводит информацию о коллеции";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 0);
            Request request = new Request(username, passwordHash);
            sender.send("info", request);
            return sender.receive();
        }catch (WrongNumberOfArgsException e){
           return new ErrorResponse("Эта команда принимает 0 аргументов");
        }catch (Exception e){
            return new ErrorResponse("Команда завершилась с ошибкой " + e.getMessage());
        }
    }
}
