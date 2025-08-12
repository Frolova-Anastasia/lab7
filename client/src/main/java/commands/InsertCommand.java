package commands;

import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import input.ProductBuilder;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;

public class InsertCommand implements ClientCommand{
    private final CommandSender sender;
    private final ProductBuilder builder;
    private final String username;
    private final String passwordHash;

    public InsertCommand(CommandSender sender, ProductBuilder builder, String username, String passwordHash) {
        this.sender = sender;
        this.builder = builder;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент по индексу";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 1);
            int index = Integer.parseInt(args[0]);
            if (index < 0){
                return new ErrorResponse("Индекс должен быть целым неотрицательным числом");
            }
            Product product = builder.builProduct();

            Request request = new Request(username, passwordHash, args);
            request.setProduct(product);
            sender.send("insert", request);
            return sender.receive();
        }catch (WrongNumberOfArgsException e){
            return new ErrorResponse("Команда требует 1 аргумент");
        }catch (NumberFormatException e){
            return new ErrorResponse("Аргумент должен быть целым неотрицательным числом");
        }catch (EndInputException e){
            return new ErrorResponse("Ввод прерван");
        }catch (Exception e){
            return new ErrorResponse("Ошибка при выполнении команды: " + e.getMessage());
        }
    }
}
