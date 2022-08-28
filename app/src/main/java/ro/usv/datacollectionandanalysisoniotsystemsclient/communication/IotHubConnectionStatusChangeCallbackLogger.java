package ro.usv.datacollectionandanalysisoniotsystemsclient.communication;

import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IotHubConnectionStatusChangeCallbackLogger implements IotHubConnectionStatusChangeCallback {

    private static final Logger LOG = LoggerFactory.getLogger(IotHubConnectionStatusChangeCallbackLogger.class);

    @Override
    public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason statusChangeReason, Throwable throwable, Object callbackContext) {

        LOG.info("CONNECTION STATUS UPDATE '{}'", status);
        LOG.info("CONNECTION STATUS REASON '{}'", statusChangeReason);
        LOG.info("CONNECTION STATUS THROWABLE '{}'", (throwable == null ? "null" : throwable.getMessage()));

        if (throwable != null) {
            throwable.printStackTrace();
        }

        switch (status) {
            case DISCONNECTED:
                //connection was lost, and is not being re-established. Look at provided exception for
                // how to resolve this issue. Cannot send messages until this issue is resolved, and you manually
                // re-open the device client
                LOG.info("Disconnected");
                break;
            case DISCONNECTED_RETRYING:
                //connection was lost, but is being re-established. Can still send messages, but they won't
                // be sent until the connection is re-established
                LOG.info("Disconnected retrying");
                break;
            case CONNECTED:
                //Connection was successfully re-established. Can send messages.
                LOG.info("Connected");
                break;
            default:
                LOG.info("Unknown");
        }
    }
}