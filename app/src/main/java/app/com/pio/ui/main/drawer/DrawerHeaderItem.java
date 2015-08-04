package app.com.pio.ui.main.drawer;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import app.com.pio.R;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.models.ProfileModel;
import app.com.pio.utility.RoundedImageUtil;
import app.com.pio.utility.Util;


/**
 * Created by matthewmichaud on 1/17/15.
 */
public class DrawerHeaderItem implements DrawerItem {

    public DrawerHeaderItem() {

    }

    @Override
    public int getViewType() {
        return RowType.HEADER_ITEM.ordinal();

    }

    @Override
    //Where things get populated. Images, text, etc
    public View getView(LayoutInflater inflater, View convertView) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = (View) inflater.inflate(R.layout.adapter_drawer_header, null);
            viewHolder = new ViewHolder();
            viewHolder.link = (TextView) convertView.findViewById(R.id.header_item_text);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.header_item_picture);
            viewHolder.level = (TextView) convertView.findViewById(R.id.header_item_level);
            viewHolder.xpBar = (View) convertView.findViewById(R.id.header_item_xp_bar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.link.setText(ProfileManager.activeProfile.getName());
        float constA = 27.523f;
        float constB = 88.805f;
        float constC = 103.9f;

        viewHolder.level.setText("Level " + Util.getLevelFromXP(ProfileManager.activeProfile.getXp()));
        //y=27.523x^2-88.805x+103.9
//        Log.d("PIO", "level: " + levelTotal);
        if (ProfileManager.activeProfile.getImage() != null && ProfileManager.activeProfile.getImage().length() > 0) {
            Picasso.with(convertView.getContext()).load(ProfileManager.activeProfile.getImage()).transform(new RoundedImageUtil(96, 0)).into(viewHolder.profileImage);
        }
        viewHolder.xpBar.setLayoutParams(new FrameLayout.LayoutParams((int) Util.dpToPx((float) (224f * Util.getExcessXP(ProfileManager.activeProfile.getXp())), convertView.getContext()), (int) Util.dpToPx(12, convertView.getContext())));
        return convertView;


    }

    //holds views, define everything like textview
    private static class ViewHolder {
        TextView link;
        ImageView profileImage;
        TextView level;
        View xpBar;
    }


}
