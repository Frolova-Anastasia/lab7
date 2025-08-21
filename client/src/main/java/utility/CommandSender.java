package utility;

import requests.Request;
import responses.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Класс, отвечающий за отправку сериализованных команд на сервер и получение ответа.
 */
public class CommandSender {
    private final DatagramChannel channel;
    private final InetSocketAddress serverAddress;
    private final Selector selector;

    public CommandSender(String host, int port) throws IOException {
        this.serverAddress = new InetSocketAddress(host, port);

        this.channel = DatagramChannel.open();
        channel.configureBlocking(false); // неблокирующий режим

        this.selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
    }

    public void send(String commandName, Request request) throws IOException {
        if (UserSession.isAuthorized()) {
            request.setUsername(UserSession.getUsername());
            request.setPasswordHash(UserSession.getPasswordHash());
        }
        CommandWrapper wrapper = new CommandWrapper(commandName, request);

        // сериализация
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(wrapper);
        out.flush();

        ByteBuffer buffer = ByteBuffer.wrap(bos.toByteArray());
        channel.send(buffer, serverAddress);
    }

    public Response receive() throws IOException, ClassNotFoundException {
        selector.select(2000); // ждем до 2 секунд
        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();

            if (key.isReadable()) {
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                buffer.clear();
                SocketAddress address = channel.receive(buffer);
                if (address != null) {
                    buffer.flip();
                    try (ObjectInputStream in = new ObjectInputStream(
                            new ByteArrayInputStream(buffer.array(), 0, buffer.limit()))) {
                        return (Response) in.readObject();
                    }
                }
            }
        }
        System.out.println("[CLIENT] Ответ от сервера не получен за 2 сек.");
        return null; // если ответа нет
    }

    public void close() throws IOException {
        channel.close();
        selector.close();
    }
}
