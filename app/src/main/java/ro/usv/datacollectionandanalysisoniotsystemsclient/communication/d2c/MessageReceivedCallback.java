package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.d2c;

import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;

public class MessageReceivedCallback implements IotHubEventCallback {
    public void execute(IotHubStatusCode status, Object context) {
        int i = context instanceof Integer ? (Integer) context : 0;
        System.out.println("IoT Hub responded to message " + i + " with status " + status.name());

        if ((status == IotHubStatusCode.OK) || (status == IotHubStatusCode.OK_EMPTY)) {
            System.out.println("Receipts confirmed");
        } else {
            System.out.println("Send failure");
        }
    }
}
