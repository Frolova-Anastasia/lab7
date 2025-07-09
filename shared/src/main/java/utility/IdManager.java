package utility;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер генерации и учёта уникальных ID для продуктов и организаций.
 */
public class IdManager implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    static int nextProductId = 1;
    private static int nextOrgId = 1;

    private static final Set<Integer> usedProducts = new HashSet<>();
    private static final Set<Integer> usedOrgs = new HashSet<>();

    /**
     * Регистрирует уже существующий ID продукта.
     * @param id ID продукта
     */
    public static void registerProductId(int id){
        usedProducts.add(id);
        if (id >= nextProductId){
            nextProductId = id + 1;
        }
    }

    /**
     * Регистрирует уже существующий ID организации.
     * @param id ID организации
     */
    public static void registerOrgId(int id){
        usedOrgs.add(id);
        if(id >= nextOrgId){
            nextOrgId = id + 1;
        }
    }

    /**
     * Генерирует новый уникальный ID для продукта.
     * @return уникальный ID
     */
    public static int getNextProductId(){
        while(usedProducts.contains(nextProductId)) {
            System.out.println("ID " + nextProductId + " уже занят, пробуем следующий...");
            nextProductId++;
        }
        usedProducts.add(nextProductId);
        System.out.println("Сгенерирован новый ID: " + nextProductId);
        return nextProductId++;
    }

    /**
     * Генерирует новый уникальный ID для организации.
     * @return уникальный ID
     */
    public static int getNextOrgId(){
        while (usedOrgs.contains(nextOrgId)) nextOrgId++;
        usedOrgs.add(nextOrgId);
        return nextOrgId++;
    }

    /**
     * Очищает все зарегистрированные ID (используется при clear).
     */
    public static void clear(){
        usedOrgs.clear();
        usedProducts.clear();
        nextOrgId = 1;
        nextProductId = 1;
    }

}
