package ro.usv.datacollectionandanalysisoniotsystemsclient.sensor;

import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;
import ro.usv.datacollectionandanalysisoniotsystemsclient.utils.Vector3;

public class SensorAdapterEventCallback extends SensorEventCallback {

    private static final int MAX_SAMPLE_RATE = 5;
    private static final int MAX_SIZE_CACHE = 49;

    private final Map<Long, Vector3> dataCacheByTimeStamp = new ConcurrentHashMap<>();
    private final List<Float>[] rollingAverage = new List[]{
            new ArrayList<>(MAX_SAMPLE_RATE + 1),
            new ArrayList<>(MAX_SAMPLE_RATE + 1),
            new ArrayList<>(MAX_SAMPLE_RATE + 1)
    };
    private final AzureIotHubConnection communicationChannel;
    private final String sensorStringType;

    public SensorAdapterEventCallback(SensorManager sensorManager, int sensorType,
                                      AzureIotHubConnection azureIotHubConnection) {

        this.communicationChannel = azureIotHubConnection;

        if (sensorManager.getSensorList(sensorType).size() > 0) {
            sensorManager.registerListener(
                    this,
                    sensorManager.getDefaultSensor(sensorType),
                    SensorManager.SENSOR_DELAY_UI
            );
            this.sensorStringType = sensorManager.getDefaultSensor(sensorType).getStringType();
        } else {
            this.sensorStringType = "404";
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        float[] data = new float[3];

        for (int i = 0; i < data.length; i++) {
            rollingAverage[i] = roll(rollingAverage[i], event.values[i]);
            data[i] = averageList(rollingAverage[i]);
        }

        if (dataCacheByTimeStamp.size() > MAX_SIZE_CACHE) {
            communicationChannel.send(packAndClearData());
        }
        dataCacheByTimeStamp.put(
                System.currentTimeMillis(),
                new Vector3(data[0], data[1], data[2]));
    }

    private String packAndClearData() {
        String json = "{\n" +
                "      \"sensor-type\": \"" + sensorStringType + "\",\n" +
                "      \"data\": [\n" + stringifyDataAndClearUsedValues() +
                "      ]\n" +
                "    }";
        return json.replaceAll("\\s+", "");
    }

    private String stringifyDataAndClearUsedValues() {
        return dataCacheByTimeStamp
                .entrySet()
                .stream()
                .filter(e -> dataCacheByTimeStamp.remove(e.getKey(), e.getValue()))
                .map(kv ->
                        "        {\n" +
                                "          \"timestamp\": \"" + kv.getKey() + "\",\n" +
                                "          \"data\": " + kv.getValue() + "\n" +
                                "        }")
                .collect(Collectors.joining(","));
    }

    private List<Float> roll(List<Float> list, float newMember) {
        if (list.size() == MAX_SAMPLE_RATE) {
            list.remove(0);
        }
        list.add(newMember);
        return list;
    }

    private float averageList(List<Float> tallyUp) {

        float total = 0;
        for (float item : tallyUp) {
            total += item;
        }
        total = total / tallyUp.size();

        return total;
    }
}

