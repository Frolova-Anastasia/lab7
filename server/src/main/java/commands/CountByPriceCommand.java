package commands;

import db.AuthManager;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;


import java.sql.SQLException;
import java.util.Objects;

public class CountByPriceCommand implements Command{
    private final CollectionManager collectionManager;
    private final AuthManager authManager;

    public CountByPriceCommand(CollectionManager collectionManager, AuthManager authManager) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
    }

    @Override
    public String getName() {
        return "count_by_price";
    }

    @Override
    public String getDescription() {
        return "выводит количество элементов, значение поля price которых равно заданному (count_by_price {price})";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException, SQLException {
        if (!authManager.login(request.getUsername(), request.getPasswordHash())) {
            return new ErrorResponse("Ошибка авторизации.");
        }
        String[] args = request.getArgs();
        if(args == null || args.length != 1){
            return new ErrorResponse("Команда требует один аргуиент - цену (вещественное число через точку");
        }
        try{
            Float price = Float.parseFloat(args[0]);
            if (price < 0){
                return new ErrorResponse("цены должна быть положительной");
            }
            long count = collectionManager.getProducts().stream()
                    .filter(product -> Objects.equals(product.getPrice(), price))
                    .count();
            return new SuccessResponse("Количество элементов с ценой " + price + " равно " + count);
        }catch (NumberFormatException e){
            return new ErrorResponse("Некорректный формат числа");
        }
    }
}
