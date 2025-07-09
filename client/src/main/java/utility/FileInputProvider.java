package utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Реализация {@link InputProvider}, которая считывает строки из указанного файла.
 * Используется для исполнения скриптов.
 */
public class FileInputProvider implements InputProvider{
    private final Iterator<String> lines;

    public FileInputProvider(Path path) throws IOException {
        List<String> fileLines = Files.readAllLines(path);
        this.lines = fileLines.iterator();
    }

    @Override
    public String nextLine() throws NoSuchElementException {
        if (!lines.hasNext()) {
            throw new NoSuchElementException("Файл закончился");
        }
        return lines.next();
    }
}
