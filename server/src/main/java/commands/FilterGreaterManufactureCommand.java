package commands;

import data.Organization;
import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.util.List;

public class FilterGreaterManufactureCommand implements Command{
    private final CollectionManager collectionManager;

    public FilterGreaterManufactureCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "filter_greater_than_manufacturer";
    }

    @Override
    public String getDescription() {
        return "вывести элементы, значение поля manufacturer которых больше заданного";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        Organization target = request.getOrganization();
        if(target == null){
            return new ErrorResponse("организация не передана");
        }
        List<Product> filtered = collectionManager.getProducts().stream()
                .filter(product -> product.getManufacturer() != null)
                .filter(product -> product.getManufacturer().compareTo(target) > 0)
                .toList();
        if(filtered.isEmpty()){
            return new SuccessResponse("Нет manufacturer больше заданного");
        }
        StringBuilder sb = new StringBuilder("Manufacturer больше заданного: \n");
        for(Product product : filtered){
            sb.append(product).append("\n");
        }
        return new SuccessResponse(sb.toString());
    }
}
