package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.util.Collections;

public class ShuffleCommand implements Command{
    private final CollectionManager collectionManager;

    public ShuffleCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "перемешивает элементы коллекции в случайном порядке";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        Collections.shuffle(collectionManager.getProducts());
        collectionManager.save();
        return new SuccessResponse("Коллекция успешно перемешана");
    }
}
