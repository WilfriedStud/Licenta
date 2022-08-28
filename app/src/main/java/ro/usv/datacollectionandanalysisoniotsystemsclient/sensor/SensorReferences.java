package ro.usv.datacollectionandanalysisoniotsystemsclient.sensor;

import static java.util.Objects.isNull;

import android.hardware.Sensor;
import android.hardware.SensorEventCallback;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;

public class SensorReferences {
    SensorEventCallback sensorEventCallback;

    public SensorReferences withEvent(AzureIotHubConnection communicationChannel,
                                      TextView[] textViews,
                                      SensorManager sensorManager, int typeGyroscope) {
        this.sensorEventCallback = new SensorAdapterEventCallback(
                sensorManager, textViews,
                typeGyroscope, communicationChannel);
        return this;
    }
}