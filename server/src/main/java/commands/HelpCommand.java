package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.Response;
import responses.SuccessResponse;

import java.util.Map;

public class HelpCommand implements Command{
    private final Map<String, Command> commands;

    public HelpCommand(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "вывести справку по доступным командам";
    }

    @Override
    public Response execute(Request request) throws WrongNumberOfArgsException, EndInputException {
        StringBuilder responseText = new StringBuilder();
        for(Command c : commands.values()){
            responseText.append(c.getName()).append(" - ").append(c.getDescription()).append("\n");
        }
        return new SuccessResponse(responseText.append("exit - завершение работы \n").append("execute_script - выполнить команды из указанного файла").toString());
    }
}
