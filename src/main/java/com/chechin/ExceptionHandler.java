package com.chechin;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;

/**
 * Created by chechin on 28.08.2016.
 */
public class ExceptionHandler {
    public static void handleException(Throwable t, GameWrapper gameWrapper) {
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

    public static void handleException(Throwable t, Session session) {
        t.printStackTrace();
        String message = t.toString();
        try {
            session.close(new CloseReason(
                    CloseReason.CloseCodes.UNEXPECTED_CONDITION, message
            ));
        } catch (IOException ignore) {
        }
    }
}
