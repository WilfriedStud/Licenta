package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.twin;


import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceMethodStatusCallback implements IotHubEventCallback {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceMethodStatusCallback.class);

    public void execute(IotHubStatusCode status, Object context) {

        LOG.info("Invoke execute with status {} and context {}", status.name(), context);
    }
}
