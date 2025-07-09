package commands;

import data.Product;
import data.Products;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;
import responses.SuccessResponse;
import utility.CollectionManager;

import java.util.List;

public class ShowCommand implements Command{
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "вывести все элементы коллекции";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        List<Product> products = collectionManager.getProducts();
        if(products.isEmpty()){
            return new SuccessResponse("Коллекция пуста");
        }
        StringBuilder sb = new StringBuilder("Содержимое коллекции:\n");
        for(Product p : products){
            String productStr = p.toString() + "\n";
            if (sb.length() + productStr.length() > 3000) {
                sb.append("... (вывод обрезан, превышен лимит в 3000 символов)\n");
                break;
            }
            sb.append(productStr);
        }
        return new SuccessResponse(sb.toString());
    }
}
