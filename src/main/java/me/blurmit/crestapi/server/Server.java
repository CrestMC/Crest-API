package me.blurmit.crestapi.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends Thread {

    private ServerSocket serverSocket;
    private final int port;
    private boolean serverRunning = false;

    private final AtomicInteger connectionIDs;
    private final ExecutorService executors;

    public Server(int port) {
        this.port = port;

        this.connectionIDs = new AtomicInteger(0);
        this.executors = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        start();
    }

    public void start() {
        executors.submit(this);

        System.out.println("Listening on port " + port + "...");
    }

    @Override
    public void run() {
        serverRunning = true;

        while (serverRunning) {
            try {
                Socket socket = serverSocket.accept();
                initializeSocket(socket);
            } catch (IOException ignored) {}
        }

        shutdown();
    }

    private void initializeSocket(Socket socket) {
        Connection connection = new Connection(socket, connectionIDs.getAndIncrement());
        executors.submit(new Thread(connection));
    }

    public void shutdown() {
        serverRunning = false;

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        interrupt();
    }

}
