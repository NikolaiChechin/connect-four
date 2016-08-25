package com.chechin.messages;

import com.chechin.GameWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by chechin on 23.08.2016.
 */
public class MessageSender {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void sendJsonMessage(Session session, GameWrapper gameWrapper, String message) {
        try {
            session.getBasicRemote()
                    .sendText(message);
        } catch (IOException e) {
            handleException(e, gameWrapper);
        }
    }

    private static void handleException(Throwable t, GameWrapper gameWrapper) {
        t.printStackTrace();
        String message = t.toString();
        try {
            gameWrapper.getPlayer1().close(new CloseReason(
                    CloseReason.CloseCodes.UNEXPECTED_CONDITION, message
            ));
        } catch (IOException ignore) {
        }
        try {
            gameWrapper.getPlayer2().close(new CloseReason(
                    CloseReason.CloseCodes.UNEXPECTED_CONDITION, message
            ));
        } catch (IOException ignore) {
        }
    }
}
