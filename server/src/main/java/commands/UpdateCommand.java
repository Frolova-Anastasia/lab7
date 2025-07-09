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

public class UpdateCommand implements Command{
    private final CollectionManager collectionManager;

    public UpdateCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "обновить значение элемента коллекции по id (update {id})";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        String[] args = request.getArgs();
        Product newProduct = request.getProduct();

        if (args == null || args.length != 1){
            return new ErrorResponse("Команда принимает 1 аргумент - id продукта, который необходимо обновить");
        }

        try {
            int id = Integer.parseInt(args[0]);

            Product exist = collectionManager.getProducts().stream()
                    .filter(product -> product.getId() == id)
                    .findFirst()
                    .orElse(null);

            if(exist == null){
                return new ErrorResponse("Продукт с ID " + id + " не найден");
            }

            newProduct.setId(id);
            newProduct.setCreationDate(exist.getCreationDate());

            collectionManager.getProducts().remove(exist);
            collectionManager.getProducts().add(newProduct);
            collectionManager.save();

            return new SuccessResponse("Продукт с ID " + id + " успешно обновлен");

        }catch (NumberFormatException e){
            return new ErrorResponse("Аргумент должен быть целым числом");
        }catch (UnsupportedOperationException e){
            return new ErrorResponse("Ошибка при установке ID: " + e.getMessage());
        }
    }
}
