package commands;

import data.Organization;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CommandSender;
import utility.ProductBuilder;

import java.io.IOException;

public class FilterGreaterManufacturerCommand implements ClientCommand{
    private final CommandSender sender;
    private final ProductBuilder builder;

    public FilterGreaterManufacturerCommand(CommandSender sender, ProductBuilder builder) {
        this.sender = sender;
        this.builder = builder;
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
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
      try {
          NumberArgsChecker.checkArgs(args, 0);
          Organization org = builder.buildOrganization();
          Request request = new Request();
          request.setOrganization(org);
          sender.send("filter_greater_than_manufacturer", request);
          return sender.receive();
      }catch (WrongNumberOfArgsException e){
          return new ErrorResponse("Команда не принимает аргументов. Ввод полей предложен отдельно");
      }catch (EndInputException e){
          return new ErrorResponse("Ввод был прерван");
      }catch (Exception e){
          return new ErrorResponse("Ошибка при выполнении команды: " + e.getMessage());
      }
    }
}
