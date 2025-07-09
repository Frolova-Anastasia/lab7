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

    public InfoCommand(CommandSender sender) {
        this.sender = sender;
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
            Request request = new Request();
            sender.send("info", request);
            return sender.receive();
        }catch (WrongNumberOfArgsException e){
           return new ErrorResponse("Эта команда принимает 0 аргументов");
        }catch (Exception e){
            return new ErrorResponse("Команда завершилась с ошибкой " + e.getMessage());
        }
    }
}
