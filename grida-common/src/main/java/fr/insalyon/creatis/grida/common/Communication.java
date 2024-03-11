package fr.insalyon.creatis.grida.common;

import java.io.IOException;

public abstract class Communication {
    abstract public void sendMessage(String message);

    public void sendErrorMessage(String message) {
        sendMessage(Constants.MSG_ERROR + message);
    }

    public void sendSuccessMessage() {
        sendMessage(Constants.MSG_SUCCESS);
    }

    public void sendEndOfMessage() {
        sendMessage(Constants.MSG_END);
    }

    abstract public String getMessage() throws IOException;

    public void close() throws IOException {
    }
}
