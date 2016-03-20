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
import app.com.pio.features.friends.FriendsDBHelper;
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

        vCityList.setAdapter(new CityAdapter(getActivity(), MonumentManager.getOrderedCities()));
        vCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), CityDetailActivity.class);
                intent.putExtra("city", MonumentManager.cities.get(i));
                if (getActivity() != null) {
                    getActivity().startActivity(intent);
                }
            }
        });

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
//        FriendsDBHelper dtemp = new FriendsDBHelper(getActivity());
//        dtemp.dropTable();
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("Monuments");
    }
}
