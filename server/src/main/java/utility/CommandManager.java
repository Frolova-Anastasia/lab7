package utility;

import commands.*;
import db.AuthManager;
import db.ProductDAO;

import java.util.*;
/**
 * Менеджер серверных команд. Хранит, регистрирует и предоставляет доступ к реализациям команд.
 */
public class CommandManager {
    public final Map<String, Command> commands = new HashMap<>();
    private final CollectionManager collectionManager;
    private final AuthManager authManager;
    private final ProductDAO productDAO;

    public CommandManager(CollectionManager collectionManager, AuthManager authManager, ProductDAO productDAO) {
        this.collectionManager = collectionManager;
        this.authManager = authManager;
        this.productDAO = productDAO;
        registerAll();
    }

    /**
     * Регистрирует все доступные команды в рамках текущей серверной архитектуры.
     */
    private void registerAll(){
        registerCommand(new HelpCommand(commands));
        registerCommand(new AddCommand(collectionManager, authManager, productDAO));
        registerCommand(new ShowCommand(collectionManager, authManager));
        registerCommand(new ClearCommand(collectionManager, authManager, productDAO));
        registerCommand(new CountByPriceCommand(collectionManager, authManager));
        registerCommand(new FilterGreaterManufactureCommand(collectionManager, authManager));
        registerCommand(new InfoCommand(collectionManager, authManager));
        registerCommand(new UpdateCommand(collectionManager, authManager, productDAO));
        registerCommand(new PrintUniqueManufacturer(collectionManager, authManager));
        registerCommand(new RemoveCommand(collectionManager, authManager, productDAO));
        registerCommand(new RemoveGreaterCommand(collectionManager, authManager, productDAO));
        registerCommand(new ShuffleCommand(collectionManager, authManager));
        registerCommand(new LoginCommand(authManager));
        registerCommand(new RegisterCommand(authManager));
        registerCommand(new InsertCommand(collectionManager, authManager, productDAO));
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
