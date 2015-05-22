package app.com.pio.ui.main.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import app.com.pio.R;

/**
 * Created by matthewmichaud on 1/17/15.
 */
public class DrawerListItem implements DrawerItem {

    private String itemText;

    public DrawerListItem(String itemText) {
        this.itemText = itemText;
    }

    @Override
    public int getViewType() {
        return RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = (View) inflater.inflate(R.layout.adapter_drawer_item, null);
            viewHolder = new ViewHolder();
            viewHolder.link = (TextView) convertView.findViewById(R.id.list_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.link.setText(itemText);

        return convertView;
    }

    private static class ViewHolder {
        TextView link;
    }
}
