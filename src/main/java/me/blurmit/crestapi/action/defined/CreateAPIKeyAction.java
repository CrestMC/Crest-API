package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.connection.Headers;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public class CreateAPIKeyAction extends AbstractAction {

    public CreateAPIKeyAction() {
        super("create_key");
    }

    @Override
    public String getName() {
        return "CREATE_API_KEY";
    }

    @Override
    public void handleConnection(Connection connection) throws Exception {
        // Check the API key of the user performing this request
        actionManager.checkValidKey(connection, actionManager.getAPIKey(connection));
        actionManager.checkAPIAdmin(connection, actionManager.getAPIKey(connection));

        String user = Headers.getArgument("uuid", connection.getRequestData()).trim();
        String key = CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(user);

        if (!key.equals("null")) {
            actionManager.sendBadRequestResponse(connection, user + " already has an API key.");
            return;
        }

        CrestAPIServer.getInstance().getDatabaseManager().createAPIKey(actionManager.getAPIKey(connection), user);

        Map<String, Object> data = new HashMap<>();
        data.put("message", "You have successfully created " + user + " an API key.");

        Map<String, Object> info = new HashMap<>();
        info.put("key", CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(user));

        connection.sendString(Response.OK.build(data, info));
    }

}
