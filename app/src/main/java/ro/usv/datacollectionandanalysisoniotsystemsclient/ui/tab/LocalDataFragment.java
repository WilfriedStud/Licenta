package ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab;

import static androidx.core.content.ContextCompat.getSystemService;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashSet;
import java.util.Set;

import ro.usv.datacollectionandanalysisoniotsystemsclient.R;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;
import ro.usv.datacollectionandanalysisoniotsystemsclient.sensor.SensorReferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalDataFragment extends Fragment {

    private SensorManager sensorManager;

    private final Set<SensorReferences> sensorReferenceStore = new HashSet<>();

    public LocalDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocalData.
     */
    public static LocalDataFragment newInstance() {
        LocalDataFragment fragment = new LocalDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = getSystemService(requireContext(), SensorManager.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sensorReferenceStore.add(new SensorReferences()
                .withEvent(AzureIotHubConnection.getInstance(),
                        new TextView[]{
                                requireView().findViewById(R.id.localViewGyroX),
                                requireView().findViewById(R.id.localViewGyroY),
                                requireView().findViewById(R.id.localViewGyroZ),
                        },
                        true,
                        sensorManager, Sensor.TYPE_GYROSCOPE));

        sensorReferenceStore.add(new SensorReferences()
                .withEvent(AzureIotHubConnection.getInstance(),
                        new TextView[]{
                                requireView().findViewById(R.id.localViewAccelX),
                                requireView().findViewById(R.id.localViewAccelY),
                                requireView().findViewById(R.id.localViewAccelZ),
                        },
                        false,
                        sensorManager, Sensor.TYPE_ACCELEROMETER));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorReferenceStore.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_data, container, false);
    }

}