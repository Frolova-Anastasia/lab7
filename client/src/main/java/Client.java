import utility.*;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.NoSuchElementException;

/**
 * Точка входа клиентского приложения.
 * Устанавливает соединение, инициализирует команды и запускает консоль.
 */
public class Client {

    /**
     * Основной метод запуска клиента.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args){
        try{
            InetAddress serverAddress = InetAddress.getByName("server");
            int serverPort = 12345;
            DatagramSocket socket = new DatagramSocket();

            CommandSender sender = new CommandSender(socket, serverAddress, serverPort);
            InputProvider consoleInput = new ConsoleInputProvider();
            ProductBuilder builder = new ProductBuilder(consoleInput);
            CommandManager commandManager = new CommandManager();

            ClientConsole console = new ClientConsole(commandManager, builder);

            commandManager.initCommands(sender, builder, console);

            System.out.println("Клиент запущен. Введите команду: ");

            console.run();

        }catch (Exception e){
            System.out.println("Ошибка запуска клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
