package ro.usv.datacollectionandanalysisoniotsystemsclient;

import static androidx.core.content.ContextCompat.getSystemService;
import static java.util.Objects.isNull;

import android.hardware.Sensor;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotCommunication;
import ro.usv.datacollectionandanalysisoniotsystemsclient.utils.AndroidSensorCommunicationChannel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalData extends Fragment {

    private SensorManager sensorManager;
    private Set<SensorReferences> sensorReferenceStore = new HashSet<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
    private AzureIotCommunication communicationChannel;

    public LocalData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocalData.
     */
    public static LocalData newInstance() {
        LocalData fragment = new LocalData();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        communicationChannel = new AzureIotCommunication(requireContext());
        sensorReferenceStore.add(new SensorReferences()
                .withEvent(communicationChannel, sensorManager, Sensor.TYPE_GYROSCOPE)
                .withTextViews(new TextView[]{
                        requireView().findViewById(R.id.localViewGyroX),
                        requireView().findViewById(R.id.localViewGyroY),
                        requireView().findViewById(R.id.localViewGyroZ),
                }));

        sensorReferenceStore.add(new SensorReferences()
                .withEvent(communicationChannel, sensorManager, Sensor.TYPE_ACCELEROMETER)
                .withTextViews(new TextView[]{
                        requireView().findViewById(R.id.localViewAccelX),
                        requireView().findViewById(R.id.localViewAccelY),
                        requireView().findViewById(R.id.localViewAccelZ),
                }));
    }

    private static class SensorReferences {
        TextView[] textViews;
        SensorEventCallback sensorEventCallback;

        SensorReferences withEvent(AzureIotCommunication communicationChannel, SensorManager sensorManager, int typeGyroscope) {
            this.sensorEventCallback = new AndroidSensorCommunicationChannel(sensorManager, typeGyroscope, communicationChannel);
            return this;
        }

        SensorReferences withTextViews(TextView[] textViews) {
            this.textViews = textViews;
            return this;
        }

        SensorEventListener eventCallbackPoller() {

            if (isNull(sensorEventCallback)) {
                throw new IllegalArgumentException("Handler is null");
            }
            return sensorEventCallback;
        }
    }
}