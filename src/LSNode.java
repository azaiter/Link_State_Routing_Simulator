import java.io.File;
import java.util.*;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
/**
 * <h1>LSNode</h1>
 *
 * <h2>LS Link class that acts as a single LS packet on the network.</h2>
 *
 * <p>It does the Following:</p>
 * <ol>
 *     <li>Initiates a LS Link from a settings file with a local network graph with its adjacent links</li>
 *     <li>Has the ability to expand its topology by recieving other topologies
 *     from other LS links instances or topology instances</li>
 *     <li>Prints the shortest path from its LS link to all other connected LS links in the network
 *     that have been known</li>
 * </ol>
 *
 * Created by Zack Zaiter (Abdulrahman) & Ryan Castiglione on 11/16/2017.
 *
 * @author Zack Zaiter (Abdulrahman) & Ryan Castiglione.
 * @since 11/16/2017
 */
public class LSNode {
    // class members
    private String packetID;
    int serverPort;
    ArrayList<Integer> adjacentLSPacketsPorts = new ArrayList<>();
    SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> lsPacketGraph =
            new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>
                    (DefaultWeightedEdge.class);

    /**
     * The class constructor that will Initiates a LS Link from a settings file with
     * a local network graph with its adjacent links
     *
     * The file has to be of the following format:
     *           LSNode ID
     *           LSNode Server Port
     *
     *           number of nodes
     *           <List them with their server port>
     *
     *           number of edges
     *           <edge from U node to V node with W weight>
     * Example:
     *
     *           0
     *           30000
     *
     *           2
     *           1 30001 2 30002
     *
     *           2
     *           0 1 1
     *           0 2 6
     *
     * @param fileName The file name of the file that contains the LS packet settings.
     * @throws Exception when the LSNode fails to initiate from a settings file
     */
    LSNode(String fileName) throws Exception{
        //initiate the variables
        Scanner sc = new Scanner(new File(fileName));
        packetID = sc.next();
        serverPort = sc.nextInt();
        lsPacketGraph.addVertex(packetID);

        // read number of nodes and iterate through them
        int numOfNodes = sc.nextInt();
        for(int i = 0; i< numOfNodes; i++){
            String nodeToRead = sc.next();
            lsPacketGraph.addVertex(nodeToRead);
            adjacentLSPacketsPorts.add(sc.nextInt());
        }

        // read number of edges and iterate through them
        int numOfEdges = sc.nextInt();
        for (int i=0; i< numOfEdges; i++){
            String from = sc.next();
            String to = sc.next();
            int weight = sc.nextInt();

            // you can't have duplicated edges in the network, skip if applicable
            // add the edge into the graph
            if(!lsPacketGraph.containsEdge(from,to) || !lsPacketGraph.containsEdge(to,from)){
                    DefaultWeightedEdge edgeFromTo = lsPacketGraph.addEdge(from, to);
                    lsPacketGraph.setEdgeWeight(edgeFromTo, weight);
                    DefaultWeightedEdge edgeToFrom = lsPacketGraph.addEdge(to, from);
                    lsPacketGraph.setEdgeWeight(edgeToFrom, weight);
            }
        }
    }

    /**
     * A method that has the ability to expand its topology by recieving other topologies
     * from other LS links instances or topology instances
     *
     * @param inLSPacket a valid LSNode instance that has a valid topology.
     */
    void addEdgesFromAnotherLSPacket(SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> inLSPacket){
        //add if nodes isn't in the graph
        inLSPacket.vertexSet().forEach((v)->{
            if(!this.lsPacketGraph.containsVertex(v)){
                this.lsPacketGraph.addVertex(v);
            }
        });

        // for each edge in the given LS packet, add the edges into the local topology.
        inLSPacket.edgeSet().forEach((v)->{
            // you can't have duplicated edges in the network, skip if applicable
            if(!lsPacketGraph.containsEdge(inLSPacket.getEdgeSource(v),inLSPacket.getEdgeTarget(v))){ //you can't have duplicated edge
                DefaultWeightedEdge edge = lsPacketGraph.addEdge(inLSPacket.getEdgeSource(v),inLSPacket.getEdgeTarget(v));
                lsPacketGraph.setEdgeWeight(edge, inLSPacket.getEdgeWeight(v));
            }
            // do it for both sides as the graph is directed.
            if(!lsPacketGraph.containsEdge(inLSPacket.getEdgeTarget(v),inLSPacket.getEdgeSource(v))){ //you can't have duplicated edge
                DefaultWeightedEdge edge = lsPacketGraph.addEdge(inLSPacket.getEdgeTarget(v),inLSPacket.getEdgeSource(v));
                lsPacketGraph.setEdgeWeight(edge, inLSPacket.getEdgeWeight(v));
            }
        });
    }


    /**
     * An overriden toString function that will return the shortest path from the current instance
     * to all others learned graph instances in the network.
     *
     * @return a string of the shortest path from the current instance
     * to all others learned graph instances in the network.
     */
    @Override
    public String toString(){
        // initiate an empty forwading table
        ArrayList<String[]> forwadingTable = new ArrayList<>();
        // we use a StringBuilder because we are smart people.
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("ID: "+packetID+"\nShortest Path From "+packetID+" to all other LSPackets:\n");

        // apply Dijkstra's algorithm for each vertex in the graph and add the path given into the string
        lsPacketGraph.vertexSet().forEach((v)->{
            DijkstraShortestPath dijkstraShortestPath
                    = new DijkstraShortestPath(lsPacketGraph);
            List shortest_path = dijkstraShortestPath.getPath(packetID,v).getVertexList();
            if(!v.equals(packetID)) {
                String[] singleForwadingEntry = {v, shortest_path.get(1).toString()};
                forwadingTable.add(singleForwadingEntry);
            }
            toReturn.append("From " + packetID + " to " + v + " you take this path: "+shortest_path+"\n");
        });

        // add forwarding table
        toReturn.append("\nForwarding Table (Dst -> Node to take):\n");
        forwadingTable.forEach((v)->{
            toReturn.append(v[0]).append(" -> ").append(v[1]).append("\n");
        });
        toReturn.append("\n");

        // return the built string.
        return toReturn.toString();
    }
}
