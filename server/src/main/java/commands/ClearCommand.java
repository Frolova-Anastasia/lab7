package commands;

import requests.Request;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

public class ClearCommand implements Command{
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "очищение коллекции";
    }

    @Override
    public Response execute(Request request) {
        collectionManager.clear();
        return new SuccessResponse("Коллекция очищена");
    }
}
