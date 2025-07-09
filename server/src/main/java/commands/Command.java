package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;

import java.io.Serializable;

public interface Command extends Serializable {
    String getName();
    String getDescription();
    Response execute(Request request) throws WrongNumberOfArgsException, EndInputException;
}
