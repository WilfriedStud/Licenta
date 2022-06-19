package ro.usv.datacollectionandanalysisoniotsystemsclient;

import static androidx.core.content.ContextCompat.getSystemService;
import static java.util.Objects.isNull;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.send.PacketSender;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalData extends Fragment {

    private SensorManager sensorManager;
    private Set<SensorData> singleValueSensorDataSet;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final PacketSender packetSender = new PacketSender();

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

        singleValueSensorDataSet = new HashSet<>();
        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewAmbientTemperatureData))));

        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewProximity))));

        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewPressure))));

        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewLight))));

        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY))
                .ifPresent(s -> singleValueSensorDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextView(requireView().findViewById(R.id.localViewRelativeHumidity))));
    }

    @Override
    public void onResume() {
        super.onResume();

        singleValueSensorDataSet.forEach(sensorData ->
                sensorManager.registerListener(
                        sensorData.eventCallbackPoller(),
                        sensorData.sensor,
                        SensorManager.SENSOR_DELAY_NORMAL));

        executorService.scheduleWithFixedDelay(() -> {
                    String jsonData = "{\n" +
                            "  \"data-pack\": [\n" +
                            singleValueSensorDataSet
                                    .stream()
                                    .map(SensorData::toString)
                                    .collect(Collectors.joining(",")) +
                            " ]\n" +
                            "}";
                    System.out.println(jsonData.replaceAll("\\s+", ""));
                    packetSender.send(requireContext(), jsonData.replaceAll("\\s+", ""));
                },
                10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        singleValueSensorDataSet.forEach(sensorData -> sensorManager.unregisterListener(sensorData.sensorEventCallback));
        packetSender.stop();
        executorService.shutdown();
    }

    private static class SensorData {
        ConcurrentHashMap<Long, Float> sensorValueByTimeStamp = new ConcurrentHashMap<>();
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

        SensorEventListener eventCallbackPoller() {
            if (isNull(sensorEventCallback)) {
                sensorEventCallback = new SensorEventCallback() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        super.onSensorChanged(event);
                        sensorValueByTimeStamp.put(System.currentTimeMillis(), event.values[0]);
                        textView.setText(String.valueOf(event.values[0]));
                    }
                };
            }
            return sensorEventCallback;
        }

        @NonNull
        @Override
        public String toString() {
            return " {\n" +
                    "      \"sensor-type\": \"" + sensor.getStringType() + "\",\n" +
                    "      \"data\": [\n" + stringifyDataAndClearUsedValues() +
                    "      ]\n" +
                    "    }";
        }

        private String stringifyDataAndClearUsedValues() {
            return sensorValueByTimeStamp
                    .entrySet()
                    .stream()
                    .filter(e -> sensorValueByTimeStamp.remove(e.getKey(), e.getValue()))
                    .map(kv ->
                            "        {\n" +
                                    "          \"timestamp\": \"" + kv.getKey() + "\",\n" +
                                    "          \"data\": \"" + kv.getValue() + "\"\n" +
                                    "        }")
                    .collect(Collectors.joining(","));
        }
    }
}