package commands;

import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;

public class ShowCommand implements ClientCommand{
    private final CommandSender sender;

    public ShowCommand(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "выводит содержимое коллекции";
    }

    @Override
    public Response execute(String[] args) throws IOException, ClassNotFoundException, WrongNumberOfArgsException {
        NumberArgsChecker.checkArgs(args, 0);
        Request request = new Request();
        sender.send("show", request);
        return sender.receive();
    }
}
