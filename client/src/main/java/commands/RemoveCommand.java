package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;

public class RemoveCommand implements ClientCommand{
    private final CommandSender sender;
    private final String username;
    private final String passwordHash;

    public RemoveCommand(CommandSender sender, String username, String passwordHash) {
        this.sender = sender;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "удалить продукт по его id (remove {id})";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 1); // должен быть один аргумент
            int id = Integer.parseInt(args[0]);
            if (id <= 0) {
                return new ErrorResponse("ID должен быть положительным числом");
            }

            Request request = new Request(username, passwordHash, args);
            sender.send("remove", request);
            return sender.receive();

        } catch (WrongNumberOfArgsException e) {
            return new ErrorResponse("Команда требует один аргумент: id");
        } catch (NumberFormatException e) {
            return new ErrorResponse("ID должен быть целым числом.");
        } catch (Exception e) {
            return new ErrorResponse("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}
