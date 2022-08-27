package ro.usv.datacollectionandanalysisoniotsystemsclient.sensor;

import static java.util.Objects.isNull;

import android.hardware.Sensor;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;

public class SensorReferences {
    TextView[] textViews;
    SensorEventCallback sensorEventCallback;

    public SensorReferences withEvent(AzureIotHubConnection communicationChannel, SensorManager sensorManager, int typeGyroscope) {
        this.sensorEventCallback = new SensorAdapterEventCallback(sensorManager, typeGyroscope, communicationChannel);
        return this;
    }

    public SensorReferences withTextViews(TextView[] textViews) {
        this.textViews = textViews;
        return this;
    }
}