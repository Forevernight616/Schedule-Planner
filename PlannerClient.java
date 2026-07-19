/**
 * @author Khanh Nguyen
 * @author Duc Tan Tran
 * @author Paloma Ortiz
 * @author Braa Oudeh
 * 
 * This class purpose is to handle the client side of the Planner. 
 * It connects to the Planner Server, sends tasks to the server and receives tasks from the server. 
 * It also has a reference to the PlannerController to update the UI when tasks are received from the server.
 */

import java.io.*;
import java.net.*;
import java.util.*;


public class PlannerClient {
    private PlannerController controller;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 4000;

    /**
     * Constructor for PlannerClient
     * @param controller the PlannerController instance
     */

    public PlannerClient(PlannerController controller) {
        this.controller = controller;
    }
    /**
     * connectToServer() -- connect to the Planner Server and start a new thread to receive tasks from the server
     */
    public void connectToServer()
    {   
        System.out.println("Trying to connect....");
        try 
        {
            socket = new Socket(HOSTNAME, PORT);

            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();

            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to Planner Server at " + HOSTNAME + ":" + PORT);

            new Thread( () -> receiveTasks()).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sendTaskToPlanner() -- send a task to the Planner Server
     * @param task the task to send
     */
    public void sendTaskToPlanner(String task)
    {
        try
        {
            if (output != null) {
                output.writeObject(task);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * sendUserNameToServer() -- send the username to the Planner Server
     * @param userName the username to send
     */
    public void sendUserNameToServer(String userName)
    {
        try
        {
            if (output != null) {
                output.writeObject(userName);
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * receiveTasks() -- receive tasks from the Planner Server
     */
    public void receiveTasks()
    {
        try {
            while (true) {
                String task = (String) input.readObject();
                System.out.println("Received task from server: " + task);
                controller.applyTasksUpdate(task);

            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Disconnected from Planner Server");
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
                if (output != null) {
                    output.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}