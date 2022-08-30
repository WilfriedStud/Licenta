package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.c2d;

import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab.CloudDataFragment;
import ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab.NotificationsFragment;

public class NotificationCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationCallback.class);

    private final NotificationsFragment notificationsFragment;
    private final CloudDataFragment cloudDataFragment;

    public NotificationCallback(NotificationsFragment notificationsFragment, CloudDataFragment cloudDataFragment) {
        this.notificationsFragment = notificationsFragment;
        this.cloudDataFragment = cloudDataFragment;
    }

    public IotHubMessageResult execute(Message msg, Object context) {

        String message = new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET);
        LOG.info("Invoking execute with message '{}' and context '{}'", message, context);

        if ("Notification".equals(message.substring(0, message.indexOf(':')))) {
            LOG.info("Notification received");
            notificationsFragment.addNotification(message.substring(message.indexOf(':') + 1));
        } else {
            LOG.info("Response received");
            cloudDataFragment.addResponse(message);
        }

        return IotHubMessageResult.COMPLETE;
    }
}