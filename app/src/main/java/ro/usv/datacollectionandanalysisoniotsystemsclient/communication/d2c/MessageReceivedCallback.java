package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.d2c;

import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReceivedCallback implements IotHubEventCallback {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceivedCallback.class);

    public void execute(IotHubStatusCode status, Object context) {

        LOG.info("Invoking execute with status '{}' and context '{}'", status.name(), context);
    }
}
