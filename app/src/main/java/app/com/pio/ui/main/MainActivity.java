package app.com.pio.ui.main;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import app.com.pio.api.PioApiController;
import app.com.pio.ui.map.RecordUtil;
import app.com.pio.ui.settings.SettingsActivity;
import app.com.pio.ui.stats.StatsFragment;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import app.com.pio.R;
import app.com.pio.database.MVDatabase;
import app.com.pio.ui.welcome.WelcomeFragment;
import app.com.pio.ui.main.drawer.DrawerAdapter;
import app.com.pio.ui.main.drawer.DrawerHeaderItem;
import app.com.pio.ui.main.drawer.DrawerItem;
import app.com.pio.ui.main.drawer.DrawerListItem;
import app.com.pio.ui.map.PioMapFragment;
import app.com.pio.ui.profile.PioProfileFragment;
import app.com.pio.utility.PrefUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
        DrawerLayout.DrawerListener {

    private String[] navBarItems;
    private ArrayList<DrawerItem> items;

    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.drawer_list)
    ListView drawerList;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    boolean isInLogin = false;
    int currentPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        PioApiController.initializeController(this);
        MVDatabase.initializeDatabase(this);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        if(PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_TYPE_KEY, null) == null) {
            // we need to login
            initLogin(savedInstanceState);
        } else {
            // continue with app use
            initRegularApp(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MVDatabase.closeDatabase();
    }

    public void initLogin(Bundle savedInstanceState) {
        isInLogin = true;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, WelcomeFragment.newInstance())
                    .commit();
        }
    }

    public void initRegularApp(Bundle savedInstanceState) {
        isInLogin = false;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PioMapFragment.newInstance(null))
                    .commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawers();
                    toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                    toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                }
            }
        });

        navBarItems = getResources().getStringArray(R.array.nav_bar_items);
        drawerLayout.setDrawerListener(this);

        items = new ArrayList<DrawerItem>();
        //get profile object - mock up for now
        //newprofile model ...
        items.add(new DrawerHeaderItem("THIS IS A HEADER", "https://lh3.googleusercontent.com/-uPDbuzhFn2Y/AAAAAAAAAAI/AAAAAAAABdE/tFm-2KDRYyA/s160-c-k-no/photo.jpg"));
        for (String s : navBarItems) {
            items.add(new DrawerListItem(s));
        }

        drawerList.setAdapter(new DrawerAdapter(this, items));
        drawerList.setOnItemClickListener(this);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return !isInLogin;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position == currentPosition) {
            drawerLayout.closeDrawers();
            return;
        }

        switch(position) {
            case 0: // profile
                cleanActionBar();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, PioProfileFragment.newInstance()).commit();
                drawerLayout.closeDrawers();
                break;
            case 1: // map
                cleanActionBar();
                Bundle args = new Bundle();
                if(RecordUtil.isRecording()) {
                    args.putFloat(PioMapFragment.KEY_STRENGTH, RecordUtil.getGpsStrength());
                    args.putFloat(PioMapFragment.KEY_AREA, RecordUtil.getUncoveredKilometersSquared());
                    args.putString(PioMapFragment.KEY_LOCATION, RecordUtil.getLocation());
                    args.putBoolean(PioMapFragment.KEY_RECORDING, RecordUtil.isRecording());
                    args.putLong(PioMapFragment.KEY_START_TIME, RecordUtil.getSessionTimeStart());
                } else {
                    args = null;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container, PioMapFragment.newInstance(args)).commit();
                drawerLayout.closeDrawers();
                break;
            case 2: // achievements
                cleanActionBar();
                drawerLayout.closeDrawers();
                break;
            case 3: // stats
                cleanActionBar();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, StatsFragment.newInstance()).commit();
                drawerLayout.closeDrawers();
                break;
        }

        currentPosition = position;
    }

    private void cleanActionBar() {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle("");
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if(isInLogin) {
            ((WelcomeFragment)getSupportFragmentManager().getFragments().get(0)).onActivityResult(requestCode, responseCode, intent);
        }
    }
}
