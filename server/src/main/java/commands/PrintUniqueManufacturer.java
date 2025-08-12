package commands;

import data.Organization;
import data.Product;
import db.AuthManager;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PrintUniqueManufacturer implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;

    public PrintUniqueManufacturer(CollectionManager collectionManager, AuthManager authManager) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
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
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException, SQLException {
        if (!authManager.login(request.getUsername(), request.getPasswordHash())) {
            return new ErrorResponse("Ошибка авторизации.");
        }
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
