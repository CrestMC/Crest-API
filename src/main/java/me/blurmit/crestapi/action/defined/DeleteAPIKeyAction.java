package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.connection.Headers;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public class DeleteAPIKeyAction extends AbstractAction {

    public DeleteAPIKeyAction() {
        super("delete_key", "remove_key");
    }

    @Override
    public String getName() {
        return "DELETE_API_KEY";
    }

    @Override
    public void handleConnection(Connection connection) throws Exception {
        // Check the API key of the user performing this request
        actionManager.checkValidKey(connection, actionManager.getAPIKey(connection));
        actionManager.checkAPIAdmin(connection, actionManager.getAPIKey(connection));

        // Check if the API key specified in the request is valid
        String uuid = Headers.getArgument("uuid", connection.getRequestData()).trim();
        String key = CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(uuid);

        if (key.equals("null")) {
            actionManager.sendBadRequestResponse(connection, uuid + " does not have an API key.");
            return;
        }

        CrestAPIServer.getInstance().getDatabaseManager().deleteAPIKey(actionManager.getAPIKey(connection), key);

        Map<String, Object> data = new HashMap<>();
        data.put("message", uuid + " no longer has an API key.");

        connection.sendString(Response.OK.build(data));
    }

}
