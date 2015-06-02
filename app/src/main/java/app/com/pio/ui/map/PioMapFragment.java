package app.com.pio.ui.map;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.h2.mvstore.MVStore;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.SpatialKey;

import java.util.Iterator;

import app.com.pio.R;
import butterknife.ButterKnife;

/**
 * Created by mmichaud on 5/22/15.
 */
public class PioMapFragment extends Fragment {

    View root;
    SupportMapFragment mapFragment;
    private GoogleMap googleMap;

    public static PioMapFragment newInstance() {

        return new PioMapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.inject(this, root);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, mapFragment).commit();
        }

        // create an in-memory store
        MVStore s = MVStore.open(null);

        // open an R-tree map
        MVRTreeMap<String> r = s.openMap("data",
                new MVRTreeMap.Builder<String>());

        // add two key-value pairs
        // the first value is the key id (to make the key unique)
        // then the min x, max x, min y, max y
        r.add(new SpatialKey(0, -3f, -2f, 2f, 3f), "left");
        r.add(new SpatialKey(1, 3f, 4f, 4f, 5f), "right");

        // iterate over the intersecting keys
        Iterator<SpatialKey> it =
                r.findIntersectingKeys(new SpatialKey(0, 0f, 9f, 3f, 6f));
        while(it.hasNext()) {
            SpatialKey k = it.next();
            Log.d("PIO", k + ": " + r.get(k));
        }
        s.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleMap == null) {
            googleMap = mapFragment.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setIndoorEnabled(false);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
    }
}
