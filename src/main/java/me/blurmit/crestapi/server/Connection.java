package me.blurmit.crestapi.server;

import me.blurmit.crestapi.CrestAPIServer;
import me.blurmit.crestapi.api.Action;
import me.blurmit.crestapi.connection.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

            Action.checkContainsKey(this);
            Action.checkValidKey(this, Action.getAPIKey(this));

            // Pass connection onto the action handler for processing
            String action = Headers.getArgument("action", requestData);
            Action.getByName(action).handleConnection(this);
        } catch (IOException e) {
            e.printStackTrace();
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
