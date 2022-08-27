package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.twin;


import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;

public class DeviceMethodStatusCallback implements IotHubEventCallback {
    public void execute(IotHubStatusCode status, Object context) {
        System.out.println("IoT Hub responded to device method operation with status " + status.name());
    }
}
