package data;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * Класс-контейнер для списка продуктов, используемый при (де)сериализации XML с помощью JAXB.
 */
@XmlRootElement(name = "products")
public class Products {
    private List<Product> products;

    /**
     * Возвращает список продуктов.
     * @return список {@link Product}
     */
    @XmlElement(name = "product") // Элементы списка
    public List<Product> getProducts() {
        return products;
    }

    /**
     * Устанавливает список продуктов.
     * @param products список {@link Product}
     */
    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
