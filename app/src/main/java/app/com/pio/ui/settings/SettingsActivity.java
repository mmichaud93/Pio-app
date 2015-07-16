package app.com.pio.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import app.com.pio.R;
import app.com.pio.ui.map.PioMapFragment;
import app.com.pio.utility.AreYouSureDialogFragment;
import app.com.pio.utility.PrefUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mmichaud on 7/13/15.
 */
public class SettingsActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, SettingsFragment.newInstance())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
