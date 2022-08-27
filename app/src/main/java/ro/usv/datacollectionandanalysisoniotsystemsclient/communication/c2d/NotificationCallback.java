package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.c2d;

import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;

import ro.usv.datacollectionandanalysisoniotsystemsclient.ui.tab.NotificationsFragment;

public class NotificationCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback {

    private final NotificationsFragment notificationsFragment;

    public NotificationCallback(NotificationsFragment notificationsFragment) {
        this.notificationsFragment = notificationsFragment;
    }

    public IotHubMessageResult execute(Message msg, Object context) {

        notificationsFragment.addNotification(
                new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET)
        );

        return IotHubMessageResult.COMPLETE;
    }
}