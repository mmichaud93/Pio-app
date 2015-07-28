package app.com.pio.ui.monuments;

import android.graphics.PointF;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by mmichaud on 7/21/15.
 */
public class MonumentItem implements Serializable {

    String id;
    String name;
    String location;

    boolean isUnlocked;

    int bitmapLockedLarge;
    int bitmapUnlockedLarge;
    int bitmapLockedMed;
    int bitmapUnlockedMed ;
    int bitmapLockedSmall;
    int bitmapUnlockedSmall;

    double pinLat;
    double pinLong;
    double radius;

    public MonumentItem(String id, String name, String location, int bitmapLockedLarge, int bitmapUnlockedLarge, int bitmapLockedMed, int bitmapUnlockedMed, int bitmapLockedSmall, int bitmapUnlockedSmall, double pinLat, double pinLong, double radius) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.bitmapLockedLarge = bitmapLockedLarge;
        this.bitmapUnlockedLarge = bitmapUnlockedLarge;
        this.bitmapLockedMed = bitmapLockedMed;
        this.bitmapUnlockedMed = bitmapUnlockedMed;
        this.bitmapLockedSmall = bitmapLockedSmall;
        this.bitmapUnlockedSmall = bitmapUnlockedSmall;
        this.pinLat = pinLat;
        this.pinLong = pinLong;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getBitmapLockedLarge() {
        return bitmapLockedLarge;
    }

    public void setBitmapLockedLarge(int bitmapLockedLarge) {
        this.bitmapLockedLarge = bitmapLockedLarge;
    }

    public int getBitmapUnlockedLarge() {
        return bitmapUnlockedLarge;
    }

    public void setBitmapUnlockedLarge(int bitmapUnlockedLarge) {
        this.bitmapUnlockedLarge = bitmapUnlockedLarge;
    }

    public int getBitmapLockedMed() {
        return bitmapLockedMed;
    }

    public void setBitmapLockedMed(int bitmapLockedMed) {
        this.bitmapLockedMed = bitmapLockedMed;
    }

    public int getBitmapUnlockedMed() {
        return bitmapUnlockedMed;
    }

    public void setBitmapUnlockedMed(int bitmapUnlockedMed) {
        this.bitmapUnlockedMed = bitmapUnlockedMed;
    }

    public int getBitmapLockedSmall() {
        return bitmapLockedSmall;
    }

    public void setBitmapLockedSmall(int bitmapLockedSmall) {
        this.bitmapLockedSmall = bitmapLockedSmall;
    }

    public int getBitmapUnlockedSmall() {
        return bitmapUnlockedSmall;
    }

    public void setBitmapUnlockedSmall(int bitmapUnlockedSmall) {
        this.bitmapUnlockedSmall = bitmapUnlockedSmall;
    }

    public double getPinLat() {
        return pinLat;
    }

    public void setPinLat(double pinLat) {
        this.pinLat = pinLat;
    }

    public double getPinLong() {
        return pinLong;
    }

    public void setPinLong(double pinLong) {
        this.pinLong = pinLong;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(boolean isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
