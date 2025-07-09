import commands.Command;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CollectionManager;
import utility.CommandManager;
import utility.CommandWrapper;
import utility.FileManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.*;

/**
 * Основной класс серверной части приложения.
 * Запускает UDP-сервер, принимает и обрабатывает команды, отправляет ответы клиенту.
 */
public class Server {
    private static final int port = 12345;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    /**
     * Точка входа. Запускает сервер, настраивает логирование, регистрирует команды и слушает входящие UDP-пакеты.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args){
        configureLogger(); // инициализация логгера
        try{
            FileManager fileManager = new FileManager();
            CollectionManager collectionManager = new CollectionManager(fileManager);
            CommandManager commandManager = new CommandManager(collectionManager);

            // Хук на завершение работы
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Сервер завершает работу. Сохраняем коллекцию...");
                collectionManager.save();
            }));

            DatagramSocket socket = new DatagramSocket(port);
            logger.info("Сервер запущен на порту " + port);


            while(true){
                try {
              //Прием пакета
                byte[] recieveData = new byte[4096];
                DatagramPacket receivePacket = new DatagramPacket(recieveData, recieveData.length);
                socket.receive(receivePacket);

                logger.info("Получен новый запрос от клиента: " + receivePacket.getAddress() + ":" + receivePacket.getPort());

                //Десериализация команды
                ByteArrayInputStream byteInput = new ByteArrayInputStream(receivePacket.getData(), 0 , receivePacket.getLength());
                ObjectInputStream in = new ObjectInputStream(byteInput);
                CommandWrapper wrapper = (CommandWrapper) in.readObject();

                //Выполнение команды
                String name = wrapper.getCommandName();
                Request request = wrapper.getRequest();

                logger.info("Выполняется команда: " + name);

                Command command = commandManager.getCommand(name);
                Response response = command != null
                        ? command.execute(request) : new ErrorResponse("Команда не найдена");

                //Отправка ответа
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(outputStream);
                out.writeObject(response);
                out.flush();

                byte[] sendData = outputStream.toByteArray();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                DatagramPacket packet = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(packet);

                logger.info("Ответ отправлен клиенту: " + clientAddress + ":" + clientPort);

                }catch (Exception e){
                    logger.log(Level.WARNING, "Ошибка обработки запроса: " + e.getMessage(), e);
                }
            }

        }catch (Exception e){
            logger.log(Level.SEVERE, "Ошибка запуска сервера: " + e.getMessage(), e);
        }
    }

    /**
     * Настраивает логгер Java Util Logging для отображения всех уровней логов в консоль.
     */
    private static void configureLogger() {
        Logger rootLogger = Logger.getLogger("");

        // Удаляем все существующие обработчики
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // Создаем новый консольный обработчик
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO); // Только INFO и выше: INFO, WARNING, SEVERE
        consoleHandler.setFormatter(new SimpleFormatter());

        rootLogger.setLevel(Level.INFO); //Уровень логирования всего приложения
        rootLogger.addHandler(consoleHandler);

        // Больше не используем родительские обработчики
        rootLogger.setUseParentHandlers(false);
    }

}
