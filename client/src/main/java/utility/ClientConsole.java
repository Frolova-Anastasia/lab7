package utility;

import commands.ClientCommand;
import exceptions.EndInputException;
import responses.Response;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Отвечает за запуск клиентской консоли и обработку команд от пользователя.
 * Поддерживает интерактивный и скриптовый режим работы.
 */
public class ClientConsole {
    private InputProvider inputProvider;
    private final CommandManager commandManager;
    private final ProductBuilder productBuilder;

    public ClientConsole(CommandManager commandManager, ProductBuilder productBuilder) {
        this.commandManager = commandManager;
        this.productBuilder = productBuilder;
        this.inputProvider = new ConsoleInputProvider();
    }

    public void run()  {
        while (true) {
            System.out.print("> ");
            String line;
            try {
                line = inputProvider.nextLine().trim();
            } catch (NoSuchElementException e) {
                System.out.println("Ввод прерван");
                break;
            }
            if (line.isEmpty()) continue;
            String[] parts = line.split(" ");
            String name = parts[0];
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            ClientCommand command = commandManager.getCommand(name);
            if (command == null) {
                System.out.println("Неизвестная команда " + name);
                continue;
            }
            try {
                Response response = command.execute(args);
                if (response != null) {
                    System.out.println(response.getMessage());
                }
            } catch (EndInputException e) {
                System.out.println("Ввод прерван пользователем");
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void runScript() throws IOException, EndInputException {
        try {
            run(); // запустить стандартную обработку
        } finally {
            resetInputMode(); // вернуть ввод в консоль после выполнения
        }
    }

    // Включение скриптового режима
    public void setScriptInputMode(Path path) throws IOException {
        this.inputProvider = new FileInputProvider(path);
        productBuilder.setInputProvider(this.inputProvider);
    }

    // Возврат в консольный режим
    public void resetInputMode() {
        this.inputProvider = new ConsoleInputProvider();
        productBuilder.setInputProvider(this.inputProvider);
    }
}
