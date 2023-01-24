package me.blurmit.crestapi;

import me.blurmit.crestapi.database.DatabaseManager;
import me.blurmit.crestapi.server.Connection;
import me.blurmit.crestapi.server.Server;

import java.util.*;
import java.util.stream.Collectors;

public class CrestAPIServer {

    private static CrestAPIServer INSTANCE;

    private final Server server;
    private final DatabaseManager databaseManager;

    private final Set<Connection> activeConnections;

    public static void main(String[] args) {
        new CrestAPIServer(443);
    }

    public CrestAPIServer(int port) {
        INSTANCE = this;

        databaseManager = new DatabaseManager();
        server = new Server(port);

        activeConnections = new HashSet<>();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Shutting down server-side of socket...");
                server.shutdown();

                System.out.println("Shutting down all client-side sockets...");
                closeAllConnections();

                databaseManager.getDatabase().shutdown();

                System.out.println("All sockets have shutdown. The server will now shutdown. Thank you and goodbye.");
                interrupt();
            }
        });
    }

    public static CrestAPIServer getInstance() {
        return INSTANCE;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public Set<Connection> getActiveConnections() {
        return activeConnections;
    }

    private void closeAllConnections() {
        new HashSet<>(getActiveConnections()).forEach(connection -> closeConnectionWithID(connection.getID()));
    }

    public Connection getConnectionWithID(int id) {
        return getActiveConnections().stream().filter(connection -> connection.getID() == id).findAny().orElse(null);
    }

    public void closeConnectionWithID(int id) {
        activeConnections.stream().filter(connection -> connection.getID() == id).collect(Collectors.toSet()).forEach(connection -> {
            activeConnections.remove(getConnectionWithID(connection.getID()));
            connection.close();
        });
    }

}
