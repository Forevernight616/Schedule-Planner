/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * 
 * This class purpose is to handle the client side of the Planner. 
 * It connects to the Planner Server, sends tasks to the server and receives tasks from the server
 */
import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;

    /**
     * Constructor for ClientHandler
     * @param socket the client socket
     */
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            input = new ObjectInputStream(clientSocket.getInputStream());
            output = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * run() -- run the client handler and listen for incoming tasks from the client, 
     * then broadcast the tasks to other clients
     */
    @Override
    public void run() {
        String task;
        try {
            username = (String) input.readObject();
            System.out.println("New Client Connected: " + username);
            while ((task = (String) input.readObject()) != null) {
                String[] parts = task.split("\\|");
                String operation = parts[0];
                
                if (operation.equals("ADD")) {
                    System.out.println(username + " Added task: " + parts[2] + " on date " + parts[1]);
                } else if (operation.equals("EDIT")) {
                    System.out.println(username + " Edited task: " + parts[2] + " to " + parts[3]);
                } else if (operation.equals("DELETE")) {
                    System.out.println(username + " Deleted task: " + parts[2] + " from date " + parts[1]);
                }
                
                PlannerServer.broadCastTasks(task, this);
             }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected: " + clientSocket.getInetAddress());
        } finally {
            PlannerServer.deleteClient(this);

            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * sendTask() -- send a task to the client
     * @param task the task to send
     */
    public void sendTask(String task) {
        if (output != null) {
            try {
                output.writeObject(task);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * getUsername() -- get the username of the client
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
