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

public class UpdateCommand implements ClientCommand{
    private final CommandSender sender;
    private final ProductBuilder builder;
    private final String username;
    private final String passwordHash;

    public UpdateCommand(CommandSender sender, ProductBuilder builder, String username, String passwordHash) {
        this.sender = sender;
        this.builder = builder;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции по id (update {id})";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 1);
            int id = Integer.parseInt(args[0]);
            if (id <= 0){
                return new ErrorResponse("ID должен быть целым положительным числом");
            }

            System.out.println("Введите новые данные для продукта с ID " + id + ":");
            Product newProduct = builder.builProduct();

            Request request = new Request(username, passwordHash, args);
            request.setProduct(newProduct);

            sender.send("update", request);
            return sender.receive();
        }catch (WrongNumberOfArgsException e){
            return new ErrorResponse("Команда принимает 1 аргумент - id продукта, который необходимо обновить");
        }catch (NumberFormatException e){
            return new ErrorResponse("Аргумент должен быть целым числом");
        }catch (EndInputException e){
            return new ErrorResponse("Ввод продукта прерван");
        }catch (Exception e){
            return new ErrorResponse("Ошибка " + e.getMessage());
        }
    }
}
