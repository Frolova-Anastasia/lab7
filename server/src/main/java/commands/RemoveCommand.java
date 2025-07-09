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

public class RemoveCommand implements Command{
    private final CollectionManager collectionManager;

    public RemoveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "удалить элемент коллекции по его id (remove {id})";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        String[] args = request.getArgs();
        if (args == null || args.length != 1) {
            return new ErrorResponse("Команда требует один аргумент — id продукта");
        }

        try {
            int id = Integer.parseInt(args[0]);
            if (id <= 0) {
                return new ErrorResponse("ID должен быть положительным числом");
            }

            List<Product> products = collectionManager.getProducts();
            Product toRemove = products.stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (toRemove == null) {
                return new ErrorResponse("Продукт с ID " + id + " не найден");
            }

            products.remove(toRemove);
            collectionManager.save();
            return new SuccessResponse("Продукт с ID " + id + " успешно удалён");

        } catch (NumberFormatException e) {
            return new ErrorResponse("ID должен быть целым числом");
        }
    }
}

