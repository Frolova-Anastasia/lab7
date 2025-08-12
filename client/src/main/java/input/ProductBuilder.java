package input;

import data.*;
import exceptions.EndInputException;

import java.util.NoSuchElementException;
import java.util.function.IntPredicate;

/**
 * Класс, отвечающий за создание объектов {@link Product} с валидацией полей.
 * Использует {@link InputProvider} для получения пользовательского ввода.
 */
public class ProductBuilder {
    private InputProvider inputProvider;

    public ProductBuilder(InputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    /**
     * Устанавливает новый источник ввода (например, консоль или файл).
     *
     * @param provider источник ввода
     */
    public void setInputProvider(InputProvider provider) {
        this.inputProvider = provider;
    }

    /**
     * Собирает новый {@link Product}, запрашивая все необходимые поля у пользователя.
     *
     * @return новый объект Product
     * @throws EndInputException если ввод был неожиданно завершён
     */
    public Product builProduct() throws EndInputException {
        try {
        String name = askNonEmptyString("Введите название товара: ");
        Coordinates coordinates = buildCoordinates();
        Float price = askNullPositiveFloat("Введите цену: ");
        UnitOfMeasure unitOfMeasure = askEnum(UnitOfMeasure.class, "введите единицу измерения: ");
        Organization organization = buildOrganization();
        return new Product(null, name, coordinates, price, unitOfMeasure, organization, null);
        }catch (NoSuchElementException e){
            throw new EndInputException("Неожиданное завершение ввода");
        }
    }

    private Coordinates buildCoordinates() throws EndInputException {
        try {
        int x = askInt("Введите координату x(<=931)", value -> value <= 931);
        Long y = askNonNullLong("Введите координату y: ");
        return new Coordinates(x, y);
        }catch (NoSuchElementException e){
            throw new EndInputException("Неожиданное завершение ввода");
        }
    }

    public Organization buildOrganization() throws EndInputException {
        try {
        System.out.print("Добавить организацию-производителя?(y/n): ");
        if (!inputProvider.nextLine().trim().equalsIgnoreCase("y")) return null;

        String name = askNonEmptyString("Введите название организации: ");
        String fullName = askNonEmptyString("Введите полное название организации: ");
        OrganizationType type = askEnum(OrganizationType.class, "Введите тип организации(или оставьте пустым: ");

        return new Organization(name, fullName, type, null);
        }catch (NoSuchElementException e){
            throw new EndInputException("Неожиданное завершение ввода");
        }
    }



    private String askNonEmptyString(String text) {
        while (true) {
            System.out.println(text);
            String input = inputProvider.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Поле не может быть пустым");
        }
    }

    private int askInt(String text, IntPredicate validator){
        while (true) {
            System.out.println(text);
            try {
                int input = Integer.parseInt(inputProvider.nextLine().trim());
                if (validator.test(input)) return input;
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите целое число");
            }
        }
    }

    private Long askNonNullLong(String text){
        while (true) {
            System.out.println(text);
            try {
                return Long.parseLong(inputProvider.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Поле не может быть пустым и является целым числом");
            }
        }
    }

    private Float askNullPositiveFloat(String text){
        System.out.println(text);
        String input = inputProvider.nextLine().trim();
        try {
            float val = Float.parseFloat(input);
            return (val > 0) ? val : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private <E extends Enum<E>> E askEnum(Class<E> enumClass, String text)  {
        while (true) {
            System.out.print(text + " ");
            for (E constant : enumClass.getEnumConstants()) System.out.println(constant);
            String input = inputProvider.nextLine().trim();
            if (input.isEmpty()) return null;
            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректное значение");
            }
        }
    }


}

