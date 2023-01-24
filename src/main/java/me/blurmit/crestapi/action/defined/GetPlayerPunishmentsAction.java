package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public class GetPlayerPunishmentsAction extends AbstractAction {

    public GetPlayerPunishmentsAction() {
        super("punishments_of_player");
    }

    @Override
    public String getName() {
        return "GET_PLAYER_PUNISHMENTS";
    }

    @Override
    public void handleConnection(Connection connection) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Not implemented yet.");

        connection.sendString(Response.OK.build(data));
    }

}
