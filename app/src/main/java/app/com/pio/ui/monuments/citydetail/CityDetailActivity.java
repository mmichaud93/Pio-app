package app.com.pio.ui.monuments.citydetail;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;

import app.com.pio.R;
import app.com.pio.ui.monuments.CityItem;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mmichaud on 7/13/15.
 */
public class CityDetailActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.city_detail_grid)
    GridView vGrid;

    CityItem item;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_detail);
        ButterKnife.inject(this);

        item = (CityItem) getIntent().getExtras().getSerializable("city");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(item.getName());

        vGrid.setAdapter(new MonumentAdapter(this, item.getMonumentItems()));
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
