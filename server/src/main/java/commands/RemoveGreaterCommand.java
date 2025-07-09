package commands;

import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.util.List;

public class RemoveGreaterCommand implements Command{
    private final CollectionManager collectionManager;

    public RemoveGreaterCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
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
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        Product product = request.getProduct();
        if (product == null) {
            return new ErrorResponse("Продукт не передан.");
        }

        List<Product> products = collectionManager.getProducts();
        int originalSize = products.size();

        products.removeIf(p -> p.compareTo(product) > 0);
        int removedCount = originalSize - products.size();

        collectionManager.save();

        return new SuccessResponse("Удалено элементов: " + removedCount);
    }
}
