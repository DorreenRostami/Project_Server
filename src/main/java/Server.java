package main.java;

import main.java.model.Email;
import main.java.model.ServerMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private static final int requestPort = 8080;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        Server.start();
    }

    public static void start() {
        try {
            serverSocket = new ServerSocket(requestPort);
            Thread serverThread = new Thread(new Server(), "Server Thread");
            serverThread.start();
        }
        catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                new Thread(new ServerRunner(serverSocket.accept()), "Server Runner").start();
            }
            catch (IOException ignored) {
            }
        }
    }
}

class ServerRunner implements Runnable {
    private Socket socket;
    private ServerHandler serverHandler;

    public ServerRunner(Socket serverSocket) {
        this.socket = serverSocket;
    }

    @Override
    public void run() {
        Object clientRequest;
        try {
            serverHandler = new ServerHandler(socket,
                    new ObjectOutputStream(socket.getOutputStream()),
                    new ObjectInputStream(socket.getInputStream()));

            clientRequest = serverHandler.getInputStream().readObject();
            System.out.println("read");
            if (clientRequest instanceof String){
                serverHandler.handleImage(socket, (String) clientRequest);
            }
            else if (clientRequest instanceof ServerMessage) {
                serverHandler.handle((ServerMessage) clientRequest);
            }
            else if (clientRequest instanceof Email){
                serverHandler.handle((Email) clientRequest);
            }
            else if (clientRequest instanceof File) {
                //TO DO
            }
        }
        catch (IOException | ClassNotFoundException ignored) {
        }
        finally {
            userDisconnect();
        }
    }

    private void userDisconnect() {
        try {
            serverHandler.getOutputStream().close();
            serverHandler.getInputStream().close();
            socket.close();
        }
        catch (IOException ignored) {
        }
    }
}
