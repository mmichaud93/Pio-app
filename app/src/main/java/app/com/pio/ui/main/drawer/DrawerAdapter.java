package app.com.pio.ui.main.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import app.com.pio.R;

/**
 * Created by matthewmichaud on 1/17/15.
 */
public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    public DrawerAdapter(Context context, List<DrawerItem> objects) {
        super(context, R.layout.adapter_drawer_item, objects);
    }

    @Override
    public int getViewTypeCount() {
        return DrawerItem.RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(LayoutInflater.from(getContext()), convertView);
    }
}
