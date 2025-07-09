package utility;

import requests.Request;

import java.io.Serial;
import java.io.Serializable;

/**
 * Обёртка команды, передаваемая от клиента на сервер.
 * Содержит имя команды и объект {@link Request}.
 */
public class CommandWrapper implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final Request request;
    /**
     * Создаёт обёртку для команды.
     * @param commandName имя команды
     * @param request объект запроса
     */
    public CommandWrapper(String commandName, Request request) {
        this.commandName = commandName;
        this.request = request;
    }

    public String getCommandName() {
        return commandName;
    }

    public Request getRequest() {
        return request;
    }
}
