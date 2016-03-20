package app.com.pio.ui.friends.friendslist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.pio.R;
import app.com.pio.features.monuments.MonumentManager;
import app.com.pio.ui.FlowLayout;
import app.com.pio.ui.friends.FriendsFragment;
import app.com.pio.utility.Util;

/**
 * Created by mmichaud on 3/6/16.
 */
public class FriendsListAdapter extends ArrayAdapter<FriendsListItem> {

    private static final String TAG = FriendsListAdapter.class.getName();

    private FriendsFragment friendsFragment;

    public FriendsListAdapter(FriendsFragment friendsFragment, int resource, List<FriendsListItem> objects) {
        super(friendsFragment.getActivity(), resource, objects);
        this.friendsFragment = friendsFragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_friends_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.friends_list_name);
            viewHolder.percent = (TextView) convertView.findViewById(R.id.friends_list_percent);
            viewHolder.level = (TextView) convertView.findViewById(R.id.friends_list_level);
            viewHolder.profileImage = (ImageView) convertView.findViewById(R.id.friends_list_profile_image);
            viewHolder.levelBar = convertView.findViewById(R.id.friends_list_level_bar);
            viewHolder.levelBarParent = (FrameLayout) convertView.findViewById(R.id.friends_list_level_bar_parent);
            viewHolder.monumentsNone = (TextView) convertView.findViewById(R.id.friends_list_monuments_none);
            viewHolder.monumentsListParent = (FlowLayout) convertView.findViewById(R.id.friends_list_monuments_list);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (getItem(position).getMonuments().length > 0) {
            viewHolder.monumentsNone.setVisibility(View.GONE);
            viewHolder.monumentsListParent.setVisibility(View.VISIBLE);
            viewHolder.monumentsListParent.removeAllViews();
            for (String monument : getItem(position).getMonuments()) {
                int resource = MonumentManager.getMonumentImage(monument);
                if (resource != -1) {
                    ImageView imageView = new ImageView(getContext());
                    FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams((int) Util.dpToPx(48, getContext()), (int) Util.dpToPx(48, getContext()));
                    layoutParams.setMargins(
                            (int) Util.dpToPx(4, getContext()),
                            (int) Util.dpToPx(4, getContext()),
                            (int) Util.dpToPx(4, getContext()),
                            (int) Util.dpToPx(4, getContext())
                    );
                    imageView.setLayoutParams(layoutParams);
                    imageView.setImageResource(resource);
                    viewHolder.monumentsListParent.addView(imageView);
                }
            }
        } else {
            viewHolder.monumentsNone.setVisibility(View.VISIBLE);
            viewHolder.monumentsListParent.setVisibility(View.GONE);
        }
        viewHolder.name.setText(getItem(position).getName());
        viewHolder.level.setText("Level: " + Util.getLevelFromXP(getItem(position).getXp()));
        viewHolder.percent.setText((int) (Util.getExcessXP(getItem(position).getXp()) * 100) + "%");
        int width = viewHolder.levelBarParent.getWidth();
        if (width == 0) {
            width = 900;
        }
        viewHolder.levelBar.setLayoutParams(
                new FrameLayout.LayoutParams(
                        (int) (width * Util.getExcessXP(getItem(position).getXp())),
                        (int) Util.dpToPx(12, getContext())));
        if (getItem(position).getProfileImageUrl() != null && !getItem(position).getProfileImageUrl().isEmpty()) {
            Picasso.with(getContext()).load(getItem(position).getProfileImageUrl()).placeholder(R.drawable.ic_empty_profile).into(viewHolder.profileImage);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView percent;
        TextView level;
        ImageView profileImage;
        View levelBar;
        FrameLayout levelBarParent;
        TextView monumentsNone;
        FlowLayout monumentsListParent;
    }
}
