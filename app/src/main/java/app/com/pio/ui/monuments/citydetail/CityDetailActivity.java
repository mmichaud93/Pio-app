package app.com.pio.ui.monuments.citydetail;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.pio.R;
import app.com.pio.features.profiles.ProfileManager;
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

    AlertDialog dialog;

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
        vGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CityDetailActivity.this);
                LayoutInflater inflater = CityDetailActivity.this.getLayoutInflater();
                View root = inflater.inflate(R.layout.dialog_monument_detail, null);

                ImageView monumentImage = (ImageView) root.findViewById(R.id.monument_detail_image);
                if(ProfileManager.monumentIsUnlocked(item.getMonumentItems().get(i).getId())) {
                    monumentImage.setImageResource(item.getMonumentItems().get(i).getBitmapUnlockedLarge());
                } else {
                    monumentImage.setImageResource(item.getMonumentItems().get(i).getBitmapLockedLarge());
                }
                TextView monumentName = (TextView) root.findViewById(R.id.monument_detail_name);
                monumentName.setText(item.getMonumentItems().get(i).getName());
                TextView monumentLocation = (TextView) root.findViewById(R.id.monument_detail_location);
                monumentLocation.setText(item.getMonumentItems().get(i).getLocation());
                Button okayButton = (Button) root.findViewById(R.id.monument_detail_okay);
                okayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialog!=null) {
                            dialog.cancel();
                        }
                    }
                });

                builder.setView(root);
                dialog = builder.create();
                dialog.show();
            }
        });
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
