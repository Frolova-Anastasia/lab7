package commands;

import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;
import utility.CommandSender;
import utility.ProductBuilder;

import java.io.IOException;


public class AddCommand implements ClientCommand {
    private final CommandSender sender;
    private final ProductBuilder builder;

    public AddCommand(CommandSender sender, ProductBuilder builder) {
        this.sender = sender;
        this.builder = builder;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавление нового продукта в коллекцию";
    }

    @Override
    public Response execute(String[] args) throws IOException, ClassNotFoundException, EndInputException, WrongNumberOfArgsException {
        NumberArgsChecker.checkArgs(args, 0);
        Product product = builder.builProduct();
        Request request = new Request();
        request.setProduct(product);
        sender.send("add", request);
        return sender.receive();
    }
}
