package exceptions;

/**
 * Исключение, выбрасываемое при неправильном количестве аргументов команды.
 */
public class WrongNumberOfArgsException extends Exception{
    public WrongNumberOfArgsException(String message) {
        super(message);
    }

    public WrongNumberOfArgsException() {
    }
}
