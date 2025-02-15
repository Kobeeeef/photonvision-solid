package org.photonvision.common.dataflow.networktables;

import org.kobe.xbot.JClient.XTablesClient;
import org.kobe.xbot.JClient.XTablesClientManager;
import org.photonvision.common.dataflow.DataChangeService;
import org.photonvision.common.dataflow.events.OutgoingUIEvent;
import org.photonvision.common.logging.LogGroup;
import org.photonvision.common.logging.Logger;
import org.photonvision.common.util.TimedTaskManager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class XTablesManager {
    private static final Logger logger = new Logger(XTablesManager.class, LogGroup.NetworkTables);

    private final XTablesClientManager xtClient;
    private static XTablesManager INSTANCE;
    public static final String ROOT_NAME = "photonvision.";

    private XTablesManager() {
            this.xtClient = XTablesClient.getDefaultClientAsynchronously();
            this.xtClient.getClientFuture().thenAccept(client -> client.addVersionProperty("PHOTONVISION"));
    }

    public boolean isReady() {
        return xtClient.isClientReady();
    }

    public XTablesClient getXtClient() {
        return xtClient.getOrNull();
    }

    public static XTablesManager getInstance() {
        if (INSTANCE == null) INSTANCE = new XTablesManager();
        return INSTANCE;
    }



    public boolean isConnected() {
        return xtClient.isClientReady()
                && xtClient.getOrNull().getSocketMonitor().isConnected("REQUEST")
                && xtClient.getOrNull().getSocketMonitor().isConnected("PUSH")
                && xtClient.getOrNull().getSocketMonitor().isConnected("SUBSCRIBE");
    }



    public void close() {

        if(xtClient != null) {
            if(xtClient.isClientReady()) {
                xtClient.getClientFuture().thenAccept(XTablesClient::shutdown);
            }
        }
    }
}
