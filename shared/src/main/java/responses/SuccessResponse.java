package responses;

import java.io.Serializable;

/**
 * Реализация {@link Response}, представляющая успешное выполнение команды.
 */
public class SuccessResponse implements Response, Serializable {
    private final String message;

    /**
     * Создаёт успешный ответ с сообщением.
     * @param message текст сообщения
     */
    public SuccessResponse(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
