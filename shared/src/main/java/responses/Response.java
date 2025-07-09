package responses;

import java.io.Serializable;

/**
 * Интерфейс ответа от сервера клиенту.
 * Реализуется через {@link SuccessResponse} и {@link ErrorResponse}.
 */
public interface Response extends Serializable {
    /**
     * Возвращает сообщение, связанное с ответом.
     * @return текст сообщения
     */
    String getMessage();
}
