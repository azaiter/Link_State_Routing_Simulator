/**
 * Created by Zaiter on 11/18/2017.
 */
public class TestCaseRunner {
    public static void main(String[] args) throws Exception {
        LSNode x0 = new LSNode("0.data");
        LSNode x1 = new LSNode("1.data");
        LSNode x2 = new LSNode("2.data");
        LSNode x3 = new LSNode("3.data");
        LSNode x4 = new LSNode("4.data");

        x0.addEdgesFromAnotherLSPacket(x1.lsPacketGraph);
        x0.addEdgesFromAnotherLSPacket(x2.lsPacketGraph);
        x0.addEdgesFromAnotherLSPacket(x3.lsPacketGraph);
        x0.addEdgesFromAnotherLSPacket(x4.lsPacketGraph);

        x1.addEdgesFromAnotherLSPacket(x0.lsPacketGraph);
        x1.addEdgesFromAnotherLSPacket(x2.lsPacketGraph);
        x1.addEdgesFromAnotherLSPacket(x3.lsPacketGraph);
        x1.addEdgesFromAnotherLSPacket(x4.lsPacketGraph);

        x2.addEdgesFromAnotherLSPacket(x1.lsPacketGraph);
        x2.addEdgesFromAnotherLSPacket(x0.lsPacketGraph);
        x2.addEdgesFromAnotherLSPacket(x3.lsPacketGraph);
        x2.addEdgesFromAnotherLSPacket(x4.lsPacketGraph);

        x3.addEdgesFromAnotherLSPacket(x1.lsPacketGraph);
        x3.addEdgesFromAnotherLSPacket(x2.lsPacketGraph);
        x3.addEdgesFromAnotherLSPacket(x0.lsPacketGraph);
        x3.addEdgesFromAnotherLSPacket(x4.lsPacketGraph);

        x4.addEdgesFromAnotherLSPacket(x1.lsPacketGraph);
        x4.addEdgesFromAnotherLSPacket(x2.lsPacketGraph);
        x4.addEdgesFromAnotherLSPacket(x3.lsPacketGraph);
        x4.addEdgesFromAnotherLSPacket(x0.lsPacketGraph);


        System.out.println(x0);
        System.out.println(x1);
        System.out.println(x2);
        System.out.println(x3);
        System.out.println(x4);

    }
}
