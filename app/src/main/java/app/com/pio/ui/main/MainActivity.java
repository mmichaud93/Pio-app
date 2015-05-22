package app.com.pio.ui.main;

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

import java.util.ArrayList;

import app.com.pio.R;
import app.com.pio.ui.main.drawer.DrawerAdapter;
import app.com.pio.ui.main.drawer.DrawerHeaderItem;
import app.com.pio.ui.main.drawer.DrawerItem;
import app.com.pio.ui.main.drawer.DrawerListItem;
import app.com.pio.ui.map.PioMapFragment;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
        DrawerLayout.DrawerListener {

    private String[] navBarItems;
    private ArrayList<DrawerItem> items;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private Menu menu;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, PioMapFragment.newInstance())
                    .commit();
        }

        // Set the toolbar as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(Gravity.START)) {
                    drawerLayout.closeDrawers();
                    toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
                    toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                }
            }
        });

        navBarItems = getResources().getStringArray(R.array.nav_bar_items);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerListener(this);
        drawerList = (ListView) findViewById(R.id.drawer_list);

        items = new ArrayList<DrawerItem>();
        items.add(new DrawerHeaderItem("THIS IS A HEADER"));
        for(String s : navBarItems) {
            items.add(new DrawerListItem(s));
        }

        drawerList.setAdapter(new DrawerAdapter(this, items));
        drawerList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0: // profile
                cleanActionBar();
                break;
            case 1: // browse
                cleanActionBar();
                drawerLayout.closeDrawers();
                break;
            case 2: // query
                cleanActionBar();
                drawerLayout.closeDrawers();
                break;
            case 3: // news
                cleanActionBar();
                drawerLayout.closeDrawers();
                break;
            case 4: // random
                cleanActionBar();
                drawerLayout.closeDrawers();
                break;
        }
    }

    private void cleanActionBar() {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Wikidata Explorer");
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
}
