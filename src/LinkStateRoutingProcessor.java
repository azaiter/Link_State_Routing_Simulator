import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Zaiter on 11/18/2017.
 */
public class LinkStateRoutingProcessor {
    public static final String SERVER_IP = "127.0.0.1";
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        int choice;
        LSPacket lsPacket = new LSPacket(args[0]);
        boolean serverRunning = false;
        boolean clientRunning = false;

        do {
            printMenu(serverRunning, clientRunning);
            try {
                choice = sc.nextInt();
                switch (choice) {
                    case 1:
                        if (!serverRunning) {
                            serverRunning = true;
                            runServer(lsPacket);
                        } else System.out.println("Server is running! you can't run it twice.");
                        break;
                    case 2:
                        clientRunning = true;
                        runClient(lsPacket);
                        break;
                    case 3:
                        System.out.println(lsPacket);
                        break;
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

    public static void runServer(LSPacket lsPacket){
        new Thread(()->{
            try {
                ServerSocket srvSocket = new ServerSocket(lsPacket.serverPort);
                try {
                    while (true) {
                        Socket socket = srvSocket.accept();
                        // initialize input and output streams.
                        new Thread(() -> {
                            try {
                                ReentrantLock lock = new ReentrantLock();
                                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                                ObjectOutput outputStream = new ObjectOutputStream(socket.getOutputStream());
                                //while (true)
                                {
                                    Object objectToRead = inputStream.readObject();
                                    socket.close();
                                    lock.lock();
                                    SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> clientLSPacketGraph =
                                            (SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>) objectToRead;
                                    lock.unlock();
                                    lsPacket.addEdgesFromAnotherLSPacket(clientLSPacketGraph);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e){e.printStackTrace();}
        }).start();
    }


    public static void runClient(LSPacket lsPacket){
        new Thread(()->{
            while(true)
            {
                for (Integer portNum : lsPacket.adjacentLSPacketsPorts) {
                    new Thread(() -> {
                        try {
                            ReentrantLock lock = new ReentrantLock();
                            Socket socket = new Socket(SERVER_IP, portNum);
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                            lock.lock();
                            outputStream.writeObject(lsPacket.lsPacketGraph);
                            lock.unlock();
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                    try {
                        Thread.sleep(5000);
                    }catch (Exception e) {e.printStackTrace();}
                }
            }
        }).start();
    }


    public static void printMenu(boolean serverRunning, boolean clientRunning){
        System.out.println(
                        "1 - Run LSPacket Server. Currently: "+serverRunning+"\n" +
                        "2 - Once you have all Packets up, run LSPacket communicator. Currently: "+clientRunning+"\n" +
                        "3 - Display current Routes from current LSPacket.\n"+
                        "4 - Exit program"
        );
    }

}
