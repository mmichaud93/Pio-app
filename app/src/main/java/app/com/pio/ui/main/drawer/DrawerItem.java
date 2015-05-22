package app.com.pio.ui.main.drawer;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by matthewmichaud on 1/17/15.
 */
public interface DrawerItem {
    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }

    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}
