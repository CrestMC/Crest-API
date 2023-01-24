package me.blurmit.crestapi.server;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.action.AbstractAction;
import me.blurmit.crestapi.action.ActionManager;
import me.blurmit.crestapi.connection.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Connection extends Thread {

    private final Socket socket;
    private final int id;
    private List<String> requestData;

    private InputStreamReader inputStreamReader;
    private BufferedReader reader;

    private OutputStream outputStream;


    public Connection(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
        this.requestData = new ArrayList<>();

        try {
            outputStream = socket.getOutputStream();
            inputStreamReader = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            reader = new BufferedReader(inputStreamReader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CrestAPIServer.getInstance().getActiveConnections().add(this);
    }

    @Override
    public void run() {
        try {
            System.out.println("Started connection #" + id + ".");
            StringBuilder inputBuilder = new StringBuilder();

            while (reader.ready()) {
                inputBuilder.append(new String(new byte[]{(byte) reader.read()}, StandardCharsets.UTF_8));
            }

            requestData = Arrays.asList(inputBuilder.toString().split("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActionManager actionManager = CrestAPIServer.getInstance().getActionManager();
        actionManager.checkContainsKey(this);
        actionManager.checkValidKey(this, actionManager.getAPIKey(this));

        // Pass connection onto the action handler for processing
        String name = Headers.getArgument("action", requestData);
        AbstractAction action = actionManager.getByName(name);

        try {
            action.handleConnection(this);
        } catch (Exception e) {
            System.err.println("An error occurred whilst attempting to handle connection #" + getID());
            e.printStackTrace();

            actionManager.sendBadRequestResponse(this, "Something went wrong while handling this request (" + e.getClass().getName() + ")");
        }
    }

    public void close() {
        try {
            System.out.println("Closing connection #" + id + "...");

            socket.close();
            inputStreamReader.close();
            outputStream.close();
            reader.close();

            interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendString(String... string) {
        try {
            for (String s : string) {
                outputStream.write(s.getBytes());
                outputStream.flush();
            }
        } catch (IOException ignored) {
        } finally {
            CrestAPIServer.getInstance().closeConnectionWithID(getID());
        }
    }

    public int getID() {
        return id;
    }

    public List<String> getRequestData() {
        return requestData;
    }

}
