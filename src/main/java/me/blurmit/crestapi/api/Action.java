package me.blurmit.crestapi.api;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.connection.Headers;
import me.blurmit.crestapi.connection.Responses;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public enum Action {

    GET_PLAYER("player_info") {
        @Override
        public void handleConnection(Connection connection) {
            Map<String, Object> data = new HashMap<>();
            Map<String, Object> info = new HashMap<>();

            Action.checkAPIAdmin(connection, Action.getAPIKey(connection));
            info.put("username", "Blurmit");
            info.put("uuid", "null");
            info.put("rank", "Developer");
            info.put("last_login", System.currentTimeMillis());
            info.put("joined", System.currentTimeMillis());

            connection.sendString(Responses.OK.build(data, info));
        }
    },
    GET_PLAYER_PUNISHMENTS("punishments_of_player") {
        @Override
        public void handleConnection(Connection connection) {
            Map<String, Object> data = new HashMap<>();
        }
    },
    GET_RANK("rank_info") {
        @Override
        public void handleConnection(Connection connection) {
            Map<String, Object> data = new HashMap<>();
        }
    },

    CREATE_API_KEY("create_key") {
        @Override
        public void handleConnection(Connection connection) {
            // Check the API key of the user performing this request
            Action.checkValidKey(connection, Action.getAPIKey(connection));
            Action.checkAPIAdmin(connection, Action.getAPIKey(connection));

            String user = Headers.getArgument("uuid", connection.getRequestData()).trim();
            String key = CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(user);

            if (!key.equals("null")) {
                sendBadRequestResponse(connection, user + " already has an API key.");
                return;
            }

            CrestAPIServer.getInstance().getDatabaseManager().createAPIKey(Action.getAPIKey(connection), user);

            Map<String, Object> data = new HashMap<>();
            data.put("message", "You have successfully created " + user + " an API key.");

            Map<String, Object> info = new HashMap<>();
            info.put("key", CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(user));

            connection.sendString(Responses.OK.build(data, info));
        }
    },
    DELETE_API_KEY("delete_key", "remove_key") {
        @Override
        public void handleConnection(Connection connection) {
            // Check the API key of the user performing this request
            Action.checkValidKey(connection, Action.getAPIKey(connection));
            Action.checkAPIAdmin(connection, Action.getAPIKey(connection));

            // Check if the API key specified in the request is valid
            String uuid = Headers.getArgument("uuid", connection.getRequestData()).trim();
            String key = CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(uuid);

            if (key.equals("null")) {
                sendBadRequestResponse(connection, uuid + " does not have an API key.");
                return;
            }

            CrestAPIServer.getInstance().getDatabaseManager().deleteAPIKey(Action.getAPIKey(connection), key);

            Map<String, Object> data = new HashMap<>();
            data.put("message", uuid + " no longer has an API key.");

            connection.sendString(Responses.OK.build(data));
        }
    },
    SET_ADMIN_KEY("set_api_admin", "set_admin_key", "create_admin_key", "create_api_admin_key") {
        @Override
        public void handleConnection(Connection connection) {
            // Check the API key of the user performing this request
            Action.checkValidKey(connection, Action.getAPIKey(connection));
            Action.checkAPIAdmin(connection, Action.getAPIKey(connection));

            // Check if the API key specified in the request is valid
            String uuid = Headers.getArgument("uuid", connection.getRequestData()).trim();
            String key = CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(uuid);

            if (key.equals("null")) {
                sendBadRequestResponse(connection, uuid + " does not have an API key.");
                return;
            }

            if (CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyAdmin(key)) {
                sendBadRequestResponse(connection, uuid + " already has an API admin key.");
                return;
            }

            CrestAPIServer.getInstance().getDatabaseManager().setAdminKey(getAPIKey(connection), uuid, true);

            Map<String, Object> data = new HashMap<>();
            data.put("message", uuid + " now has an API admin key.");

            connection.sendString(Responses.OK.build(data));
        }
    },
    UNSET_ADMIN_KEY("unset_api_admin", "unset_admin_key", "delete_admin_key", "delete_api_admin_key", "remove_admin_key", "remove_api_admin_key") {
        @Override
        public void handleConnection(Connection connection) {
            // Check the API key of the user performing this request
            Action.checkValidKey(connection, Action.getAPIKey(connection));
            Action.checkAPIAdmin(connection, Action.getAPIKey(connection));

            // Check if the API key specified in the request is valid
            String uuid = Headers.getArgument("uuid", connection.getRequestData()).trim();
            String key = CrestAPIServer.getInstance().getDatabaseManager().getAPIKey(uuid);

            if (key.equals("null")) {
                sendBadRequestResponse(connection, uuid + " does not have an API key.");
                return;
            }

            if (!CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyAdmin(key)) {
                sendBadRequestResponse(connection, uuid + " does not have an API admin key.");
                return;
            }

            CrestAPIServer.getInstance().getDatabaseManager().setAdminKey(Action.getAPIKey(connection), uuid, false);

            Map<String, Object> data = new HashMap<>();
            data.put("message", uuid + " no longer has an API admin key.");

            connection.sendString(Responses.OK.build(data));
        }
    },
    GET_API_KEY {
        @Override
        public void handleConnection(Connection connection) {

        }
    },

    UNKNOWN("null") {
        @Override
        public void handleConnection(Connection connection) {
            sendBadRequestResponse(connection, "You did not specify a valid API action to perform.");
        }
    };

    private final String[] aliases;

    private static final Map<String, Action> ACTIONS = new HashMap<>();

    Action(String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public static void checkAPIAdmin(Connection connection, String key) {
        if (!CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyAdmin(key)) {
            sendUnauthorizedResponse(connection, "You are not authorized to perform this action.");
        }
    }

    public static void checkValidKey(Connection connection, String key) {
        if (!CrestAPIServer.getInstance().getDatabaseManager().isAPIKeyValid(key)) {
            sendBadRequestResponse(connection, "The API key that you provided is invalid or does not exist.");
        }
    }

    public static void checkContainsKey(Connection connection) {
        if (!containsAPIKey(connection)) {
            sendBadRequestResponse(connection, "You need to provide a X-Crest-API-Key header in your request.");
        }
    }

    public static void sendUnauthorizedResponse(Connection connection, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);

        connection.sendString(Responses.UNAUTHORIZED.build(data));
    }

    public static void sendBadRequestResponse(Connection connection, String message) {
        Map<String, Object> data = new HashMap<>();
        data.put("message", message);

        connection.sendString(Responses.BAD_REQUEST.build(data));
    }

    public static String getAPIKey(Connection connection) {
        return Headers.getValue("X-Crest-API-Key", connection.getRequestData());
    }

    public static boolean containsAPIKey(Connection connection) {
        return Headers.hasHeader("X-Crest-API-Key", connection.getRequestData());
    }

    public static Action getByName(String name) {
        return ACTIONS.get(name.toLowerCase().trim()) == null ? Action.UNKNOWN : ACTIONS.get(name.toLowerCase().trim());
    }

    static {
        for (Action value : values()) {
            ACTIONS.put(value.name().replace("_", "").toLowerCase(), value);
            ACTIONS.put(value.name().replace("-", "").toLowerCase(), value);
            ACTIONS.put(value.name().toLowerCase().trim(), value);

            for (String alias : value.getAliases()) {
                ACTIONS.put(alias.replace("_", "").toLowerCase(), value);
                ACTIONS.put(alias.replace("-", "").toLowerCase(), value);
                ACTIONS.put(alias.toLowerCase(), value);
            }
        }
    }

    public abstract void handleConnection(Connection connection);

}
