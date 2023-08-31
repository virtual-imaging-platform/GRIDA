package fr.insalyon.creatis.grida.common;

import java.io.IOException;
import java.net.Socket;

public class StringCommunication implements Communication {

    public StringCommunication() {
    }

    @Override
    public void sendMessage(String message) {

    }

    @Override
    public void sendErrorMessage(String message) {

    }

    @Override
    public void sendSuccessMessage() {

    }

    @Override
    public void sendEndOfMessage() {

    }

    @Override
    public String getMessage() throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
