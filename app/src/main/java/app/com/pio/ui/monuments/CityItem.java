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
    String primaryColor;
    String accentColor;
    String otherAccentColor;


    public static double AREA_FOR_EACH_POINT_KMSQ = 0.00001050708;

    public CityItem(String name, String province, String country, String id, ArrayList<MonumentItem> monumentItems, double area, String primaryColor, String accentColor, String otherAccentColor) {
        this.name = name;
        this.province = province;
        this.country = country;
        this.id = id;
        this.monumentItems = monumentItems;
        this.cityStats = new CityStats(area);
        this.primaryColor = primaryColor;
        this.accentColor = accentColor;
        this.otherAccentColor = otherAccentColor;
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

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getOtherAccentColor() {
        return otherAccentColor;
    }

    public void setOtherAccentColor(String otherAccentColor) {
        this.otherAccentColor = otherAccentColor;
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
