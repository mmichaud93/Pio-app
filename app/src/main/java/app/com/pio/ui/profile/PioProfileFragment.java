package app.com.pio.ui.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.com.pio.R;

/**
 * Created by pilleym on 5/28/2015.
 */
public class PioProfileFragment extends Fragment {
    View root;
    public static PioProfileFragment newInstance()
    {
        return new PioProfileFragment();
    }
 public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
 {
     root = inflater.inflate(R.layout.fragment_profile, container, false);
     return root;

 }
}
