package app.com.pio.ui.monuments;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mmichaud on 7/21/15.
 */
public class CityItem implements Serializable {

    String name;
    boolean isCompleted;
    ArrayList<MonumentItem> monumentItems;

    public CityItem(String name, ArrayList<MonumentItem> monumentItems) {
        this.name = name;
        this.monumentItems = monumentItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
