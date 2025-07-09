package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import responses.Response;

import java.io.IOException;

public interface ClientCommand {
    String getName();
    String getDescription();
    Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException;

}
