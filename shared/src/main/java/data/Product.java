package data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Product implements Serializable, Comparable<Product> {
    @Serial
    private static final long serialVersionUID = 1L; //ID класса, чтобы нормально (де)сериализовалось при изменении структуры

    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    @XmlElement(name = "creationDate")
    @XmlJavaTypeAdapter(ZonedDateTimeAdapter.class)
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float price; //Поле может быть null, Значение поля должно быть больше 0
    private UnitOfMeasure unitOfMeasure; //Поле может быть null
    private Organization manufacturer; //Поле может быть null


    public Product(Integer id, String name, Coordinates coordinates, Float price, UnitOfMeasure unitOfMeasure, Organization manufacturer, ZonedDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.price = price;
        this.unitOfMeasure = unitOfMeasure;
        this.manufacturer = manufacturer;
        this.creationDate = creationDate;
    }

    public Product() {
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта не может быть пустым");
        } else {
            this.name = name;}
    }
    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null)  {
            throw new IllegalArgumentException("Поле координат не может быть пустым");
        }
        this.coordinates = coordinates;
    }
    public void setPrice(Float price) {
        if (price != null && price <= 0)  {
            throw new IllegalArgumentException("Цена не может быть меньше нуля, попробуй еще!");
        }
        this.price = price;
    }

    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setOrganization(Organization manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setId(Integer id) {
        if (this.id != null) {
            throw new UnsupportedOperationException("ID уже установлен и не может быть изменён");
        }
        this.id = id;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getId() {
        return id;
    }
    public String getName(){
        return name;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }
    public Float getPrice() {
        return price;
    }
    public UnitOfMeasure getUnitOfMeasure() {
        return unitOfMeasure;
    }
    public Organization getManufacturer() {
        return manufacturer;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", unitOfMeasure=" + unitOfMeasure +
                ", manufacturer=" + manufacturer +
                '}';
    }

    @Override
    public int compareTo(Product other) {

        //Сравниваем по имени
        int nameComparison = this.name.compareTo(other.name);
        if (nameComparison != 0) {
            return nameComparison;
        }

        // Если имена одинаковые, сравниваем по цене (если цены не null)
        if (this.price != null && other.price != null) {
            int priceComparison = this.price.compareTo(other.price);
            if (priceComparison != 0) {
                return priceComparison;
            }
        } else if (this.price != null) {
            return 1;
        } else if (other.price != null) {
            return -1;
        }

        // 3. Если имена и цены одинаковые, сравниваем по ID
        return this.id.compareTo(other.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name) && Objects.equals(coordinates, product.coordinates) && Objects.equals(creationDate, product.creationDate) && Objects.equals(price, product.price) && unitOfMeasure == product.unitOfMeasure && Objects.equals(manufacturer, product.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, price, unitOfMeasure, manufacturer);
    }
}
