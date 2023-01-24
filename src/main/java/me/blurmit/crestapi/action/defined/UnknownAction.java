package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.server.Connection;


public class UnknownAction extends AbstractAction {

    public UnknownAction() {
        super("null");
    }

    @Override
    public String getName() {
        return "UNKNOWN";
    }

    @Override
    public void handleConnection(Connection connection) throws Exception {
        actionManager.sendBadRequestResponse(connection, "You did not specify a valid API action to perform.");
    }

}
