package ua.smartfoxpro.bot.pojo;

import javax.persistence.*;
import java.io.Serializable;
@Entity
@Table(name = "city_data", schema = "city_data")
public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long cityId;
    @Column
    private String name;
    @Column
    private String text;

    public City() {
    }

    public City(Long cityId, String name, String text) {
        this.cityId = cityId;
        this.name = name;
        this.text = text;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (!cityId.equals(city.cityId)) return false;
        if (!name.equals(city.name)) return false;
        return text.equals(city.text);
    }

    @Override
    public int hashCode() {
        int result = cityId.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityId='" + cityId + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
