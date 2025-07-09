package commands;

import exceptions.WrongNumberOfArgsException;
import responses.Response;

public class ExitCommand implements ClientCommand{
    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "завершение работы";
    }

    @Override
    public Response execute(String[] args) throws WrongNumberOfArgsException {
        NumberArgsChecker.checkArgs(args, 0);
        System.out.println("Завершение работы..");
        System.exit(0);
        return null;
    }
}
