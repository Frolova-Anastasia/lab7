package data;


import java.io.Serializable;

public class Coordinates  implements Serializable {
    private int x; //Максимальное значение поля: 931
    private Long y; //Поле не может быть null

    public Coordinates(int x, Long y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {
    }

    public void setX(int newX) {
        if (newX > 931) {
            throw new IllegalArgumentException("Значение x не может превышать 931!");
        } else {
            this.x = newX;
        }
    }

    public int getX() {
        return x;
    }

    public void setY(Long newY) {
        if (newY == null) {
            throw new IllegalArgumentException("Значение y не может быть пустым");
        } else {
            this.y = newY;
        }
    }
    public Long getY() {
        return this.y;
    }

    public void validate() {
        setX(x);
        setY(y);
    }


    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
