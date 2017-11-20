import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h1>LinkStateRoutingProcessor</h1>
 *
 * <h2>LS Routing main application runner. It acts as a simulator for a single LS link in a network.</h2>
 *
 * <p>It does the Following:</p>
 * <ol>
 *     <li>Runs the server that will be acting as the communicator about current LS link with other links that request
 *     the graph topology.</li>
 *     <li>Acts as a client that requests the network topology from all of its intermediate connections</li>
 *     <li>Prints the shortest path from its LS link to all other connected LS links in the network
 *     that have been known</li>
 * </ol>
 *
 * Created by Zack Zaiter (Abdulrahman) & Ryan Castiglione on 11/18/2017.
 *
 * @author Zack Zaiter (Abdulrahman) & Ryan Castiglione.
 * @since 11/18/2017
 */
public class LinkStateRoutingProcessor {
    // a Constant that holds the server ip of the communicator.
    private static final String SERVER_IP = "127.0.0.1";

    /**
     * a main function that runs the interactive window function
     *
     * @param args program caller arguments
     * @throws Exception when the LSNode fails to initiate from a settings file
     */
    public static void main(String[] args) throws Exception {
        showInteractiveMenu(args);
    }

    /**
     * A method that shows the main interactive window of the program.
     *
     * @param args program caller arguments
     * @throws Exception when the LSNode fails to initiate from a settings file
     */
    public static void showInteractiveMenu(String[] args) throws Exception {
        // initialize the variables
        Scanner sc = new Scanner(System.in);
        int choice;
        LSNode lsNode = new LSNode(args[0]);
        boolean serverRunning = false;
        boolean clientRunning = false;

        // do a loop that will ask the user of their choice.
        do {
            printMenu(serverRunning, clientRunning);
            try {
                choice = sc.nextInt();
                switch (choice) {
                    // 1- run the server
                    case 1:
                        if (!serverRunning) {
                            serverRunning = true;
                            runServer(lsNode);
                        } else System.out.println("Server is running! you can't run it twice.");
                        break;
                    // 2- run the clients communicator
                    case 2:
                        clientRunning = true;
                        runClient(lsNode);
                        break;
                    // 3- print current LS shortest path from this node to all others
                    case 3:
                        System.out.println(lsNode);
                        break;
                    // 4- exit the program
                    case 4:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid selection");
                        break; // This break is not really necessary
                }
            } catch (Exception e){
                System.out.println("Invalid selection, input a valid choice");
                sc.next();
            }
        } while(true);
    }

    /**
     * A method to run the server that will be acting as the communicator about current LS link with other links that
     * request the graph topology.
     *
     * @param lsNode a valid LSNode instance.
     */
    public static void runServer(LSNode lsNode){
        // run the server as a thread that spawns threads for each LS packet connection ( Concurrency Control )
        new Thread(()->{
            try {
                //initiate server socket
                ServerSocket srvSocket = new ServerSocket(lsNode.serverPort);
                try {
                    while (true) {
                        // accept client connection and serve it in a new thread.
                        Socket socket = srvSocket.accept();
                        // initialize input and output streams.
                        new Thread(() -> {
                            try {
                                // initiate a reentrant lock when accessing the LSNode instance and updating the topography.
                                ReentrantLock lock = new ReentrantLock();
                                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                                Object objectToRead = inputStream.readObject();

                                //close the socket as soon as it gets the needed graph topology.
                                socket.close();

                                // lock the thread, update the local topology and unlock the reentrant lock
                                lock.lock();
                                SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> clientLSPacketGraph =
                                        (SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>) objectToRead;
                                lsNode.addEdgesFromAnotherLSPacket(clientLSPacketGraph);
                                lock.unlock();
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } catch (Exception e){
                //e.printStackTrace();
                }
        }).start();
    }

    /**
     * A client runner that requests the network topology from all of its intermediate connections
     *
     * @param lsNode a valid LSNode instance.
     */
    public static void runClient(LSNode lsNode){
        // run a thread for each client request to all connected adjacent LS links
        new Thread(()->{
            while(true){
                for (Integer portNum : lsNode.adjacentLSPacketsPorts) {
                    new Thread(() -> {
                        try {
                            // initiate a lock, server the LS topography and close the connections.
                            ReentrantLock lock = new ReentrantLock();
                            Socket socket = new Socket(SERVER_IP, portNum);
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            lock.lock();
                            outputStream.writeObject(lsNode.lsPacketGraph);
                            lock.unlock();
                            socket.close();
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }).start();
                    try {
                        Thread.sleep(5000); // sleep for 5 seconds (pulse sleep) to lessen the load on the network
                    }catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * a method to print the main menu.
     *
     * @param serverRunning a boolean that indicates if the server is running or not
     * @param clientRunning a boolean that indicates if the client communicator is running or not
     */
    public static void printMenu(boolean serverRunning, boolean clientRunning){
        System.out.println(
                        "1 - Run LSNode Server. Currently: "+serverRunning+"\n" +
                        "2 - Once you have all Packets up, run LSNode communicator. Currently: "+clientRunning+"\n" +
                        "3 - Display current Routes from current LSNode.\n"+
                        "4 - Exit program"
        );
    }

}
