package ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ro.usv.datacollectionandanalysisoniotsystemsclient.R;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CloudDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CloudDataFragment extends Fragment {


    public CloudDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CloudData.
     */
    public static CloudDataFragment newInstance() {
        CloudDataFragment fragment = new CloudDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    public void addResponse(String response) {
    }

    public void setDate(int year, int month, int day) {
    }


    @Override
    public void onStart() {
        super.onStart();
        requireView().findViewById(R.id.refreshButton).setOnClickListener(v -> {
            AzureIotHubConnection.getInstance().send(message(requireView().findViewById(R.id.calendarView)));
        });
    }

    private String message(CalendarView calendarView) {

        return "{\n" +
                "  \"sensorType\" : \"Request\",\n" +
                "  \"requestParams\" : \"" + toDate(calendarView.getDate()) + "\"\n" +
                "}";
    }

    private String toDate(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date startRange = new Date(timestamp);
        return formatter.format(startRange);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_data, container, false);
    }

}