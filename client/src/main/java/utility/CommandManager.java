package utility;

import commands.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер клиентских команд. Позволяет регистрировать и извлекать команды по их имени.
 */
public class CommandManager {
    private final Map<String, ClientCommand> commandMap = new HashMap<>();

    public CommandManager() {
    }

    public void initCommands(CommandSender sender, ProductBuilder builder, ClientConsole console){
        register(new HelpCommand(sender));
        register(new AddCommand(sender, builder));
        register(new ShowCommand(sender));
        register(new ExitCommand());
        register(new ClearCommand(sender));
        register(new CountByPriceCommand(sender));
        register(new FilterGreaterManufacturerCommand(sender, builder));
        register(new InfoCommand(sender));
        register(new InsertCommand(sender, builder));
        register(new UpdateCommand(sender, builder));
        register(new PrintUniqueManufacturer(sender));
        register(new RemoveCommand(sender));
        register(new RemoveGreaterCommand(sender, builder));
        register(new ShuffleCommand(sender));
        register(new ExecuteScriptCommand(this, console));
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
