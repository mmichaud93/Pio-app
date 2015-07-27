package app.com.pio.ui.monuments.citydetail;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.com.pio.R;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.main.drawer.DrawerItem;
import app.com.pio.ui.monuments.CityItem;
import app.com.pio.ui.monuments.MonumentItem;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mmichaud on 7/21/15.
 */
public class MonumentAdapter extends ArrayAdapter<MonumentItem> {

    public MonumentAdapter(Context context, List<MonumentItem> objects) {
        super(context, R.layout.adapter_monument_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonumentItem item = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = (View) LayoutInflater.from(getContext()).inflate(R.layout.adapter_monument_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(ProfileManager.monumentIsUnlocked(item.getId())) {
            viewHolder.vMonumentImage.setImageResource(item.getBitmapUnlocked());
        } else {
            viewHolder.vMonumentImage.setImageResource(item.getBitmapLocked());
        }
        viewHolder.vMonumentName.setText(item.getName());
        viewHolder.vMonumentLocation.setText(item.getLocation());

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.monument_image)
        ImageView vMonumentImage;
        @InjectView(R.id.monument_name)
        TextView vMonumentName;
        @InjectView(R.id.monument_location)
        TextView vMonumentLocation;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
