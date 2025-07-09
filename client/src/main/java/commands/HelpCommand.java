package commands;

import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;
import utility.CommandSender;

import java.io.IOException;


public class HelpCommand implements ClientCommand {
    private final CommandSender sender;

    public HelpCommand(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "- вывести справку по командам";
    }

    @Override
    public Response execute(String[] args) throws IOException, ClassNotFoundException, WrongNumberOfArgsException {
        NumberArgsChecker.checkArgs(args, 0);
        Request request = new Request();
        sender.send("help", request);
        return sender.receive();
    }
}
