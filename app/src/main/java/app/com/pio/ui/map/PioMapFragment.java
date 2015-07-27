package app.com.pio.ui.map;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import app.com.pio.R;
import app.com.pio.api.PioApiController;
import app.com.pio.api.PioApiResponse;
import app.com.pio.database.MVDatabase;
import app.com.pio.features.monuments.MonumentManager;
import app.com.pio.features.profiles.ProfileManager;
import app.com.pio.service.LocationUpdateService;
import app.com.pio.ui.monuments.CityItem;
import app.com.pio.ui.monuments.MonumentItem;
import app.com.pio.utility.AnimUtil;
import app.com.pio.utility.Util;
import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mmichaud on 5/22/15.
 */
public class PioMapFragment extends Fragment {

    private static final String TAG = "PioMapFragment";

    public static String KEY_RECORDING = "isRecording";
    public static String KEY_LOCATION = "location";
    public static String KEY_START_TIME = "startTime";
    public static String KEY_STRENGTH = "strength";
    public static String KEY_AREA = "area";

    View root;
    SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private TileOverlay overlay;
    private MaskTileProvider tileProvider;
    Geocoder geocoder;

    @InjectView(R.id.map_record)
    RelativeLayout record;
    @InjectView(R.id.map_record_button)
    Button recordButton;
    @InjectView(R.id.map_record_image)
    ImageView recordImage;
    @InjectView(R.id.map_dashboard)
    RelativeLayout dashboard;
    @InjectView(R.id.map_gps_bar_1)
    View bar1;
    @InjectView(R.id.map_gps_bar_2)
    View bar2;
    @InjectView(R.id.map_gps_bar_3)
    View bar3;
    @InjectView(R.id.map_gps_bar_4)
    View bar4;
    @InjectView(R.id.map_dashboard_session_time)
    TextView sessionTime;
    @InjectView(R.id.map_dashboard_area)
    TextView areaText;
    @InjectView(R.id.map_dashboard_location)
    TextView locationText;

    DecimalFormat areaFormat = new DecimalFormat("#.###");

    public static PioMapFragment newInstance(Bundle args) {
        PioMapFragment mapFragment = new PioMapFragment();
        mapFragment.setArguments(args);
        return mapFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.inject(this, root);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // make a dialog to enable gps
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Your GPS is disabled, do you want to enable it now?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                    dialog.cancel();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    if (RecordUtil.isRecording()) {
                        // stop recording
                        RecordUtil.stopRecording(getActivity());
                        stopRecordingVisual();
                        slideDashboardDown();
                    } else {
                        // start recording
                        RecordUtil.startRecording(getActivity(), dashboard);
                        startRecordingVisuals();
                        sessionTime.setText(Util.formatLongToTime(0));
                        areaText.setText(getString(R.string.area_km, areaFormat.format(RecordUtil.getUncoveredKilometersSquared())));
                        slideDashboardUp(AnimUtil.animationSpeed);
                    }
                }
            }
        });

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        manager.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int i) {
                if (getActivity() != null) {
                    if (i == GpsStatus.GPS_EVENT_STARTED) {
                        if (RecordUtil.isRecording()) {
                            startRecordingVisuals();
                        } else {
                            stopRecordingVisual();
                        }

                    } else if (i == GpsStatus.GPS_EVENT_STOPPED) {
                        warnVisuals();
                    }
                }
            }
        });
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            warnVisuals();
        }

        if(savedInstanceState!=null && savedInstanceState.getBoolean(KEY_RECORDING)) {
            RecordUtil.startRecording(getActivity(), dashboard);
            RecordUtil.setLocation(savedInstanceState.getString(KEY_LOCATION));
            RecordUtil.setGpsStrength(savedInstanceState.getFloat(KEY_STRENGTH));
            RecordUtil.setSessionTimeStart(savedInstanceState.getLong(KEY_START_TIME));
            RecordUtil.setUncoveredKilometersSquared(savedInstanceState.getFloat(KEY_AREA));
            startRecordingVisuals();
            sessionTime.setText(Util.formatLongToTime(RecordUtil.getRecordingSessionTime()));
            areaText.setText(getString(R.string.area_km, areaFormat.format(RecordUtil.getUncoveredKilometersSquared())));
            locationText.setText(RecordUtil.getLocation());
            slideDashboardUp(0);
        } else if(getArguments()!=null) {
            RecordUtil.startRecording(getActivity(), dashboard);
            RecordUtil.setLocation(getArguments().getString(KEY_LOCATION));
            RecordUtil.setGpsStrength(getArguments().getFloat(KEY_STRENGTH));
            RecordUtil.setSessionTimeStart(getArguments().getLong(KEY_START_TIME));
            RecordUtil.setUncoveredKilometersSquared(getArguments().getFloat(KEY_AREA));
            startRecordingVisuals();
            sessionTime.setText(Util.formatLongToTime(RecordUtil.getRecordingSessionTime()));
            areaText.setText(getString(R.string.area_km, areaFormat.format(RecordUtil.getUncoveredKilometersSquared())));
            locationText.setText(RecordUtil.getLocation());
            slideDashboardUp(0);
        }

        return root;
    }

    private void startRecordingVisuals() {
        recordButton.setBackgroundResource(R.drawable.record_button_stop);
        recordImage.setImageResource(R.drawable.ic_stop);
        recordImage.setPadding(0, 0, 0, 0);
    }

    private void stopRecordingVisual() {
        recordButton.setBackgroundResource(R.drawable.record_button_start);
        recordImage.setImageResource(R.drawable.ic_play);
        recordImage.setPadding((int) Util.dpToPx(5, getActivity()), 0, 0, 0);
    }

    private void warnVisuals() {
        recordButton.setBackgroundResource(R.drawable.record_button_warn);
        recordImage.setImageResource(R.drawable.ic_warning);
        recordImage.setPadding(0, 0, 0, 0);
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
        overlay.clearTileCache();

        if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().getExtras().containsKey(LocationUpdateService.SESSION_KEY)) {
            Bundle sessionData = getActivity().getIntent().getBundleExtra(LocationUpdateService.SESSION_KEY);
            float area = sessionData.getFloat(LocationUpdateService.AREA_KEY);
            RecordUtil.startRecording(getActivity(), dashboard);
            RecordUtil.setUncoveredKilometersSquared(area);
            recordButton.setBackgroundResource(R.drawable.record_button_stop);
            recordImage.setImageResource(R.drawable.ic_stop);
            recordImage.setPadding(0, 0, 0, 0);
            sessionTime.setText(Util.formatLongToTime(0));
            areaText.setText(getString(R.string.area_km, areaFormat.format(RecordUtil.getUncoveredKilometersSquared())));
            slideDashboardUp(0);
            getActivity().getIntent().getExtras().clear();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // the fragment is on it's way out, so create the service
        //stopTimer();
        //RecordUtil.stopRecording(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {


        savedInstanceState.putBoolean(KEY_RECORDING, RecordUtil.isRecording());
        if (RecordUtil.isRecording()) {
            savedInstanceState.putLong(KEY_START_TIME, RecordUtil.getSessionTimeStart());
            savedInstanceState.putString(KEY_LOCATION, RecordUtil.getLocation());
            savedInstanceState.putFloat(KEY_AREA, RecordUtil.getUncoveredKilometersSquared());
            savedInstanceState.putFloat(KEY_STRENGTH, RecordUtil.getGpsStrength());
        }


        super.onSaveInstanceState(savedInstanceState);
    }

    LatLng firstLatLng = null;
    long getLocationTime = 0;
    private void setUpMap() {
        if (googleMap != null) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            googleMap.setIndoorEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
//                    if (firstLatLng==null) {
//                        firstLatLng = latLng;
//                        return;
//                    }
//                    firstLatLng = new LatLng(latLng.latitude+0.005, latLng.longitude);
//                    for(int i = 0; i < 1000; i++) {
//                        MVDatabase.storePoint((float) (firstLatLng.latitude+(latLng.latitude - firstLatLng.latitude)/1000*i)
//                                , (float) (firstLatLng.longitude+(latLng.longitude - firstLatLng.longitude)/1000*i), true);
//                    }
//                    firstLatLng = new LatLng(latLng.latitude, latLng.longitude+0.005);
//                    for(int i = 0; i < 1000; i++) {
//                        MVDatabase.storePoint((float) (firstLatLng.latitude+(latLng.latitude - firstLatLng.latitude)/1000*i)
//                                , (float) (firstLatLng.longitude+(latLng.longitude - firstLatLng.longitude)/1000*i), true);
//                    }
//                    firstLatLng = null;
//                    Log.d("PIO", "ll.lat: " + latLng.latitude + ", ll.lon: " + latLng.longitude);
                    overlay.clearTileCache();
                }
            });
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if(location!=null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 16));
            }

            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 16));
                    googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            RecordUtil.setGpsStrength(location.getAccuracy());
                            if (location.getAccuracy() <= 10) {
                                bar4.setBackgroundResource(R.drawable.gps_accuracy_full);
                            } else {
                                bar4.setBackgroundResource(R.drawable.gps_accuracy_empty);
                            }
                            if (location.getAccuracy() <= 20) {
                                bar3.setBackgroundResource(R.drawable.gps_accuracy_full);
                            } else {
                                bar3.setBackgroundResource(R.drawable.gps_accuracy_empty);
                            }
                            if (location.getAccuracy() <= 30) {
                                bar2.setBackgroundResource(R.drawable.gps_accuracy_full);
                            } else {
                                bar2.setBackgroundResource(R.drawable.gps_accuracy_empty);
                            }
                            if (location.getAccuracy() <= 40) {
                                bar1.setBackgroundResource(R.drawable.gps_accuracy_full);
                            } else {
                                bar1.setBackgroundResource(R.drawable.gps_accuracy_empty);
                            }

                            List<Address> addresses;

                            if (getLocationTime < System.currentTimeMillis()) {
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    if (addresses.size() > 0) {
                                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        locationText.setText(address);
                                        RecordUtil.setLocation(address);
                                    } else {
                                        locationText.setText(getString(R.string.unknown));
                                        RecordUtil.setLocation(getString(R.string.unknown));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    getLocationTime = System.currentTimeMillis()+15000;
                                }
                            }

                            areaText.setText(getString(R.string.area_km, areaFormat.format(RecordUtil.getUncoveredKilometersSquared())));
                        }
                    });
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

            for(CityItem city : MonumentManager.cities) {
                for(MonumentItem monument: city.getMonumentItems()) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(monument.getPinLat(), monument.getPinLong()))
                            .title(monument.getName()));
                }
            }
        }

    }

    private void slideDashboardUp(final int speed) {

        if(root.getHeight()==0) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(getActivity()!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slideDashboardUp(speed);
                            }
                        });
                    }
                }
            }, 250);
        } else {
            dashboard.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(0).y(root.getHeight()).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    dashboard.setAlpha(0);
                    dashboard.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    dashboard.setAlpha(1);
                    dashboard.animate().y(root.getHeight() - Util.dpToPx(96, getActivity())).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(speed).setListener(AnimUtil.blankAnimationListener).start();
                    record.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(speed).y(root.getHeight() - Util.dpToPx(142, getActivity())).start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            }).start();
        }
    }

    private void slideDashboardDown() {

        record.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(AnimUtil.animationSpeed).y(root.getHeight() - Util.dpToPx(92, getActivity())).start();

        dashboard.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(AnimUtil.animationSpeed).y(root.getHeight()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                dashboard.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        }).start();
    }
}
