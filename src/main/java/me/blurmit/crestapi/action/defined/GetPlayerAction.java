package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public class GetPlayerAction extends AbstractAction {

    public GetPlayerAction() {
        super("player_info");
    }

    @Override
    public String getName() {
        return "GET_PLAYER";
    }

    @Override
    public void handleConnection(Connection connection) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> info = new HashMap<>();

        actionManager.checkAPIAdmin(connection, actionManager.getAPIKey(connection));
        info.put("username", "Blurmit");
        info.put("uuid", "null");
        info.put("rank", "Developer");
        info.put("last_login", System.currentTimeMillis());
        info.put("joined", System.currentTimeMillis());

        connection.sendString(Response.OK.build(data, info));
    }

}
