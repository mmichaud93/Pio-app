package app.com.pio.ui.main;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;

import app.com.pio.api.PioApiResponse;
import app.com.pio.api.ProfileResponse;
import app.com.pio.api.PioApiController;
import app.com.pio.features.monuments.MonumentManager;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.ui.map.RecordUtil;
import app.com.pio.ui.monuments.MonumentsFragment;
import app.com.pio.ui.settings.SettingsActivity;
import app.com.pio.ui.friends.FriendsFragment;
import app.com.pio.utility.Util;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import app.com.pio.R;
import app.com.pio.database.MVDatabase;
import app.com.pio.ui.welcome.WelcomeFragment;
import app.com.pio.ui.main.drawer.DrawerAdapter;
import app.com.pio.ui.main.drawer.DrawerItem;
import app.com.pio.ui.map.PioMapFragment;
import app.com.pio.ui.profile.PioProfileFragment;
import app.com.pio.utility.PrefUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.main_level_parent)
    LinearLayout mainLevelParent;
    @InjectView(R.id.main_level_text)
    TextView mainLevelText;
    @InjectView(R.id.main_level_progress)
    TextView mainLevelProgress;
    @InjectView(R.id.main_level_bar_parent)
    FrameLayout mainLevelBarParent;
    @InjectView(R.id.main_level_bar)
    View mainLevelBar;

    boolean isInLogin = false;
    int currentPosition = 1;

    public static RequestQueue queue;

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
        FacebookSdk.sdkInitialize(getApplicationContext());
        queue = Volley.newRequestQueue(this);
        queue.start();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {

                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setTitle("Pio");
                }
            }
        });

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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isInLogin) {
            pushProfileData();
        }

        updatePlayerViews();
    }

    @Override
    protected void onStop() {
        super.onStop();

        ProfileManager.saveActiveProfile(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MVDatabase.closeDatabase();
    }

    public void initLogin(Bundle savedInstanceState) {
        isInLogin = true;
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, WelcomeFragment.newInstance())
                    .commit();
        }
    }

    public void initRegularApp(Bundle savedInstanceState) {
        isInLogin = false;

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, PioMapFragment.newInstance(null))
                    .commit();
        }

        // login in the background
        mainLevelParent.setVisibility(View.VISIBLE);
        PioApiController.loginUser(this,
                PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_EMAIL_KEY, null),
                PrefUtil.getPref(this, PrefUtil.PREFS_LOGIN_PASSWORD_KEY, null),
                new Callback<ProfileResponse>() {
                    @Override
                    public void success(ProfileResponse profileResponse, Response response) {
                        updatePlayerViews();
                        if (profileResponse.getProfile() == null) {
                            return;
                        }
                        if (ProfileManager.activeProfile == null) {
                            ProfileManager.activeProfile = profileResponse.getProfile();
                            ProfileManager.saveActiveProfile();
                        } else {
                            if (profileResponse.getProfile().getLastUpdated() > ProfileManager.activeProfile.getLastUpdated()) {
                                ProfileManager.activeProfile = profileResponse.getProfile();
                                ProfileManager.saveActiveProfile();
                            } else {
                                // the profile item on the server is out of data and we need to push the local copy to the server,
                                // this will happen a very large majority of the time
                                pushProfileData();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        updatePlayerViews();
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);
        updatePlayerViews();
    }

    private void updatePlayerViews() {

        if (ProfileManager.activeProfile != null) {
            mainLevelText.setText("Level: " + Util.getLevelFromXP(ProfileManager.activeProfile.getXp()));
            mainLevelProgress.setText((int)(Util.getExcessXP(ProfileManager.activeProfile.getXp()) * 100) + "%");

            mainLevelBar.setLayoutParams(
                    new FrameLayout.LayoutParams(
                            (int) (mainLevelBarParent.getWidth() * Util.getExcessXP(ProfileManager.activeProfile.getXp())),
                            (int) Util.dpToPx(12, this)));
        }
    }

    public void pushProfileData() {
        PioApiController.pushUser(ProfileManager.activeProfile, new Callback<PioApiResponse>() {
            @Override
            public void success(PioApiResponse pioApiResponse, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
                // fail quietly because we can just push this later, no biggie
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return !isInLogin;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if(isInLogin) {
            ((WelcomeFragment)getSupportFragmentManager().getFragments().get(0)).onActivityResult(requestCode, responseCode, intent);
            return;
        }
        // this is going to cause so many problems in the future but I have other things to do now.
        // It's like this because we need to pass the activity result onto the fragments, in this case specifically the friends fragment
        // for the Facebook login button.
        if (getSupportFragmentManager().getFragments().size() > 1) {
            getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getFragments().size()-1).onActivityResult(requestCode, responseCode, intent);
        }
        super.onActivityResult(requestCode, responseCode, intent);
    }
}
