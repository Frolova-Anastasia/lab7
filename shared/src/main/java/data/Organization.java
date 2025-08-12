package data;


import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Organization implements Comparable<Organization>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String fullName; //Поле не может быть null
    private OrganizationType type; //Поле может быть null


    public Organization(String name, String fullName, OrganizationType type, Integer id) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.type = type;
    }

    public Organization() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (this.id != null) {
            throw new IllegalStateException("ID уже установлен");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Имя необходимо задать");
        }
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            throw new IllegalArgumentException("Полное имя нужно задать!");
        }
        this.fullName = fullName;
    }

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public void validate() {
        if (id <= 0) {
            throw new IllegalArgumentException("ID организации должен быть больше 0");
        }
        setName(name);
        setFullName(fullName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(fullName, that.fullName) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fullName, type);
    }


    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public int compareTo(Organization other) {
        if (other == null) {
            return 1; // null считается меньшим, чем любой не-null объект
        }

        // Сначала сравниваем по имени
        int nameComparison = this.name.compareTo(other.name);
        if (nameComparison != 0) {
            return nameComparison;
        }

        // Если имена одинаковы, сравниваем по полному имени
        int fullNameComparison = this.fullName.compareTo(other.fullName);
        if (fullNameComparison != 0) {
            return fullNameComparison;
        }

        // Если и полные имена одинаковы, сравниваем по типу (type)
        if (this.type == null && other.type == null) {
            return 0;
        }
        if (this.type == null) {
            return -1; // null type считается меньшим
        }
        if (other.type == null) {
            return 1;
        }
        return this.type.compareTo(other.type);

    }
}
