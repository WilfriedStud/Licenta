package ro.usv.datacollectionandanalysisoniotsystemsclient;

import static androidx.core.content.ContextCompat.getSystemService;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
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
import java.util.Optional;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalData extends Fragment {

    private SensorManager mSensorManager;

    private Set<SensorData> singleValueSensorDataSet;

    private static class SensorData {
        Sensor sensor;
        TextView textView;
        SensorEventCallback sensorEventCallback;

        SensorData withSensor(Sensor sensor) {
            this.sensor = sensor;
            return this;
        }

        SensorData withTextView(TextView textView) {
            this.textView = textView;
            return this;
        }

        SensorData withSensorEventCallback(SensorEventCallback sensorEventCallback) {
            this.sensorEventCallback = sensorEventCallback;
            return this;
        }
    }

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
        mSensorManager = getSystemService(requireContext(), SensorManager.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_data, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        singleValueSensorDataSet = new HashSet<>();
        Optional.ofNullable(mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewAmbientTemperatureData))));

        Optional.ofNullable(mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewProximity))));

        Optional.ofNullable(mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewPressure))));

        Optional.ofNullable(mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewLight))));

        Optional.ofNullable(mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewRelativeHumidity))));

        registerSensorEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensorEvents();
    }

    @Override
    public void onPause() {
        super.onPause();
        singleValueSensorDataSet.forEach(sensorData -> mSensorManager.unregisterListener(sensorData.sensorEventCallback));
    }

    private void registerSensorEvents() {
        singleValueSensorDataSet.forEach(sensorData -> mSensorManager.registerListener(sensorData.withSensorEventCallback(new SensorEventCallback() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                super.onSensorChanged(event);
                sensorData.textView.setText(String.valueOf(event.values[0]));
            }
        }).sensorEventCallback, sensorData.sensor, SensorManager.SENSOR_DELAY_NORMAL));
    }
}