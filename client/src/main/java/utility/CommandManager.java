package utility;

import commands.*;
import input.ClientConsole;
import input.ProductBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер клиентских команд. Позволяет регистрировать и извлекать команды по их имени.
 */
public class CommandManager {
    private final Map<String, ClientCommand> commandMap = new HashMap<>();
    private String username;
    private String passwordHash;

    public CommandManager() {
    }

    public void initCommands(CommandSender sender, ProductBuilder builder, ClientConsole console){
        register(new HelpCommand(sender, username, passwordHash));
        register(new AddCommand(sender, builder, username, passwordHash));
        register(new ShowCommand(sender, username, passwordHash));
        register(new ExitCommand());
        register(new ClearCommand(sender, username, passwordHash));
        register(new CountByPriceCommand(sender, username, passwordHash));
        register(new FilterGreaterManufacturerCommand(sender, builder, username, passwordHash));
        register(new InfoCommand(sender, username, passwordHash));
        register(new UpdateCommand(sender, builder, username, passwordHash));
        register(new PrintUniqueManufacturer(sender, username, passwordHash));
        register(new RemoveCommand(sender, username, passwordHash));
        register(new RemoveGreaterCommand(sender, builder, username, passwordHash));
        register(new ShuffleCommand(sender, username, passwordHash));
        register(new ExecuteScriptCommand(this, console));
        register(new LoginCommand(sender));
        register(new RegisterCommand(sender));
        register(new InsertCommand(sender,builder, username, passwordHash));
    }

    public void setUser(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    private void register(ClientCommand command) {
        commandMap.put(command.getName(), command);
    }

    public ClientCommand getCommand(String name) {
        return commandMap.get(name);
    }

    public Map<String, ClientCommand> getCommands() {
        return commandMap;
    }
}
