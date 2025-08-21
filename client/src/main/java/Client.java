import input.ClientConsole;
import input.ConsoleInputProvider;
import input.InputProvider;
import input.ProductBuilder;
import utility.*;

import java.io.IOException;

/**
 * Точка входа клиентского приложения.
 * Устанавливает соединение, инициализирует команды и запускает консоль.
 */
public class Client {
    public static void main(String[] args) {
        try {
            CommandSender sender = new CommandSender("localhost", 12348);

            InputProvider consoleInput = new ConsoleInputProvider();
            ProductBuilder builder = new ProductBuilder(consoleInput);
            CommandManager commandManager = new CommandManager();
            ClientConsole console = new ClientConsole(commandManager, builder);

            commandManager.initCommands(sender, builder, console);

            //Хук на завершение работы клиента
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nКлиент завершает работу. До свидания!");
                try {
                    sender.close(); // закрываем соединение
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            System.out.println("Клиент запущен. Введите команду:");
            console.run();

        } catch (Exception e) {
            System.out.println("Ошибка запуска клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
