/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * This is the Planner Server. It listens for incoming client connections and handles the communication between clients. It maintains a list of connected clients a
 * and broadcasts tasks to clients.
 */
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;

public class PlannerServer {
    private static final int PORT = 4000;
    private static ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    /**
     * main() -- start the Planner Server
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            System.out.println("Planner Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("New client connected with the IP ADRESS : " + clientSocket.getInetAddress());
                ClientHandler client = new ClientHandler(clientSocket);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * broadCastTasks() -- broadcast tasks to all connected clients
     * @param task - the task to broadcast
     * @param sender - the client that sent the task
     */
    public static void broadCastTasks(String task, ClientHandler sender) {
        for (ClientHandler client : clients) {
            boolean isSameUserName = client.getUsername().equals(sender.getUsername());
            if (client != sender && isSameUserName) {
                client.sendTask(task);
            } 
        }
    }

    /**
     * deleteClient() -- remove a client from the list of connected clients
     * @param client - the client to remove
     */
    public static void deleteClient(ClientHandler client) {
        clients.remove(client);
    }
}
