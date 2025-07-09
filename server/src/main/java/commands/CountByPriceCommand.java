package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.util.Objects;

public class CountByPriceCommand implements Command{
    private final CollectionManager collectionManager;

    public CountByPriceCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
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
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
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
