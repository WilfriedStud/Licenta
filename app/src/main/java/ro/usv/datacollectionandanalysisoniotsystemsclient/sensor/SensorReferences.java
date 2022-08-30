package ro.usv.datacollectionandanalysisoniotsystemsclient.sensor;

import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;
import android.widget.TextView;

import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.AzureIotHubConnection;

public class SensorReferences {
    SensorEventCallback sensorEventCallback;

    public SensorReferences withEvent(AzureIotHubConnection communicationChannel,
                                      TextView[] textViews,
                                      boolean disabled, SensorManager sensorManager, int typeGyroscope) {
        this.sensorEventCallback = new SensorAdapterEventCallback(
                sensorManager, textViews,
                typeGyroscope, communicationChannel, disabled);
        return this;
    }
}