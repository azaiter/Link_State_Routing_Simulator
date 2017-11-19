/**
 * Created by Zaiter on 11/18/2017.
 */
public class TestCaseRunner {
    public static void main(String[] args) throws Exception {
        LSPacket x0 = new LSPacket("0.data");
        LSPacket x1 = new LSPacket("1.data");
        LSPacket x2 = new LSPacket("2.data");
        LSPacket x3 = new LSPacket("3.data");
        LSPacket x4 = new LSPacket("4.data");

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
