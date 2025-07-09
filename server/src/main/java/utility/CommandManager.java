package utility;

import commands.*;

import java.util.*;
/**
 * Менеджер серверных команд. Хранит, регистрирует и предоставляет доступ к реализациям команд.
 */
public class CommandManager {
    public final Map<String, Command> commands = new HashMap<>();
    private final CollectionManager collectionManager;

    public CommandManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        registerAll();
    }

    /**
     * Регистрирует все доступные команды в рамках текущей серверной архитектуры.
     */
    private void registerAll(){
        registerCommand(new HelpCommand(commands));
        registerCommand(new AddCommand(collectionManager));
        registerCommand(new ShowCommand(collectionManager));
        registerCommand(new ClearCommand(collectionManager));
        registerCommand(new CountByPriceCommand(collectionManager));
        registerCommand(new FilterGreaterManufactureCommand(collectionManager));
        registerCommand(new InfoCommand(collectionManager));
        registerCommand(new InsertCommand(collectionManager));
        registerCommand(new UpdateCommand(collectionManager));
        registerCommand(new PrintUniqueManufacturer(collectionManager));
        registerCommand(new RemoveCommand(collectionManager));
        registerCommand(new RemoveGreaterCommand(collectionManager));
        registerCommand(new ShuffleCommand(collectionManager));
    }

    public void registerCommand(Command command){
        commands.put(command.getName(), command);
    }

    /**
     * Возвращает команду по её имени.
     *
     * @param name имя команды
     * @return объект {@link Command}, либо null, если команда не найдена
     */
    public Command getCommand(String name){
        return commands.get(name);
    }

    public Collection<Command> getCommands() {
        return commands.values();
    }
}
