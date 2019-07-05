import model.ServerMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server implements Runnable {

    private static final int requestPort = 8080;
    private static ServerSocket serverSocket;

    public static void main(String[] args){
        Server.start();
    }

    private static void start() {
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

    ServerRunner(Socket serverSocket) {
        this.socket = serverSocket;
    }

    @Override
    public void run() {
        try {
            serverHandler = new ServerHandler(socket,
                    new ObjectOutputStream(socket.getOutputStream()),
                    new ObjectInputStream(socket.getInputStream()));

            ServerMessage clientRequest = (ServerMessage) serverHandler.getInputStream().readObject();
            System.out.println(clientRequest.getSender().getUsername() + " connect");
            System.out.println(new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
            serverHandler.handle(clientRequest);
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