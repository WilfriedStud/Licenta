package ro.usv.datacollectionandanalysisoniotsystemsclient.communication;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import android.content.Context;

import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.Message;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

import ro.usv.datacollectionandanalysisoniotsystemsclient.BuildConfig;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.c2d.NotificationCallback;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.d2c.MessageReceivedCallback;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.twin.ClientDeviceMethodCallback;
import ro.usv.datacollectionandanalysisoniotsystemsclient.communication.twin.DeviceMethodStatusCallback;

// source: https://github.com/Azure-Samples/azure-iot-samples-java/blob/master/iot-hub/Samples/device/AndroidSample/app/src/main/java/com/microsoft/azure/iot/sdk/samples/androidsample/MainActivity.java
public class AzureIotHubConnection {

    private static final Logger LOG = LoggerFactory.getLogger(AzureIotHubConnection.class);

    private static AzureIotHubConnection instance;

    private final Context appContext;

    private DeviceClient client;
    private Thread sendThread;
    private int msgSentCount = 0;

    private AzureIotHubConnection(Context appContext) {
        this.appContext = appContext;
    }

    public static void init(Context appContext) {

        if (isNull(instance)) {
            instance = new AzureIotHubConnection(appContext);
        }
    }

    public static AzureIotHubConnection getInstance() {

        if (nonNull(instance)) {
            return instance;
        }

        throw new IllegalArgumentException("Cannot get not initialized instance");
    }

    public void send(String json) {

        LOG.info("Invoked send with json {}", json);

        if (!StringUtils.isBlank(json)) {
            sendThread = new Thread(() -> {
                try {
                    initClient();
                    tryOpenConnection();
                    client.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallbackLogger(), new Object());
                    client.subscribeToDeviceMethod(new ClientDeviceMethodCallback(), appContext, new DeviceMethodStatusCallback(), null);
                    sendMessages(json);
                } catch (Exception e) {
                    LOG.info("Exception in method send", e);
                }
            });
            sendThread.start();
        }
    }

    public void stop() {

        LOG.info("Invoked stop");

        new Thread(() -> {
            try {
                sendThread.interrupt();
                tryCloseConnection();
            } catch (Exception e) {
                LOG.info("Exception in method stop", e);
            }
        }).start();
    }

    private void initClient() throws URISyntaxException {

        LOG.info("Invoked initClient");

        if (isNull(client)) {
            String connString = BuildConfig.DeviceConnectionString;
            client = new DeviceClient(connString, IotHubClientProtocol.AMQPS);
        }
    }

    private void tryOpenConnection() {
        try {
            LOG.info("Opening connection");
            client.open();
        } catch (Exception e) {
            LOG.info("Exception in method initClient", e);
            tryCloseConnection();
        }
    }

    private void tryCloseConnection() {
        try {
            LOG.info("Close connection");
            client.closeNow();
        } catch (Exception e) {
            LOG.info("Exception in method initClient", e);
        }
    }

    public void addNotificationCallback(NotificationCallback notificationCallback) {

        LOG.info("Invoked addNotificationCallback");

        try {
            initClient();
            client.setMessageCallback(notificationCallback, null);
            tryOpenConnection();

        } catch (Exception e) {
            LOG.info("Exception in method addNotificationCallback", e);
            try {
                LOG.info("Shutting down connection...");
                client.closeNow();
            } catch (IOException ex) {
                LOG.info("Exception in method addNotificationCallback, shutting down", e);
            }
        }
    }

    private void sendMessages(String json) {

        LOG.info("Invoked sendMessages");

        try {
            Message sendMessage = new Message(json);
            sendMessage.setMessageId(java.util.UUID.randomUUID().toString());
            sendMessage.setContentType("application/json");
            sendMessage.setContentEncoding("utf-8");
            MessageReceivedCallback messageReceivedCallback = new MessageReceivedCallback();
            client.sendEventAsync(sendMessage, messageReceivedCallback, msgSentCount);
            LOG.info("Message Sent: {}", json);
            msgSentCount++;
        } catch (Exception e) {
            LOG.info("Exception in method sendMessages", e);
        }
    }
}

