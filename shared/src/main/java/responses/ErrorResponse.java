package responses;

import java.io.Serializable;

/**
 * Реализация {@link Response}, представляющая неудачный результат выполнения команды.
 */
public class ErrorResponse implements Response, Serializable {
    private final String message;

    /**
     * Создаёт сообщение об ошибке.
     * @param message текст ошибки
     */
    public ErrorResponse(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
