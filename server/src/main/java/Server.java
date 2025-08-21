import commands.Command;
import db.*;
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
import java.net.SocketException;
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
    private static volatile boolean running = true; // флаг работы

    /**
     * Точка входа. Запускает сервер, настраивает логирование, регистрирует команды и слушает входящие UDP-пакеты.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        configureLogger(); // инициализация логгера

        try {
            // Подключение к БД
            DBManager dbManager = new DBManager();

            OrganizationDAO organizationDAO = new OrganizationDAO(dbManager.getConnection());
            ProductDAO productDAO = new ProductDAO(dbManager.getConnection(), organizationDAO);
            UserDAO userDAO = new UserDAO(dbManager);

            CollectionManager collectionManager = new CollectionManager(productDAO);
            AuthManager authManager = new AuthManager(userDAO);
            CommandManager commandManager = new CommandManager(collectionManager, authManager, productDAO);

            ExecutorService processPool = Executors.newCachedThreadPool(); // обработка
            ExecutorService sendPool = Executors.newFixedThreadPool(2);    // отправка

            DatagramSocket socket = new DatagramSocket(port);
            logger.info("Сервер запущен на порту " + port);

            // Хук завершения
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Получен сигнал завершения. Останавливаем сервер...");
                running = false;
                socket.close(); // прерываем socket.receive()
                processPool.shutdown();
                sendPool.shutdown();
                dbManager.close();
            }));

            // Основной цикл приёма
            while (running) {
                try {
                    byte[] receiveData = new byte[8192];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    socket.receive(receivePacket); // блокируется, пока не придут данные

                    processPool.submit(() -> {
                        try {
                            Response response = handleRequest(receivePacket, commandManager);

                            sendPool.submit(() -> {
                                try {
                                    sendResponse(socket, response,
                                            receivePacket.getAddress(),
                                            receivePacket.getPort());
                                } catch (IOException e) {
                                    logger.log(Level.WARNING, "Ошибка отправки ответа", e);
                                }
                            });

                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Ошибка обработки запроса", e);
                        }
                    });

                } catch (SocketException e) {
                    // выбрасывается при socket.close() → значит выходим из цикла
                    if (running) {
                        logger.log(Level.WARNING, "Ошибка при приёме пакета", e);
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Ошибка при приёме пакета", e);
                }
            }

            logger.info("Сервер успешно остановлен.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка запуска сервера: " + e.getMessage(), e);
        }
    }

    private static Response handleRequest(DatagramPacket receivePacket, CommandManager commandManager) {
        try {
            System.out.println("[SERVER] Получен пакет, длина: " + receivePacket.getLength() +
                    " байт от " + receivePacket.getAddress() + ":" + receivePacket.getPort());

            //  Десериализация
            try (ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength()))) {

                CommandWrapper wrapper = (CommandWrapper) in.readObject();
                if (wrapper == null) {
                    logger.warning("Получен пустой запрос (wrapper = null)");
                    return new ErrorResponse("Ошибка: пустой запрос");
                }

                String name = wrapper.getCommandName();
                Request request = wrapper.getRequest();

                logger.info("Выполняется команда: " + name + " от пользователя " + request.getUsername());

                //  Поиск команды
                Command command = commandManager.getCommand(name);
                if (command == null) {
                    logger.warning("Команда '" + name + "' не найдена в CommandManager");
                    return new ErrorResponse("Команда не найдена: " + name);
                }

                // Выполнение команды
                Response response = command.execute(request);
                if (response == null) {
                    logger.warning("Команда '" + name + "' вернула null вместо ответа");
                    return new ErrorResponse("Ошибка: команда не вернула ответ");
                }

                logger.info("Команда '" + name + "' выполнена успешно");
                return response;
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка при чтении запроса: " + e.getMessage(), e);
            return new ErrorResponse("Ошибка при чтении запроса");
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING, "Ошибка десериализации объекта: " + e.getMessage(), e);
            return new ErrorResponse("Ошибка десериализации объекта");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Неизвестная ошибка при обработке запроса: " + e.getMessage(), e);
            return new ErrorResponse("Внутренняя ошибка сервера");
        }
    }


        private static void sendResponse(DatagramSocket socket, Response response, InetAddress clientAddress, int clientPort)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(response);
        out.flush();

        byte[] sendData = outputStream.toByteArray();
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
