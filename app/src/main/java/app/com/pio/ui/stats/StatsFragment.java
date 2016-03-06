package app.com.pio.ui.stats;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.com.pio.R;
import app.com.pio.ui.map.RecordUtil;
import app.com.pio.utility.PrefUtil;
import app.com.pio.utility.Util;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mmichaud on 7/13/15.
 */
public class StatsFragment extends Fragment {

    View root;
    @InjectView(R.id.stats_total_area)
    TextView totalArea;
    @InjectView(R.id.stats_total_time)
    TextView totalTime;
    @InjectView(R.id.stats_number_of_points)
    TextView numberPoints;
    @InjectView(R.id.stats_speed)
    TextView speed;
    @InjectView(R.id.stats_distance)
    TextView distance;
    @InjectView(R.id.stats_total_distance)
    TextView totalDistance;

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_stats, container, false);
        ButterKnife.inject(this, root);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        totalArea.setText(getString(R.string.stats_total_area_text,
                (PrefUtil.getPref(getActivity(), PrefUtil.PREFS_STATS_AREA_KEY, 0f) + RecordUtil.getUncoveredKilometersSquared())));
        totalTime.setText(getString(R.string.stats_total_time_text,
                Util.formatLongToTime(PrefUtil.getPref(getActivity(), PrefUtil.PREFS_STATS_TIME_KEY, 0) + RecordUtil.getRecordingSessionTime())));
        totalDistance.setText(getString(R.string.stats_total_distance_text,
                (PrefUtil.getPref(getActivity(), PrefUtil.PREFS_STATS_DISTANCE_KEY, 0f) + RecordUtil.getDistanceTravelled())));
        numberPoints.setText(getString(R.string.stats_number_of_points_text,
               RecordUtil.getNumberOfPoints()));
        speed.setText(getString(R.string.stats_speed_text,
                RecordUtil.getSpeed()));
        distance.setText(getString(R.string.stats_distance_text,
                RecordUtil.getDistanceTravelled()));
        return root;

    }

    @Override
    public void onResume() {
        super.onResume();

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Stats");
    }
}
