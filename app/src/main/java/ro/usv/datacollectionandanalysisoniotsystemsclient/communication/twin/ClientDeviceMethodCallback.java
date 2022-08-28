package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.twin;

import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientDeviceMethodCallback implements DeviceMethodCallback {

    private static final Logger LOG = LoggerFactory.getLogger(ClientDeviceMethodCallback.class);
    private static final int METHOD_SUCCESS = 200;
    private static final int METHOD_THROWS = 403;
    private static final int METHOD_NOT_DEFINED = 404;

    @Override
    public DeviceMethodData call(String methodName, Object methodData, Object context) {

        LOG.info("Invoking call with methodName '{}' and methodData '{}' and context '{}'",
                methodData, methodData, context);

        DeviceMethodData deviceMethodData;
        int status;
        try {
            if ("setSendMessagesInterval".equals(methodName)) {
                status = method_setSendMessagesInterval(methodData);
            } else {
                status = method_default(methodData);
            }
            deviceMethodData = new DeviceMethodData(status, "executed " + methodName);
        } catch (Exception e) {
            LOG.info("Exception in method call", e);
            status = METHOD_THROWS;
            deviceMethodData = new DeviceMethodData(status, "Method Throws " + methodName);
        }
        return deviceMethodData;
    }

    private int method_setSendMessagesInterval(Object methodData) {

        LOG.info("Invoked method_setSendMessagesInterval {}", methodData);

        return METHOD_SUCCESS;
    }

    private int method_default(Object data) {

        LOG.info("Invoked method_default {}", data);

        return METHOD_NOT_DEFINED;
    }
}