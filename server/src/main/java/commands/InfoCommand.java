package commands;

import requests.Request;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

public class InfoCommand implements Command{
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "показывает информацию о коллекции";
    }

    @Override
    public Response execute(Request request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип коллекции: ").append(collectionManager.getCollectionType()).append("\n");
        sb.append("Количество элементов: ").append(collectionManager.getProducts().size()).append("\n");
        sb.append("Время инициализации: ").append(collectionManager.getInitTime()).append("\n");
        return new SuccessResponse(sb.toString());
    }
}
