package requests;

import data.Organization;
import data.Product;

import java.io.Serializable;

/**
 * Класс, представляющий запрос от клиента к серверу.
 * Содержит аргументы команды, объект {@link Product} и, при необходимости, {@link Organization}.
 */
public class Request implements Serializable {
    private final String[] args;
    private Product product;
    private Organization organization;

    /**
     * Конструктор с аргументами команды.
     * @param args строковые аргументы, переданные с командой
     */
    public Request(String... args) {
        this.args = args;
    }

    public String[] getArgs() {
        return args;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product){
        this.product = product;
    }

    public Organization getOrganization(){
        return organization;
    }

    public void setOrganization(Organization organization){
        this.organization = organization;
    }

}
