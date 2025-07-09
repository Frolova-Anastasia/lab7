package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;

public class PrintUniqueManufacturer implements ClientCommand{
    private final CommandSender sender;

    public PrintUniqueManufacturer(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return "print_unique_manufacturer";
    }

    @Override
    public String getDescription() {
        return "вывести уникальные значения поля manufacturer всех элементов";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 0);
            Request request = new Request();
            sender.send("print_unique_manufacturer", request);
            return sender.receive();
        }catch (WrongNumberOfArgsException e){
            return new ErrorResponse(e.getMessage());
        }catch (Exception e){
            return new ErrorResponse("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}
