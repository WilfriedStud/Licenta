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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.send.PacketSender;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalData extends Fragment {

    private SensorManager sensorManager;
    private Set<SensorData> sensorAverageDataSet;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
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

        sensorAverageDataSet = new HashSet<>();
        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE))
                .ifPresent(s -> sensorAverageDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextViews(new TextView[]{
                                requireView().findViewById(R.id.localViewGyroX),
                                requireView().findViewById(R.id.localViewGyroY),
                                requireView().findViewById(R.id.localViewGyroZ),
                        })));

        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
                .ifPresent(s -> sensorAverageDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextViews(new TextView[]{
                                requireView().findViewById(R.id.localViewAccelX),
                                requireView().findViewById(R.id.localViewAccelY),
                                requireView().findViewById(R.id.localViewAccelZ),
                        })));

        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
                .ifPresent(s -> sensorAverageDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextViews(new TextView[]{
                                requireView().findViewById(R.id.localViewMagneticX),
                                requireView().findViewById(R.id.localViewMagneticY),
                                requireView().findViewById(R.id.localViewMagneticZ)
                        })));


        Optional.ofNullable(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY))
                .ifPresent(s -> sensorAverageDataSet.add(new SensorData()
                        .withSensor(s)
                        .withTextViews(new TextView[]{
                                requireView().findViewById(R.id.localViewProximity),
                        })));
    }

    @Override
    public void onStart() {
        super.onStart();
        startActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        startActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopActivity();
    }

    private void stopActivity() {
        sensorAverageDataSet.forEach(sensorData -> sensorManager.unregisterListener(sensorData.sensorEventCallback));
        packetSender.stop();
        executorService.shutdown();
    }

    private void startActivity() {
        sensorAverageDataSet.forEach(sensorData ->
                sensorManager.registerListener(
                        sensorData.eventCallbackPoller(),
                        sensorData.sensor,
                        SensorManager.SENSOR_DELAY_UI));

        executorService.scheduleWithFixedDelay(() -> {
            String jsonData = "{\n" +
                    "  \"data-pack\": [\n" +
                    sensorAverageDataSet
                            .stream()
                            .map(SensorData::toString)
                            .collect(Collectors.joining(",")) +
                    " ]\n" +
                    "}";
            System.out.println(jsonData.replaceAll("\\s+", ""));
            packetSender.send(requireContext(), jsonData.replaceAll("\\s+", ""));
        }, 10, 10, TimeUnit.SECONDS);

        executorService.scheduleWithFixedDelay(() ->
                sensorAverageDataSet.forEach(sensorData -> {

                    float totalX = sensorData.totalX.getAndSet(0);
                    float totalY = sensorData.totalY.getAndSet(0);
                    float totalZ = sensorData.totalZ.getAndSet(0);
                    int countX = sensorData.countX.getAndSet(0);
                    int countY = sensorData.countY.getAndSet(0);
                    int countZ = sensorData.countZ.getAndSet(0);

                    if (countX > 0) {
                        sensorData.sensorValueByTimeStamp.put(
                                System.currentTimeMillis(), new SensorData.Vector3(
                                        countZ > 0 ? totalX / countX : 0,
                                        countZ > 0 ? totalY / countY : 0,
                                        countZ > 0 ? totalZ / countZ : 0));

                        if (sensorData.textViews.length > 0) {
                            sensorData.textViews[0].setText(String.valueOf((float) (countZ > 0 ? totalX / countX : 0)));
                        }
                        if (sensorData.textViews.length > 1) {
                            sensorData.textViews[1].setText(String.valueOf((float) (countZ > 0 ? totalY / countY : 0)));
                        }
                        if (sensorData.textViews.length > 2) {
                            sensorData.textViews[2].setText(String.valueOf((float) (countZ > 0 ? totalZ / countZ : 0)));
                        }
                    }
                }), 500, 500, TimeUnit.MILLISECONDS);
    }

    private static class SensorData {
        ConcurrentHashMap<Long, Vector3> sensorValueByTimeStamp = new ConcurrentHashMap<>();
        Sensor sensor;
        TextView[] textViews;
        SensorEventCallback sensorEventCallback;

        AtomicFloat totalX = new AtomicFloat(), totalY = new AtomicFloat(), totalZ = new AtomicFloat();
        AtomicInteger countX = new AtomicInteger(), countY = new AtomicInteger(), countZ = new AtomicInteger();

        SensorData withSensor(Sensor sensor) {
            this.sensor = sensor;
            return this;
        }

        SensorData withTextViews(TextView[] textViews) {
            this.textViews = textViews;
            return this;
        }

        SensorEventListener eventCallbackPoller() {

            if (isNull(sensorEventCallback)) {
                sensorEventCallback = new SensorEventCallback() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        super.onSensorChanged(event);

                        if (event.values.length > 0) {
                            totalX.addAndGet(event.values[0]);
                            countX.incrementAndGet();
                        }
                        if (event.values.length > 1) {
                            totalY.addAndGet(event.values[1]);
                            countY.incrementAndGet();
                        }
                        if (event.values.length > 2) {
                            totalZ.addAndGet(event.values[2]);
                            countZ.incrementAndGet();
                        }
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

        String stringifyDataAndClearUsedValues() {
            return sensorValueByTimeStamp
                    .entrySet()
                    .stream()
                    .filter(e -> sensorValueByTimeStamp.remove(e.getKey(), e.getValue()))
                    .map(kv ->
                            "        {\n" +
                                    "          \"timestamp\": \"" + kv.getKey() + "\",\n" +
                                    "          \"data\": " + kv.getValue() + "\n" +
                                    "        }")
                    .collect(Collectors.joining(","));
        }

        private static class Vector3 {
            final float x;
            final float y;
            final float z;

            public Vector3(float x, float y, float z) {
                this.x = x;
                this.y = y;
                this.z = z;
            }

            @NonNull
            @Override
            public String toString() {
                return "{\n" +
                        "  \"x\" : \"" + x + "\",\n" +
                        "  \"y\" : \"" + y + "\",\n" +
                        "  \"z\" : \"" + z + "\"\n" +
                        "}";
            }
        }
    }
}