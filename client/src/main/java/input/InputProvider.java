package input;

import java.util.NoSuchElementException;

/**
 * Интерфейс абстракции над источником ввода (консоль, файл).
 */
public interface InputProvider {
    /**
     * Возвращает следующую строку ввода.
     *
     * @return строка ввода
     * @throws NoSuchElementException если строк больше нет
     */
    String nextLine() throws NoSuchElementException;
}
