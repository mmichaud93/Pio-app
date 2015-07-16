package app.com.pio.ui.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.view.View;

import app.com.pio.R;
import app.com.pio.utility.AreYouSureDialogFragment;
import app.com.pio.utility.PrefUtil;

/**
 * Created by mmichaud on 7/13/15.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_settings);
        Preference connectToNewComputer= findPreference(getString(R.string.prefs_key_beta_clear_stats));
        connectToNewComputer.setOnPreferenceClickListener(this);
    }

    AreYouSureDialogFragment areYouSure;

    @Override
    public boolean onPreferenceClick (Preference preference)
    {
        String key = preference.getKey();
        // do what ever you want with this key
        if (key.equals(getString(R.string.prefs_key_beta_clear_stats))) {
            // are you sure dialog
            areYouSure = new AreYouSureDialogFragment(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrefUtil.savePref(SettingsFragment.this.getActivity().getApplicationContext(), PrefUtil.PREFS_STATS_AREA_KEY, 0);
                    PrefUtil.savePref(SettingsFragment.this.getActivity().getApplicationContext(), PrefUtil.PREFS_STATS_TIME_KEY, 0);
                    if (areYouSure != null) {
                        areYouSure.dismiss();
                    }

                }
            }, null);
            areYouSure.show(getFragmentManager(), "");
        }
        return false;
    }
}
