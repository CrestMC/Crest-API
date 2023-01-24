package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.connection.Headers;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public class UnsetAdminKeyAction extends AbstractAction {

    public UnsetAdminKeyAction() {
        super("unset_api_admin", "unset_admin_key", "delete_admin_key", "delete_api_admin_key", "remove_admin_key", "remove_api_admin_key");
    }

    @Override
    public String getName() {
        return "UnsetAdminKey";
    }

    @Override
    public void handleConnection (Connection connection) throws Exception {
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

        if (!CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyAdmin(key)) {
            actionManager.sendBadRequestResponse(connection, uuid + " does not have an API admin key.");
            return;
        }

        CrestAPIServer.getInstance().getDatabaseManager().setAdminKey(actionManager.getAPIKey(connection), uuid, false);

        Map<String, Object> data = new HashMap<>();
        data.put("message", uuid + " no longer has an API admin key.");

        connection.sendString(Response.OK.build(data));
    }

}
