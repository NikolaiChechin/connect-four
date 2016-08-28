package com.chechin.messages;

import com.chechin.GameWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;

import static com.chechin.ExceptionHandler.handleException;

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

    public static void sendJsonMessage(Session session, GameWrapper gameWrapper, Object message) {
        try {
            session.getBasicRemote()
                    .sendText(mapper.writeValueAsString(message));
        } catch (IOException e) {
            handleException(e, gameWrapper);
        }
    }
    public static void sendJsonMessage(Session session, Object message) {
        try {
            session.getBasicRemote()
                    .sendText(mapper.writeValueAsString(message));
        } catch (IOException e) {
            handleException(e, session);
        }
    }
}
