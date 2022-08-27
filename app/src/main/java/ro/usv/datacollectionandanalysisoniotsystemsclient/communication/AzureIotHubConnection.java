package ro.usv.datacollectionandanalysisoniotsystemsclient.communication;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import android.content.Context;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;

import ro.usv.datacollectionandanalysisoniotsystemsclient.BuildConfig;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.c2d.NotificationCallback;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.d2c.MessageReceivedCallback;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.twin.ClientDeviceMethodCallback;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.twin.DeviceMethodStatusCallback;

// source: https://github.com/Azure-Samples/azure-iot-samples-java/blob/master/iot-hub/Samples/device/AndroidSample/app/src/main/java/com/microsoft/azure/iot/sdk/samples/androidsample/MainActivity.java
public class AzureIotHubConnection {

    private static AzureIotHubConnection instance;

    private final Context appContext;

    private DeviceClient client;
    private Thread sendThread;

    private int msgSentCount = 0;

    private AzureIotHubConnection(Context appContext) {

        this.appContext = appContext;
    }

    public static AzureIotHubConnection getInstance() {
        if (nonNull(instance)) {
            return instance;
        }

        throw new IllegalArgumentException("Cannot get not initialized instance");
    }

    public static void init(Context appContext) {

        if (isNull(instance)) {
            instance = new AzureIotHubConnection(appContext);
        }
    }

    public void send(String json) {
        if (!StringUtils.isBlank(json)) {
            sendThread = new Thread(() -> {
                try {
                    initClient();
                    sendMessages(json);
                } catch (Exception e) {
                    System.out.println("Exception while opening IoTHub connection: " + e);
                }
            });
            sendThread.start();
        }
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

    private void initClient() throws URISyntaxException, IOException {
        String connString = BuildConfig.DeviceConnectionString;
        System.out.println(connString);
        client = new DeviceClient(connString, IotHubClientProtocol.HTTPS);

        try {
            client.open();
            client.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());
            client.subscribeToDeviceMethod(new ClientDeviceMethodCallback(), appContext, new DeviceMethodStatusCallback(), null);
        } catch (Exception e) {
            System.err.println("Exception while opening IoTHub connection: " + e);
            client.closeNow();
            System.out.println("Shutting down...");
        }
    }

    public void addNotificationCallback(NotificationCallback notificationCallback) {
        try {
            if (isNull(client)) {
                initClient();
            }
            client.setMessageCallback(notificationCallback, null);
        } catch (Exception e) {
            try {
                client.closeNow();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.err.println("Exception while adding notification callback: " + e);
        }
    }

    private void sendMessages(String json) {
        try {
            Message sendMessage = new Message(json);
            sendMessage.setMessageId(java.util.UUID.randomUUID().toString());
            sendMessage.setContentType("application/json");
            sendMessage.setContentEncoding("utf-8");
            System.out.println("Message Sent: " + json);
            MessageReceivedCallback messageReceivedCallback = new MessageReceivedCallback();
            client.sendEventAsync(sendMessage, messageReceivedCallback, msgSentCount);
            msgSentCount++;
        } catch (Exception e) {
            System.err.println("Exception while sending event: " + e);
        }
    }
}

