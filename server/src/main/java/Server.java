import commands.Command;
import db.*;
import exceptions.EndInputException;
import exceptions.WrongNumberOfArgsException;
import requests.Request;
import responses.ErrorResponse;
import responses.Response;
import utility.CollectionManager;
import utility.CommandManager;
import utility.CommandWrapper;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

/**
 * Основной класс серверной части приложения.
 * Запускает UDP-сервер, принимает и обрабатывает команды, отправляет ответы клиенту.
 */
public class Server {
    private static final int port = 12348;
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    /**
     * Точка входа. Запускает сервер, настраивает логирование, регистрирует команды и слушает входящие UDP-пакеты.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args){
        configureLogger(); // инициализация логгера
        try {
            // 1. Подключение к БД
            DBManager dbManager = new DBManager();

            // 2. DAO
            OrganizationDAO organizationDAO = new OrganizationDAO(dbManager.getConnection());
            ProductDAO productDAO = new ProductDAO(dbManager.getConnection(), organizationDAO);
            UserDAO userDAO = new UserDAO(dbManager);

            // 3. Менеджеры
            CollectionManager collectionManager = new CollectionManager(productDAO); // теперь работает с БД
            AuthManager authManager = new AuthManager(userDAO);

            CommandManager commandManager = new CommandManager(collectionManager, authManager, productDAO);

            // 4. Настройка пула потоков
            ExecutorService executorService = Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors()
            );

            // 5. Сокет
            DatagramSocket socket = new DatagramSocket(port);
            logger.info("Сервер запущен на порту " + port);

            // 6. Хук завершения
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Сервер завершает работу...");
                executorService.shutdown();
                dbManager.close();
            }));

            // 7. Основной цикл
            while (true) {
                try {
                    byte[] receiveData = new byte[8192];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    // Обрабатываем запрос в пуле потоков
                    executorService.submit(() -> {
                        try {
                            processRequest(socket, receivePacket, commandManager);
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Ошибка при обработке запроса", e);
                        }
                    });

                } catch (IOException e) {
                    logger.log(Level.WARNING, "Ошибка при приёме пакета", e);
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка запуска сервера: " + e.getMessage(), e);
        }
    }

    private static void processRequest(DatagramSocket socket, DatagramPacket receivePacket, CommandManager commandManager)
            throws IOException, ClassNotFoundException, SQLException, WrongNumberOfArgsException, EndInputException {

        // 1. Десериализация
        ByteArrayInputStream byteInput = new ByteArrayInputStream(
                receivePacket.getData(), 0, receivePacket.getLength()
        );
        ObjectInputStream in = new ObjectInputStream(byteInput);
        CommandWrapper wrapper = (CommandWrapper) in.readObject();

        String name = wrapper.getCommandName();
        Request request = wrapper.getRequest();

        logger.info("Выполняется команда: " + name);
        System.out.println("[SERVER] Received request from " + request.getUsername() + " with hash: " + request.getPasswordHash());

        // 2. Выполнение
        Command command = commandManager.getCommand(name);
        Response response = (command != null)
                ? command.execute(request)
                : new ErrorResponse("Команда не найдена");

        // 3. Отправка ответа
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(response);
        out.flush();

        byte[] sendData = outputStream.toByteArray();
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        socket.send(sendPacket);

        logger.info("Ответ отправлен клиенту: " + clientAddress + ":" + clientPort);
    }

    /**
     * Настраивает логгер Java Util Logging для отображения некоторых уровней логов в консоль.
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
