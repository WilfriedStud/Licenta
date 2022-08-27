package ro.usv.datacollectionandanalysisoniotsystemsclient.communication;

import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;

public class IotHubConnectionStatusChangeCallbackLogger implements IotHubConnectionStatusChangeCallback {
    @Override
    public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason statusChangeReason, Throwable throwable, Object callbackContext) {
        System.out.println();
        System.out.println("CONNECTION STATUS UPDATE: " + status);
        System.out.println("CONNECTION STATUS REASON: " + statusChangeReason);
        System.out.println("CONNECTION STATUS THROWABLE: " + (throwable == null ? "null" : throwable.getMessage()));
        System.out.println();

        if (throwable != null) {
            throwable.printStackTrace();
        }

        switch (status) {
            case DISCONNECTED:
                //connection was lost, and is not being re-established. Look at provided exception for
                // how to resolve this issue. Cannot send messages until this issue is resolved, and you manually
                // re-open the device client
                System.out.println("Disconnected");
                break;
            case DISCONNECTED_RETRYING:
                //connection was lost, but is being re-established. Can still send messages, but they won't
                // be sent until the connection is re-established
                System.out.println("Disconnected retrying");
                break;
            case CONNECTED:
                //Connection was successfully re-established. Can send messages.
                System.out.println("Connected");
                break;
            default:
                System.out.println("Unknown");
        }
    }
}