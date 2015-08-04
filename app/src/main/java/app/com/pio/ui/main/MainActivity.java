package app.com.pio.ui.main;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import app.com.pio.api.PioApiResponse;
import app.com.pio.api.ProfileResponse;
import app.com.pio.api.PioApiController;
import app.com.pio.features.monuments.MonumentManager;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.map.RecordUtil;
import app.com.pio.ui.monuments.MonumentsFragment;
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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
        DrawerLayout.DrawerListener {

    private ArrayList<DrawerItem> items;
    private DrawerAdapter drawerAdapter;

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
        MonumentManager.initializeMonuments(this);
        ProfileManager.loadActiveProfile(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        if(PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_TYPE_KEY, null) == null ||
                PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_EMAIL_KEY, null) == null ||
                PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_PASSWORD_KEY, null) == null) {
            // we need to login
            initLogin(savedInstanceState);
        } else {
            // continue with app use
            initRegularApp(savedInstanceState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateDrawerHeader();
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

        String[] navBarItems = getResources().getStringArray(R.array.nav_bar_items);
        TypedArray navBarIcons = getResources().obtainTypedArray(R.array.nav_bar_icons);
        drawerLayout.setDrawerListener(this);

        items = new ArrayList<DrawerItem>();
        //get profile object - mock up for now
        //newprofile model ...
        items.add(new DrawerHeaderItem());
        for (int i = 0; i < navBarItems.length; i++) {
            items.add(new DrawerListItem(navBarItems[i], navBarIcons.getResourceId(i, -1)));
        }

        drawerAdapter = new DrawerAdapter(this, items);
        drawerList.setAdapter(drawerAdapter);
        drawerList.setOnItemClickListener(this);

        // login in the background
        PioApiController.loginUser(this,
                PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_EMAIL_KEY, null),
                PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_PASSWORD_KEY, null),
                new Callback<ProfileResponse>() {
                    @Override
                    public void success(ProfileResponse profileResponse, Response response) {
                        Log.d("PIO", "[MainActivity] successfully logged in!");
                        if (profileResponse.getProfile() == null) {
                            return;
                        }
                        if (ProfileManager.activeProfile == null) {
                            ProfileManager.activeProfile = profileResponse.getProfile();
                            ProfileManager.saveActiveProfile();
                            updateDrawerHeader();
                        } else {
                            if (profileResponse.getProfile().getLastUpdated() > ProfileManager.activeProfile.getLastUpdated()) {
                                ProfileManager.activeProfile = profileResponse.getProfile();
                                ProfileManager.saveActiveProfile();
                                updateDrawerHeader();
                            } else {
                                // the profile item on the server is out of data and we need to push the local copy to the server,
                                // this will happen a very large majority of the time
                                PioApiController.pushUser(ProfileManager.activeProfile, new Callback<PioApiResponse>() {
                                    @Override
                                    public void success(PioApiResponse pioApiResponse, Response response) {
                                        updateDrawerHeader();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.d("PIO", "[MainActivity] could not push profile to server");
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("PIO", "[MainActivity] could not log in! Requiring a log in next time.");
                        //PrefUtil.savePref(MainActivity.this, PrefUtil.PREFS_LOGIN_EMAIL_KEY, null);
                        //PrefUtil.savePref(MainActivity.this, PrefUtil.PREFS_LOGIN_PASSWORD_KEY, null);
                    }
                });
    }

    private void updateDrawerHeader() {
        if (drawerAdapter!=null) {
            drawerAdapter.notifyDataSetChanged();
        }
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
            case 2: // monumnets
                cleanActionBar();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, MonumentsFragment.newInstance()).commit();
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
