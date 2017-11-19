import java.io.File;
import java.util.*;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

public class LSPacket {
    String packetID;
    int serverPort;
    ArrayList<Integer> adjacentLSPacketsPorts = new ArrayList<>();

    SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> lsPacketGraph =
            new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>
                    (DefaultWeightedEdge.class);

    LSPacket(String fileName) throws Exception{
        Scanner sc = new Scanner(new File(fileName));
        packetID = sc.next();
        serverPort = sc.nextInt();
        lsPacketGraph.addVertex(packetID);


        int numOfNodes = sc.nextInt();
        for(int i = 0; i< numOfNodes; i++){
            String nodeToRead = sc.next();
            lsPacketGraph.addVertex(nodeToRead);
            adjacentLSPacketsPorts.add(sc.nextInt());
        }
        int numOfEdges = sc.nextInt();
        for (int i=0; i< numOfEdges; i++){
            String from = sc.next();
            String to = sc.next();
            int weight = sc.nextInt();
            if(!lsPacketGraph.containsEdge(from,to) || !lsPacketGraph.containsEdge(to,from)){ //you can't have duplicated edge
                    DefaultWeightedEdge edgeFromTo = lsPacketGraph.addEdge(from, to);
                    lsPacketGraph.setEdgeWeight(edgeFromTo, weight);
                    DefaultWeightedEdge edgeToFrom = lsPacketGraph.addEdge(to, from);
                    lsPacketGraph.setEdgeWeight(edgeToFrom, weight);
            }

        }
    }

    void addEdgesFromAnotherLSPacket(SimpleDirectedWeightedGraph<String, DefaultWeightedEdge> inLSPacket){
        //add if nodes isn't in the graph
        inLSPacket.vertexSet().forEach((v)->{
            if(!this.lsPacketGraph.containsVertex(v)){
                //System.out.println("Adding node: "+ v);
                this.lsPacketGraph.addVertex(v);
            }
        });

        inLSPacket.edgeSet().forEach((v)->{
            if(!lsPacketGraph.containsEdge(inLSPacket.getEdgeSource(v),inLSPacket.getEdgeTarget(v))){ //you can't have duplicated edge
                DefaultWeightedEdge edge = lsPacketGraph.addEdge(inLSPacket.getEdgeSource(v),inLSPacket.getEdgeTarget(v));
                lsPacketGraph.setEdgeWeight(edge, inLSPacket.getEdgeWeight(v));
            }
            if(!lsPacketGraph.containsEdge(inLSPacket.getEdgeTarget(v),inLSPacket.getEdgeSource(v))){ //you can't have duplicated edge
                DefaultWeightedEdge edge = lsPacketGraph.addEdge(inLSPacket.getEdgeTarget(v),inLSPacket.getEdgeSource(v));
                lsPacketGraph.setEdgeWeight(edge, inLSPacket.getEdgeWeight(v));
            }
        });
    }


    @Override
    public String toString(){
        StringBuilder toReturn = new StringBuilder();
        toReturn.append("ID: "+packetID+"\nShortest Path From "+packetID+" to all other LSPackets:\n");

        lsPacketGraph.vertexSet().forEach((v)->{
            DijkstraShortestPath dijkstraShortestPath
                    = new DijkstraShortestPath(lsPacketGraph);
            List shortest_path = dijkstraShortestPath.getPath(packetID,v).getVertexList();
            toReturn.append("From " + packetID + " to " + v + " you take this path: "+shortest_path+"\n");
        });
        return toReturn.toString();
    }
}
