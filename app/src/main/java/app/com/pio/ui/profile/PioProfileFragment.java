package app.com.pio.ui.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import app.com.pio.R;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.utility.RoundedImageUtil;
import app.com.pio.utility.Util;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by pilleym on 5/28/2015.
 */
public class PioProfileFragment extends Fragment {
    View root;

    @InjectView(R.id.profile_image)
    ImageView vImage;
    @InjectView(R.id.profile_name)
    TextView vName;
    @InjectView(R.id.profile_level)
    TextView vLevel;
    @InjectView(R.id.profile_xp_bar)
    View vXPBar;
    @InjectView(R.id.profile_meter)
    View vMeter;
    @InjectView(R.id.profile_meter_text)
    TextView vMeterText;

    public static PioProfileFragment newInstance() {
        return new PioProfileFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.inject(this, root);

        if (ProfileManager.activeProfile.getImage() != null && ProfileManager.activeProfile.getImage().length() > 0) {
            Picasso.with(getActivity()).load(ProfileManager.activeProfile.getImage()).transform(
                    new RoundedImageUtil(96, 0)).into(vImage);
        }

        vName.setText(ProfileManager.activeProfile.getName());
        vLevel.setText("Level " + Util.getLevelFromXP(ProfileManager.activeProfile.getXp()));

        ViewTreeObserver viewTreeObserver = vXPBar.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(vXPBar.getWidth()==0 && Util.getExcessXP(ProfileManager.activeProfile.getXp()) > 0) {
                        vXPBar.setLayoutParams(new FrameLayout.LayoutParams((int) Util.dpToPx((float) (vLevel.getWidth() * Util.getExcessXP(ProfileManager.activeProfile.getXp())), getActivity()), (int) Util.dpToPx(12, getActivity())));
                        vMeter.setVisibility(View.VISIBLE);
                        RelativeLayout.LayoutParams meterParams = new RelativeLayout.LayoutParams(vMeter.getLayoutParams());
                        meterParams.setMargins((int) Util.dpToPx(
                                (float) (vLevel.getWidth() * Util.getExcessXP(ProfileManager.activeProfile.getXp()) - 1), getActivity())
                                , 0, 0, 0);
                        vMeter.setLayoutParams(meterParams);
                        vMeterText.setText(((int)(Util.getExcessXP(ProfileManager.activeProfile.getXp())*100))+"%");
                        if(Util.getExcessXP(ProfileManager.activeProfile.getXp()) > 0.5) {
                            RelativeLayout.LayoutParams meterTextParams = new RelativeLayout.LayoutParams(vMeterText.getLayoutParams());
                            meterTextParams.addRule(RelativeLayout.LEFT_OF, R.id.profile_meter);
                            vMeterText.setLayoutParams(meterParams);
                        }
                    }
                }
            });
        }

        return root;
    }
}
