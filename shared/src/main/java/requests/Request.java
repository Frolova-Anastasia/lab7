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
    private String username;
    private String passwordHash;
    private Product product;
    private Organization organization;

    /**
     * Конструктор с аргументами команды.
     * @param args строковые аргументы, переданные с командой
     */
    public Request(String username, String passwordHash, String... args) {
        this.args = args;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
