# Link State Routing Simulator
The goal of this project is to demonstrate the working of link-state(LS) networking routing technique using this
simulator that uses Dijkstra's OSP algorithm. 

# Basic Requirements
#### Each node should:
1. Generate LS packet with each packet containing the identities and costs of its attached links. 
(Assuming that each node already has this information stored in a file. In real networks, 
initially every node will explore its direct neighbors and generate a local topology.)

2. Broadcast LS packets to all adjecent nodes in the network of that LSNode. The result of the nodes' broadcast
 is that all nodes have an identical and complete view of the network.
 
3. Each node can then run the LS routing algorithm and compute the same set of least-cost paths as every other node.
 (For this project, it uses the Dijkstra's algorithm). 
 When the LS algorithm terminates, each node should display the resulting least cost path and forwarding table.

# Implementation (Java)
#### For each of requirements above:
1. LS packet is generated through a LSNode instance using a configuration file that is passed to the constructor.
the file contains all the sufficient information to build its own topology and connection information for its
adjacent LSNodes in the network. The topology is basically a directed weighted graph stored in the LSNode
and can expand easily by adding more nodes on the fly by running them on the network.

2. Broadcasting the LS packets is basically done by a LS routing processor that runs a server that will be acting
as the communicator about current LS link with other links that request the graph topology. In addition to a 
client communicator that requests the network topology from all of its intermediate connections.

3. Each LSNode will run Dijkstra's algorithm when a toString method is called on the LSNode, as it will build the 
shortest path from LSNode to all LSNodes that are saved in its local graph. This allows the user to view the changes
that are happening over that time when the LSNodes in the network communicate with each other with their own
topology and expand overtime, this program allows that easily by running a new LS Routing Processor instance with 
its own settings, as it will broadcast its information easily and dynamically over the network. So no worries about
concurrency and expansion at all :)

# How to Run
1. Add LSNode settings file that follows the following scheme (updated scheme always in smaple.data):
```text
LSNode ID
LSNode Server Port

number of nodes
<List them with their server port>

number of edges
<edge from U node to V node with W weight>
```
Sample File:
```text
0
30000

2
1 30001 2 30002

2
0 1 1
0 2 6
```

2. Run the program with an argument containing the name of the LSNode settings file. This will act as a single 
node in the network and you need more to build you're own network. 

3. Run the server on all nodes first by typing 1, then after running all of them you can run the client 
communicator by typing 2. You can always view the local topology by typing 3.

# Example
Have a look at "Graph.gif" that has implementation with its "0.data" to "4.data" files.

This following video shows how it is working and communicating with each other.
[![Link State Routing Simulator](https://i.imgur.com/Wzm0sDN.png)](https://www.youtube.com/watch?v=Q_QYbKwNmNM&feature=youtu.be "Everything Is AWESOME")

# Contribution and Special Thanks
* <b>Zack Zaiter (Abdulrahman)</b>: Programmer.
* <b>Ryan Casiglione</b>: Emotional Support Specialist and discussions invoker.
* Thanks to <b>Dr. Poonam</b> for this great project idea to explore our skills in programming and networking.