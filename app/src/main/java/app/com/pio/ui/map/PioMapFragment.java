package app.com.pio.ui.map;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import app.com.pio.R;
import app.com.pio.database.MVDatabase;
import butterknife.ButterKnife;

/**
 * Created by mmichaud on 5/22/15.
 */
public class PioMapFragment extends Fragment {

    View root;
    SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private TileOverlay overlay;
    private MaskTileProvider tileProvider;

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
        if(googleMap!=null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setIndoorEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MVDatabase.storePoint((float) latLng.latitude, (float) latLng.longitude);
                    Log.d("PIO", "ll.lat: " + latLng.latitude + ", ll.lon: " + latLng.longitude);
                    overlay.clearTileCache();
                }
            });
            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 14), 1500, null);
                    googleMap.setOnMyLocationChangeListener(null);
                }
            });

            // Create new TileOverlayOptions instance.
            tileProvider = new MaskTileProvider(googleMap);
            //tileProvider.setPoints(points);
            TileOverlayOptions opts = new TileOverlayOptions();
            opts.fadeIn(true);
            // Set the tile provider to your custom implementation.
            opts.tileProvider(tileProvider);
            // Add the tile overlay to the map.
            overlay = googleMap.addTileOverlay(opts);
        }
    }
}
