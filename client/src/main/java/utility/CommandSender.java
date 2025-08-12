package utility;

import requests.Request;
import responses.Response;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Класс, отвечающий за отправку сериализованных команд на сервер и получение ответа.
 */
public class CommandSender {
    private final DatagramSocket socket;
    private final InetAddress serverAdd;
    private final int serverPort;

    public CommandSender(DatagramSocket socket, InetAddress serverAdd, int serverPort) {
        this.socket = socket;
        this.serverAdd = serverAdd;
        this.serverPort = serverPort;
    }

    /**
     * Отправляет команду и её аргументы на сервер.
     *
     * @param commandName имя команды
     * @param request     объект запроса
     * @throws IOException если произошла ошибка при передаче
     */
    public void send(String commandName, Request request) throws IOException {
        if (UserSession.isAuthorized()) {
            request.setUsername(UserSession.getUsername());
            request.setPasswordHash(UserSession.getPasswordHash());
        }

        CommandWrapper wrapper = new CommandWrapper(commandName, request);
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);
        out.writeObject(wrapper);
        out.flush();

        byte[] sendData = byteStream.toByteArray();
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverAdd, serverPort);
        socket.send(packet);
    }


    /**
     * Получает и десериализует ответ от сервера.
     *
     * @return объект ответа
     * @throws IOException            если произошла ошибка при получении данных
     * @throws ClassNotFoundException если класс ответа не найден
     */
    public Response receive() throws IOException, ClassNotFoundException {
        byte[] recieveData = new byte[4096];
        DatagramPacket recievePacket = new DatagramPacket(recieveData, recieveData.length);
        socket.receive(recievePacket);

        //Десериализация ответа
        ByteArrayInputStream byteInput = new ByteArrayInputStream(recievePacket.getData(), 0, recievePacket.getLength());
        ObjectInputStream in = new ObjectInputStream(byteInput);
        Response response = (Response) in.readObject();

        return response;
    }

    public void close() {
        socket.close();
    }
}
