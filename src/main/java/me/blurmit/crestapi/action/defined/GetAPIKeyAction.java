package me.blurmit.crestapi.action.defined;

import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.connection.Response;
import me.blurmit.crestapi.server.Connection;

import java.util.HashMap;
import java.util.Map;

public class GetAPIKeyAction extends AbstractAction {

    public GetAPIKeyAction() {
        super("get_key");
    }

    @Override
    public String getName() {
        return "GET_API_KEY";
    }

    @Override
    public void handleConnection(Connection connection) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Not implemented yet.");

        connection.sendString(Response.OK.build(data));
    }

}
