package me.blurmit.crestapi.action;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.action.defined.UnknownAction;
import me.blurmit.crestapi.connection.Headers;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;
import me.blurmit.crestapi.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;

public class ActionManager {

    private final Map<String, AbstractAction> actions;

    public ActionManager() {
        actions = new HashMap<>();

        System.out.println("Loading API actions....");

        ReflectionUtil.consume("me.blurmit.crestapi.action.defined", ActionManager.class.getClassLoader(), AbstractAction.class, action -> {
            action.setActionManager(this);

            String name = action.getName();
            actions.put(name, action);

            actions.put(name.replace("_", "").toLowerCase(), action);
            actions.put(name.replace("-", "").toLowerCase(), action);
            actions.put(name.toLowerCase().trim(), action);

            for (String alias : action.getAliases()) {
                actions.put(alias.replace("_", "").toLowerCase(), action);
                actions.put(alias.replace("-", "").toLowerCase(), action);
                actions.put(alias.toLowerCase(), action);
            }
        }, true);

        System.out.println("Loaded " + actions.size() + " API actions.");
    }

    public AbstractAction getByName(String name) {
        AbstractAction unknownAction = new UnknownAction();
        unknownAction.setActionManager(this);

        return actions.get(name.toLowerCase().trim()) == null ? unknownAction : actions.get(name.toLowerCase().trim());
    }

    public void checkAPIAdmin(Connection connection, String key) {
        if (!CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyAdmin(key)) {
            sendUnauthorizedResponse(connection, "You are not authorized to perform this action.");
        }
    }

    public void checkValidKey(Connection connection, String key) {
        if (!CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyValid(key)) {
            sendBadRequestResponse(connection, "The API key that you provided is invalid or does not exist.");
        }
    }

    public void checkContainsKey(Connection connection) {
        if (!containsAPIKey(connection)) {
            sendBadRequestResponse(connection, "You need to provide a X-Crest-API-Key header in your request.");
        }
    }

    public void sendUnauthorizedResponse(Connection connection, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);

        connection.sendString(Response.UNAUTHORIZED.build(data));
    }

    public void sendBadRequestResponse(Connection connection, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);

        connection.sendString(Response.BAD_REQUEST.build(data));
    }

    public String getAPIKey(Connection connection) {
        return Headers.getValue("X-Crest-API-Key", connection.getRequestData());
    }

    public boolean containsAPIKey(Connection connection) {
        return Headers.hasHeader("X-Crest-API-Key", connection.getRequestData());
    }

}
