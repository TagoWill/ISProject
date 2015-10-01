package crawler;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiago on 28/09/2015.
 * Trabalho: Interesacao de Sistemas
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "data"
})
@XmlRootElement(name = "listofthings")
public class ListOfThings {
    @XmlElement(name = "info", required = true)
    protected List<ListOfThings.Info> data;
    @XmlAttribute(name = "timestamp")
    protected Long timestamp;
    @XmlAttribute(name = "timezone")
    protected String timezone;
    @XmlAttribute(name = "version")
    protected Double version;

    public List<Info> getData() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return this.data;
    }

    @SuppressWarnings("all")
    public Long getTimestamp() {
        return timestamp;
    }

    @SuppressWarnings("all")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @SuppressWarnings("all")
    public String getTimezone() {
        return timezone;
    }

    @SuppressWarnings("all")
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @SuppressWarnings("all")
    public Double getVersion() {
        return version;
    }

    @SuppressWarnings("all")
    public void setVersion(Double version) {
        this.version = version;
    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "brand",
            "name",
            "price"
    })
    public static class Info{

        protected String brand;
        protected String name;
        protected String price;

        @SuppressWarnings("all")
        public String getBrand() {
            return brand;
        }


        public void setBrand(String brand) {
            this.brand = brand;
        }

        @SuppressWarnings("all")
        public String getName() {
            return name;
        }



        public void setName(String name) {
            this.name = name;
        }

        @SuppressWarnings("all")
        public String getPrice() {
            return price;
        }


        public void setPrice(String price) {
            this.price = price;
        }
    }
}
