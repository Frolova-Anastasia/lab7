package commands;

import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;
import input.ProductBuilder;

import java.io.IOException;

public class RemoveGreaterCommand implements ClientCommand{
    private final CommandSender sender;
    private final ProductBuilder builder;
    private final String username;
    private final String passwordHash;

    public RemoveGreaterCommand(CommandSender sender, ProductBuilder builder, String username, String passwordHash) {
        this.sender = sender;
        this.builder = builder;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return "remove_greater";
    }

    @Override
    public String getDescription() {
        return "удалить из коллекции все элементы, превышающие заданный";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 0);
            Product product = builder.builProduct();

            Request request = new Request(username, passwordHash);
            request.setProduct(product);

            sender.send("remove_greater", request);
            return sender.receive();

        } catch (WrongNumberOfArgsException e) {
            return new ErrorResponse("Команда не принимает аргументов");
        } catch (EndInputException e) {
            return new ErrorResponse("Ввод продукта прерван");
        } catch (Exception e) {
            return new ErrorResponse("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}
