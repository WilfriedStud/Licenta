package ro.usv.datacollectionandanalysisoniotsystemsclient.communication.send;

import android.content.Context;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;

import java.io.IOException;
import java.net.URISyntaxException;

import ro.usv.datacollectionandanalysisoniotsystemsclient.BuildConfig;

public class PacketSender {

    private DeviceClient client;

    IotHubClientProtocol protocol = IotHubClientProtocol.HTTPS;

    private int msgSentCount = 0;
    private int receiptsConfirmedCount = 0;
    private int sendFailuresCount = 0;
    private int msgReceivedCount = 0;

    private Thread sendThread;

    private static final int METHOD_SUCCESS = 200;
    public static final int METHOD_THROWS = 403;
    private static final int METHOD_NOT_DEFINED = 404;

    public void send(Context context, String json) {
        sendThread = new Thread(() -> {
            try {
                initClient(context);
                sendMessages(json);
            } catch (Exception e) {
                System.out.println("Exception while opening IoTHub connection: " + e);
            }
        });
        sendThread.start();
    }

    public void stop() {
        new Thread(() -> {
            try {
                sendThread.interrupt();
                client.closeNow();
                System.out.println("Shutting down...");
            } catch (Exception e) {
                System.out.println("Exception while closing IoTHub connection: " + e);
            }
        }).start();
    }

    private void initClient(Context applicationContext) throws URISyntaxException, IOException {
        String connString = BuildConfig.DeviceConnectionString;
        System.out.println(connString);
        client = new DeviceClient(connString, protocol);

        try {
            client.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());
            client.open();
            MessageCallback callback = new MessageCallback();
            client.setMessageCallback(callback, null);
            client.subscribeToDeviceMethod(new SampleDeviceMethodCallback(), applicationContext, new DeviceMethodStatusCallBack(), null);
        } catch (Exception e) {
            System.err.println("Exception while opening IoTHub connection: " + e);
            client.closeNow();
            System.out.println("Shutting down...");
        }
    }

    private void sendMessages(String json) {
        try {
            Message sendMessage = new Message(json);
            sendMessage.setMessageId(java.util.UUID.randomUUID().toString());
            System.out.println("Message Sent: " + json);
            EventCallback eventCallback = new EventCallback();
            client.sendEventAsync(sendMessage, eventCallback, msgSentCount);
            msgSentCount++;
        } catch (Exception e) {
            System.err.println("Exception while sending event: " + e);
        }
    }

    class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            int i = context instanceof Integer ? (Integer) context : 0;
            System.out.println("IoT Hub responded to message " + i
                    + " with status " + status.name());

            if ((status == IotHubStatusCode.OK) || (status == IotHubStatusCode.OK_EMPTY)) {
                receiptsConfirmedCount++;
                System.out.println("Receipts confirmed count: " + receiptsConfirmedCount);
            } else {
                sendFailuresCount++;
                System.out.println("Send failure count: " + sendFailuresCount);
            }
        }
    }

    class MessageCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback {
        public IotHubMessageResult execute(Message msg, Object context) {
            System.out.println(
                    "Received message with content: " + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET));
            msgReceivedCount++;
            System.out.println("Message received count: " + msgReceivedCount);
            System.out.println("[" + new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET) + "]");
            return IotHubMessageResult.COMPLETE;
        }
    }

    protected static class IotHubConnectionStatusChangeCallbackLogger implements IotHubConnectionStatusChangeCallback {
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

    private int method_setSendMessagesInterval(Object methodData) {
        System.out.println(methodData);
        return METHOD_SUCCESS;
    }

    private int method_default(Object data) {
        System.out.println("invoking default method for this device");
        // Insert device specific code here
        System.out.println(data);
        return METHOD_NOT_DEFINED;
    }

    protected static class DeviceMethodStatusCallBack implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            System.out.println("IoT Hub responded to device method operation with status " + status.name());
        }
    }

    protected class SampleDeviceMethodCallback implements com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodCallback {
        @Override
        public DeviceMethodData call(String methodName, Object methodData, Object context) {
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
                status = METHOD_THROWS;
                deviceMethodData = new DeviceMethodData(status, "Method Throws " + methodName);
            }
            return deviceMethodData;
        }
    }
}

