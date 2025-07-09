package utility;

import data.Product;

import java.time.ZonedDateTime;

/**
 * Класс, отвечающий за установку автоматически генерируемых полей для объекта {@link Product}.
 */
public class ProductFinalizer {
    /**
     * Устанавливает ID и дату создания для {@link Product}, а также ID организации при необходимости.
     *
     * @param product объект, которому необходимо установить автоматически генерируемые поля
     */
    public static void finalize(Product product){
        product.setId(IdManager.getNextProductId());
        product.setCreationDate(ZonedDateTime.now());
        if(product.getManufacturer() != null && product.getManufacturer().getId() == null){
            product.getManufacturer().setId(IdManager.getNextOrgId());
        }
    }
}
