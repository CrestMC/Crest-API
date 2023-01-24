package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.connection.Headers;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public class SetAdminKeyAction extends AbstractAction {

    public SetAdminKeyAction() {
        super("set_api_admin", "set_admin_key", "create_admin_key", "create_api_admin_key");
    }

    @Override
    public String getName() {
        return "SET_ADMIN_KEY";
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

        if (CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyAdmin(key)) {
            actionManager.sendBadRequestResponse(connection, uuid + " already has an API admin key.");
            return;
        }

        CrestAPIServer.getInstance().getDatabaseManager().setAdminKey(actionManager.getAPIKey(connection), uuid, true);

        Map<String, Object> data = new HashMap<>();
        data.put("message", uuid + " now has an API admin key.");

        connection.sendString(Response.OK.build(data));
    }

}
