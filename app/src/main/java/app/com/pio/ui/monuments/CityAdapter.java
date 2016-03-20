package app.com.pio.ui.monuments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import app.com.pio.R;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.main.drawer.DrawerItem;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by matthewmichaud on 1/17/15.
 */
public class CityAdapter extends ArrayAdapter<CityItem> {

    DecimalFormat areaFormat = new DecimalFormat("#.###");

    public CityAdapter(Context context, List<CityItem> objects) {
        super(context, R.layout.adapter_city_item, objects);
    }

    @Override
    public int getViewTypeCount() {
        return DrawerItem.RowType.values().length;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CityItem item = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = (View) LayoutInflater.from(getContext()).inflate(R.layout.adapter_city_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.vCityName.setText(item.getName());
        int areaPoints = ProfileManager.activeProfile.getPointCounts(item.getId());
        viewHolder.vCityAreaExplored.setText("~"+areaFormat.format(
                (areaPoints * CityItem.AREA_FOR_EACH_POINT_KMSQ)
                / item.getCityStats().getArea()) + "% explored");

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.city_item_name)
        TextView vCityName;
        @InjectView(R.id.city_item_area_explored)
        TextView vCityAreaExplored;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
