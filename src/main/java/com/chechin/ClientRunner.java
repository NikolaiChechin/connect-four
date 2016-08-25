package com.chechin;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;

/**
 * Created by Chechin on 25.08.2016.
 */
public class ClientRunner {
    public static void main(String[] args) {

        try {

            String dest = "ws://localhost:8080/connect-four/1/John/start";
            ConnectFourClientSocket socket = new ConnectFourClientSocket();
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(socket, new URI(dest));

            socket.getLatch().await();
            Thread.sleep(10000l);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
