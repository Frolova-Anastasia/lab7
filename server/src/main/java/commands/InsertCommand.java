package commands;

import data.Product;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;
import utility.ProductFinalizer;

public class InsertCommand implements Command{
    private final CollectionManager collectionManager;

    public InsertCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "insert";
    }

    @Override
    public String getDescription() {
        return "добавить новый элемент по заданному индексу (insert {index})";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        String[] args = request.getArgs();
        Product product = request.getProduct();
        if (args == null || args.length != 1){
            return new ErrorResponse("Команда требует 1 аргумент - индекс коллекции, куда добавить новый элемент");
        }
        if (product == null){
            return new ErrorResponse("Продукт не передан");
        }
        try {
            int index = Integer.parseInt(args[0]);
            if (index < 0 || index > collectionManager.getProducts().size()) {
                return new ErrorResponse("Недопустимый индекс. Должен быть от 0 до " + collectionManager.getProducts().size());
            }
            ProductFinalizer.finalize(product);
            collectionManager.getProducts().add(index, product);
            collectionManager.save();
            return new SuccessResponse("Продукт успешно вставлен по индексу " + index);
        }catch (NumberFormatException e){
            return new ErrorResponse("Индекс должен быть целым числом от 0 до " + collectionManager.getProducts().size());
        }
    }
}
