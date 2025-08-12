package commands;

import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import responses.ErrorResponse;
import responses.Response;
import responses.SuccessResponse;
import input.ClientConsole;
import utility.CommandManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ExecuteScriptCommand implements ClientCommand{
    private final CommandManager commandManager;
    private final ClientConsole console;
    private final Set<Path> activeScripts = new HashSet<>();


    public ExecuteScriptCommand(CommandManager commandManager, ClientConsole console) {
        this.commandManager = commandManager;
        this.console = console;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getDescription() {
        return "выполнить команды из указанного файла";
    }

    @Override
    public Response execute(String[] args) throws EndInputException, IOException, ClassNotFoundException, WrongNumberOfArgsException {
        try {
            NumberArgsChecker.checkArgs(args, 1);
            Path scriptPath = Path.of(args[0]).toAbsolutePath().normalize();

            if (!Files.exists(scriptPath)) {
                return new ErrorResponse("Файл не найден: " + scriptPath);
            }

            if (activeScripts.contains(scriptPath)) {
                return new ErrorResponse("Обнаружена рекурсия: " + scriptPath);
            }

            activeScripts.add(scriptPath);
            console.setScriptInputMode(scriptPath);     // переключение на файл
            console.runScript();                         // просто запускаем как обычный ввод

            return new SuccessResponse("Скрипт выполнен: " + scriptPath);
        } catch (IOException e) {
            return new ErrorResponse("Ошибка чтения файла: " + e.getMessage());
        } catch (WrongNumberOfArgsException e) {
            return new ErrorResponse("Неверное количество аргументов");
        } finally {
            activeScripts.clear();
        }
    }
}
