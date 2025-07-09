package commands;

import data.Organization;
import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PrintUniqueManufacturer implements Command{
    private final CollectionManager collectionManager;

    public PrintUniqueManufacturer(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "print_unique_manufacturer";
    }

    @Override
    public String getDescription() {
        return "вывести уникальные значения поля manufacturer всех элементов в коллекции";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        Set<Organization> uniqueMan = collectionManager.getProducts().stream()
                .map(Product::getManufacturer)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if(uniqueMan.isEmpty()){
            return new SuccessResponse("Уникальные значения manufacturer отсутствуют");
        }
        StringBuilder sb = new StringBuilder("Уникальные значения manufacturer: \n");
        for(Organization org : uniqueMan){
            sb.append(org).append("\n");
        }
        return new SuccessResponse(sb.toString());
    }
}
