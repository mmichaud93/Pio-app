package app.com.pio.ui.monuments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import app.com.pio.R;
import app.com.pio.features.monuments.MonumentManager;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.main.drawer.DrawerItem;
import app.com.pio.utility.Util;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by matthewmichaud on 1/17/15.
 */
public class CityAdapter extends ArrayAdapter<CityItem> {

    DecimalFormat areaFormat = new DecimalFormat("#.######");

    public CityAdapter(Context context, List<CityItem> objects) {
        super(context, R.layout.adapter_city_item, objects);
    }

    @Override
    public int getViewTypeCount() {
        return DrawerItem.RowType.values().length;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CityItem item = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = (View) LayoutInflater.from(getContext()).inflate(R.layout.adapter_city_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cityNameDrop.setText(item.getName());
        float[] hsvDarker = new float[3];
        Color.colorToHSV(Color.parseColor(item.getPrimaryColor()), hsvDarker);
        hsvDarker[2] *= 0.75f; // value component
        int darkerColor = Color.HSVToColor(hsvDarker);
        viewHolder.cityNameDrop.setTextColor(darkerColor);
        viewHolder.cityName.setText(item.getName());
        int areaPoints = ProfileManager.activeProfile.getPointCounts(item.getId());
        viewHolder.cityPieChart.setPercentFilled((areaPoints * CityItem.AREA_FOR_EACH_POINT_KMSQ)
                / item.getCityStats().getArea());
//        viewHolder.cityPieChart.setPercentFilled(Math.random());
        viewHolder.cityPieChart.setBaseColor(Color.parseColor(item.getAccentColor()));
        viewHolder.cityPieChart.setFillColor(Color.parseColor(item.getOtherAccentColor()));
        String text = areaFormat.format(
                (areaPoints * CityItem.AREA_FOR_EACH_POINT_KMSQ)
                        / item.getCityStats().getArea()) + "%";
        viewHolder.cityAreaExplored.setText(areaFormat.format(
                (areaPoints * CityItem.AREA_FOR_EACH_POINT_KMSQ)
                        / item.getCityStats().getArea()) + "%");
        viewHolder.cityMonumentsUnlocked.setText(ProfileManager.activeProfile.getMonuments(item.getId()).size() + "/" + item.getMonumentItems().size() + " monuments unlocked");
        viewHolder.cityMonumentsUnlockedDrop.setText(ProfileManager.activeProfile.getMonuments(item.getId()).size() + "/" + item.getMonumentItems().size() + " monuments unlocked");
        viewHolder.cityMonumentsUnlockedDrop.setTextColor(darkerColor);
//        int res = R.drawable.city_item_icon_default;
//        viewHolder.cityParent.setBackgroundColor(Color.parseColor(item.getPrimaryColor()));
//            res = R.drawable.cover_boston;
        final RelativeLayout relativeLayout = viewHolder.cityParent;
        viewHolder.cityParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                BitmapDrawable background = getCityItemBackground(getContext(), Color.parseColor(item.primaryColor), relativeLayout.getWidth(), relativeLayout.getHeight());
//                if (background != null) {
//
//                    relativeLayout.setBackground(background);
//                }
            }
        });

//        else
//            res = R.drawable.cover_new_york;
//        Picasso.with(getContext()).load(res).into(viewHolder.cityIcon);
//        Picasso.with(getContext()).load(res).transform(new RoundedImageUtil(
//                (int)Util.dpToPx(192, getContext()), 0
//        )).into(viewHolder.cityIcon);
        return convertView;
    }

    public static BitmapDrawable getCityItemBackground(Context context, int color, int width, int height) {

        if (width <= 0 || height <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, null);
        Canvas canvas = new Canvas(bitmap);

        Paint leftTrapezoidPaint = new Paint();

        float[] hsvDarker = new float[3];
        Color.colorToHSV(color, hsvDarker);
        hsvDarker[2] *= 0.95f; // value component
        int darkerColor = Color.HSVToColor(hsvDarker);
        float[] hsvLighter = new float[3];
        Color.colorToHSV(color, hsvLighter);
        hsvLighter[2] = 1.0f - 0.8f * (1.0f - hsvLighter[2]);
        int lighterColor = Color.HSVToColor(hsvLighter);

        int top = 32;
        int bottom = height - 32;
        int left = 32;
        int right = width - 32;

        leftTrapezoidPaint.setColor(darkerColor);
        Path leftTrapezoid = new Path();
        leftTrapezoid.moveTo(0, 0); // used for first point
        leftTrapezoid.lineTo(left, top);
        leftTrapezoid.lineTo(left, right);
        leftTrapezoid.lineTo(0, height);
        leftTrapezoid.lineTo(0, 0); // there is a setLastPoint action but i found it not to work as expected

        canvas.drawPath(leftTrapezoid, leftTrapezoidPaint);

        Paint rightTrapezoidPaint = new Paint();
        rightTrapezoidPaint.setColor(darkerColor);
        Path rightTrapezoid = new Path();
        rightTrapezoid.moveTo(width, 0); // used for first point
        rightTrapezoid.lineTo(right, top);
        rightTrapezoid.lineTo(right, bottom);
        rightTrapezoid.lineTo(width, height);
        rightTrapezoid.lineTo(width, 0); // there is a setLastPoint action but i found it not to work as expected

        canvas.drawPath(rightTrapezoid, rightTrapezoidPaint);

        Paint topTrapezoidPaint = new Paint();
        topTrapezoidPaint.setColor(lighterColor);
        Path topTrapezoid = new Path();
        topTrapezoid.moveTo(0, 0); // used for first point
        topTrapezoid.lineTo(width, 0);
        topTrapezoid.lineTo(right, top);
        topTrapezoid.lineTo(left, top);
        topTrapezoid.lineTo(0, 0); // there is a setLastPoint action but i found it not to work as expected

        canvas.drawPath(topTrapezoid, topTrapezoidPaint);

        Paint bottomTrapezoidPaint = new Paint();
        bottomTrapezoidPaint.setColor(lighterColor);
        Path bottomTrapezoid = new Path();
        bottomTrapezoid.moveTo(0, height); // used for first point
        bottomTrapezoid.lineTo(width, height);
        bottomTrapezoid.lineTo(right, bottom);
        bottomTrapezoid.lineTo(left, bottom);
        bottomTrapezoid.lineTo(0, height); // there is a setLastPoint action but i found it not to work as expected

        canvas.drawPath(bottomTrapezoid, bottomTrapezoidPaint);

        Paint centerPaint = new Paint();
        centerPaint.setColor(color);
        canvas.drawRect(left, top, right, bottom, centerPaint);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    static class ViewHolder {
        @InjectView(R.id.city_item_parent)
        RelativeLayout cityParent;
        @InjectView(R.id.city_item_name_drop)
        TextView cityNameDrop;
        @InjectView(R.id.city_item_name)
        TextView cityName;
        @InjectView(R.id.city_item_monuments_unlocked_drop)
        TextView cityMonumentsUnlockedDrop;
        @InjectView(R.id.city_item_monuments_unlocked)
        TextView cityMonumentsUnlocked;
        @InjectView(R.id.city_item_pie_chart)
        PieChartView cityPieChart;
//        @InjectView(R.id.city_item_icon)
//        ImageView cityIcon;
        @InjectView(R.id.city_item_area_explored)
        TextView cityAreaExplored;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
