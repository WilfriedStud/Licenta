package ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ro.usv.datacollectionandanalysisoniotsystemsclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CloudData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CloudData extends Fragment {

    public CloudData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CloudData.
     */
    // TODO: Rename and change types and number of parameters
    public static CloudData newInstance() {
        CloudData fragment = new CloudData();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_data, container, false);
    }
}