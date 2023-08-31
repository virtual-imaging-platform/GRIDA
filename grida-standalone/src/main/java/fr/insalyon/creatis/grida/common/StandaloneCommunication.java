package fr.insalyon.creatis.grida.common;

import fr.insalyon.creatis.grida.server.execution.Executor;

import java.io.IOException;

public class StandaloneCommunication extends Communication {

    private StringBuilder messageBuilder;
    
    public StandaloneCommunication() {
        this.messageBuilder = new StringBuilder();
    }

    @Override
    public void sendMessage(String message) {
        this.messageBuilder.append(message);
    }


    @Override
    public void sendEndOfMessage() {
        super.sendEndOfMessage();
        new Executor(this).run();
    }

    @Override
    public String getMessage() throws IOException {
        // reset message for next exchange
        String message = messageBuilder.toString();
        messageBuilder = new StringBuilder();
        return message;
    }
}
