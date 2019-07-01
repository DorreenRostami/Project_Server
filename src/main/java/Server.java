import model.ServerMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server implements Runnable {

    private static final int requestPort = 8080;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        // Creating a Stream of Integers
        Stream<Integer> stream = Stream.of(-2, -1, 1, -2, 0, 1, 2);

        // Using Stream.collect() to collect the
        // elements of stream in a container.
        Set<Integer> streamSet = stream.collect(Collectors.toSet());

        // Displaying the elements
        streamSet.forEach(System.out::println);
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
            if (clientRequest instanceof ServerMessage) {
                serverHandler.handleSending((ServerMessage) clientRequest);
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
