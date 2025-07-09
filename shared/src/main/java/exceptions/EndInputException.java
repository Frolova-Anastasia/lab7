package exceptions;

/**
 * Исключение, выбрасываемое при неожиданном завершении ввода данных (например, Ctrl+D/Ctrl+C).
 */
public class EndInputException extends Exception{
    public EndInputException(String message) {
        super("Ввод был неожиданно завершен");
    }

    public EndInputException() {
    }
}
