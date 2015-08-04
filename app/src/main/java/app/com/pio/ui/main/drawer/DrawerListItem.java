package app.com.pio.ui.main.drawer;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.pio.R;

/**
 * Created by matthewmichaud on 1/17/15.
 */
public class DrawerListItem implements DrawerItem {

    private String itemText;
    private int iconRes;

    public DrawerListItem(String itemText, int iconRes) {
        this.itemText = itemText;
        this.iconRes = iconRes;
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
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.list_item_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.link.setText(itemText);
        if(iconRes > 0) {
            viewHolder.icon.setImageResource(iconRes);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView link;
        ImageView icon;
    }
}
