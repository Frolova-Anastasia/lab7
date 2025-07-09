package commands;

import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;
import utility.IdManager;
import utility.ProductFinalizer;

import java.time.ZonedDateTime;

public class AddCommand implements Command{
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "добавить новый товар";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        Product product = request.getProduct();
        if(product == null){
            return new ErrorResponse("Продукт не передан");
        }
        ProductFinalizer.finalize(product);
        collectionManager.add(product);
        collectionManager.save();
        return new SuccessResponse("Продукт добавлен с ID = " + product.getId());
    }
}
