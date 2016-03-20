package app.com.pio.ui.monuments;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mmichaud on 7/21/15.
 */
public class CityItem implements Serializable {

    String name;
    String province;
    String country;
    String id;
    boolean isCompleted;
    ArrayList<MonumentItem> monumentItems;
    CityStats cityStats;

    public static double AREA_FOR_EACH_POINT_KMSQ = 0.00001050708;

    public CityItem(String name, String province, String country, String id, ArrayList<MonumentItem> monumentItems, double area) {
        this.name = name;
        this.province = province;
        this.country = country;
        this.id = id;
        this.monumentItems = monumentItems;
        this.cityStats = new CityStats(area);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public ArrayList<MonumentItem> getMonumentItems() {
        return monumentItems;
    }

    public void setMonumentItems(ArrayList<MonumentItem> monumentItems) {
        this.monumentItems = monumentItems;
    }

    public CityStats getCityStats() {
        return cityStats;
    }

    public void setCityStats(CityStats cityStats) {
        this.cityStats = cityStats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class CityStats implements Serializable {

        double area;

        public CityStats(double area) {
            this.area = area;
        }

        public double getArea() {
            return area;
        }

        public void setArea(double area) {
            this.area = area;
        }
    }
}
