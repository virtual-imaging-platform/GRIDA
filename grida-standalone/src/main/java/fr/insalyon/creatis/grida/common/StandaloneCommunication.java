package fr.insalyon.creatis.grida.common;

import fr.insalyon.creatis.grida.server.execution.Executor;

import java.io.IOException;

public class StandaloneCommunication extends Communication {

    private StringBuilder messageBuilder;

    private boolean hasExecuted = false;
    
    public StandaloneCommunication() {
        this.messageBuilder = new StringBuilder();
    }

    @Override
    public void sendMessage(String message) {
        if ( ! this.messageBuilder.toString().isEmpty()) {
            messageBuilder.append(Constants.MSG_SEP_1);
        }
        this.messageBuilder.append(message);
    }


    @Override
    public void sendEndOfMessage() {
        if ( ! hasExecuted) {
            hasExecuted = true;
            new Executor(this).run();
        }
    }

    @Override
    public String getMessage() throws IOException {
        // reset message for next exchange
        String message = messageBuilder.toString();
        messageBuilder = new StringBuilder();
        return message;
    }
}
