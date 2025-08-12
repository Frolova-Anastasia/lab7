package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;

public class CountByPriceCommand implements ClientCommand{
    private final CommandSender sender;
    private final String username;
    private final String passwordHash;

    public CountByPriceCommand(CommandSender sender, String username, String passwordHash) {
        this.sender = sender;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return "count_by_price";
    }

    @Override
    public String getDescription() {
        return "выводит количество элементов, значение поля price которых равно заданному";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args,1);
            Float.parseFloat(args[0]);
            Request request = new Request(username, passwordHash, args);
            sender.send("count_by_price", request);
            return sender.receive();
        }catch (WrongNumberOfArgsException e) {
            return new ErrorResponse("Команда требует один аргумент — цену.");
        } catch (NumberFormatException e) {
            return new ErrorResponse("Введите в качестве аргумента корректное вещественное число.");
        } catch (Exception e) {
            return new ErrorResponse("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}
