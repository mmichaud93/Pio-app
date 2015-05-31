package app.com.pio.ui.welcome;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.com.pio.R;
import app.com.pio.models.WelcomePageModel;

/**
 * Created by mmichaud on 5/29/15.
 */
public class WelcomePagerAdapter extends PagerAdapter {

    WelcomePageModel[] models;
    WelcomeFragment fragment;

    public WelcomePagerAdapter(WelcomeFragment fragment, WelcomePageModel[] models) {
        this.models = models;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.adapter_welcome_pager, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.adapter_welcome_image);
        TextView textView = (TextView) itemView.findViewById(R.id.adapter_welcome_text);
        imageView.setImageResource(models[position].getImage());
        textView.setText(models[position].getMessage());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
