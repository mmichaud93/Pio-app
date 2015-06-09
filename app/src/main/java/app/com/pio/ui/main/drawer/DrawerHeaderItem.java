package app.com.pio.ui.main.drawer;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import app.com.pio.R;
import app.com.pio.utility.RoundedImageUtil;


/**
 * Created by matthewmichaud on 1/17/15.
 */
public class DrawerHeaderItem implements DrawerItem {

    private String itemText;
    private String itemImage;


    public DrawerHeaderItem(String itemText, String itemImage) {
        this.itemText = itemText;
        this.itemImage = itemImage;


    }

    @Override
    public int getViewType() {
        return RowType.HEADER_ITEM.ordinal();

    }

    @Override
    //Where things get populated. Images, text, etc
    public View getView(LayoutInflater inflater, View convertView) {
        ViewHolder viewHolder;
        if(convertView == null) {
            convertView = (View) inflater.inflate(R.layout.adapter_drawer_header, null);
            viewHolder = new ViewHolder();
            viewHolder.link = (TextView) convertView.findViewById(R.id.header_item_text);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.header_item_picture);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.link.setText(itemText);

        Picasso.with(convertView.getContext()).load(itemImage).transform(new RoundedImageUtil(96,0)).into(viewHolder.profileImage);

        return convertView;


    }
    //holds views, define everything like textview
    private static class ViewHolder {
        TextView link;
        ImageView profileImage;
    }




}
