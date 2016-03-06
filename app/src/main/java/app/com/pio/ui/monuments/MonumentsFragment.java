package app.com.pio.ui.monuments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import app.com.pio.R;
import app.com.pio.features.monuments.MonumentManager;
import app.com.pio.ui.monuments.citydetail.CityDetailActivity;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by mmichaud on 7/13/15.
 */
public class MonumentsFragment extends Fragment {

    View root;
    @InjectView(R.id.monument_city_list)
    ListView vCityList;


    public static MonumentsFragment newInstance() {
        return new MonumentsFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_monuments, container, false);
        ButterKnife.inject(this, root);

        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        final ArrayList<CityItem> cityItems = new ArrayList<CityItem>();
//        ArrayList<MonumentItem> bostonItems = new ArrayList<>();
//        bostonItems.add(new MonumentItem("Old North Church", "Boston, MA", true, R.drawable.old_north_church_bw, R.drawable.old_north_church, null));
//        bostonItems.add(new MonumentItem("Fenway Park", "Boston, MA", true, R.drawable.fenway_park_bw, R.drawable.fenway_park, null));
//        bostonItems.add(new MonumentItem("USS Constitution", "Charlestown, MA", false, R.drawable.uss_constitution_bw, R.drawable.uss_constitution, null));
//        bostonItems.add(new MonumentItem("MIT", "Cambridge, MA", false, R.drawable.mit_bw, R.drawable.mit, null));
//        cityItems.add(new CityItem("Boston", false, bostonItems));

        vCityList.setAdapter(new CityAdapter(getActivity(), MonumentManager.cities));
        vCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                intent.putExtra("city", MonumentManager.cities.get(i));
                getActivity().startActivity(intent);
            }
        });

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();

        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Monuments");
    }
}
