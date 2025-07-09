package commands;

import exceptions.WrongNumberOfArgsException;

/**
 * Класс для проверки количества аргументов, переданных команде.
 */
public class NumberArgsChecker {
    /**
     * Проверяет, соответствует ли фактическое количество аргументов ожидаемому.
     *
     * @param command       массив аргументов, переданных команде (может быть {@code null})
     * @param expectedArgs  ожидаемое количество аргументов
     * @throws WrongNumberOfArgsException если количество аргументов не совпадает с ожидаемым
     */
    public static void checkArgs(String[] command, int expectedArgs) throws WrongNumberOfArgsException {
        int actualArgs = (command == null) ? 0 : command.length;
        if(actualArgs != expectedArgs){
            throw new WrongNumberOfArgsException("Эта команда принимает " + expectedArgs + " аргументов");
        }
    }
}
