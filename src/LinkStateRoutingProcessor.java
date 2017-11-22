import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import javax.swing.*;
import java.awt.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
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

        runServer(lsNode);
        runClient(lsNode);

        // do a loop that will ask the user of their choice.
        do {
            printMenu();
            try {
                choice = sc.nextInt();
                switch (choice) {
                    // 1- print current LS shortest path from this node to all others
                    case 1:
                        System.out.println(lsNode);
                        showGraphGUI(lsNode.lsPacketGraph, lsNode.packetID);
                        break;
                    // 2- exit the program
                    case 2:
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
        System.out.println("Running the LSNode on port: " + lsNode.serverPort);
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
                                SimpleDirectedWeightedGraph<String, LSEdge> clientLSPacketGraph =
                                        (SimpleDirectedWeightedGraph<String, LSEdge>) objectToRead;
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
        System.out.println("Running the LSNodes Communicator, sit and enjoy how the graph develop over time :D");
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
     * */
    public static void printMenu(){
        System.out.println(
                        "1 - Display current state of routes from current LSNode.\n"+
                        "2 - Exit program"
        );
    }


    /**
     * Shows the LSNode topology as a graph on the screen.
     *
     * @param graph the graph instance of the LSNode
     * @param name the name of the LSNode instance, it will be the title of the window.
     */
    public static void showGraphGUI(SimpleDirectedWeightedGraph<String, LSEdge> graph, String name){
        // declare variables
        JApplet applet = new JApplet();
        ListenableGraph<String, LSEdge> g = new DefaultListenableGraph<>(graph);
        // create a visualization using JGraph, via an adapter
        JGraphXAdapter<String, LSEdge> jgxAdapter = new JGraphXAdapter<>(g);
        applet.getContentPane().add(new mxGraphComponent(jgxAdapter));
        applet.resize(new Dimension(500, 500));
        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        // make the graph static, can't be edited
        jgxAdapter.setCellsBendable(false);
        jgxAdapter.setCellsDeletable(false);
        jgxAdapter.setCellsCloneable(false);
        jgxAdapter.setCellsEditable(false);
        jgxAdapter.setCellsSelectable(false);
        jgxAdapter.setCellsResizable(false);
        jgxAdapter.setEdgeLabelsMovable(false);
        jgxAdapter.setConnectableEdges(false);
        jgxAdapter.setCellsDisconnectable(false);
        jgxAdapter.setEdgeLabelsMovable(false);
        jgxAdapter.setCellsLocked(false);
        jgxAdapter.setConnectableEdges(false);
        jgxAdapter.setAutoOrigin(false);
        jgxAdapter.setSplitEnabled(false);

        // set up the scene and show it to the user
        layout.execute(jgxAdapter.getDefaultParent());
        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle(name);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        positionFrameOnScreen(frame, Math.random(), Math.random());
        frame.toFront();
        frame.setFocusable(true);
        frame.requestFocus();
        boolean aot = frame.isAlwaysOnTop();
        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(aot);
    }

    /**
     * Positions the specified frame at a relative position in the screen, where
     * 50% is considered to be the center of the screen.
     *
     * @param frame the frame.
     * @param horizontalPercent the relative horizontal position of the frame (0.0 to 1.0, where
     * 0.5 is the center of the screen).
     * @param verticalPercent the relative vertical position of the frame (0.0 to 1.0, where 0.5
     *  is the center of the screen).
     */
    public static void positionFrameOnScreen(final Window frame, final double horizontalPercent,
                                             final double verticalPercent) {
        final Rectangle s = getMaximumWindowBounds();
        final Dimension f = frame.getSize();
        final int w = Math.max(s.width - f.width, 0);
        final int h = Math.max(s.height - f.height, 0);
        final int x = (int) (horizontalPercent * w) + s.x;
        final int y = (int) (verticalPercent * h) + s.y;
        frame.setBounds(x, y, f.width, f.height);
    }

    /**
     * Computes the maximum bounds of the current screen device.
     *
     * @return the maximum bounds of the current screen.
     */
    public static Rectangle getMaximumWindowBounds() {
        final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            final Method method = GraphicsEnvironment.class.getMethod("getMaximumWindowBounds",
                    (Class[]) null);
            return (Rectangle) method.invoke(localGraphicsEnvironment, (Object[]) null);
        } catch (Exception e) {
        }
        final Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle(0, 0, s.width, s.height);
    }
}
