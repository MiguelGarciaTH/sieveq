/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica;

import core.components.workerpool.DataBlockQueue;
import core.management.BlackList;
import core.management.CoreConfiguration;
import core.management.Lock;
import core.management.Message;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class PreReplicaReceive implements Runnable {

    private final int port;
    private final ArrayBlockingQueue inQueue;
    private final BlackList list;
    private final DataBlockQueue threadBlock;
    private final ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    private final boolean trinco = true;
    private final IPList ips;
    private volatile Lock lock;

    public PreReplicaReceive(int port, IPList ips, Lock lock, ArrayBlockingQueue inQueue, DataBlockQueue threadBlock) {
        this.port = port;
        this.inQueue = inQueue;
        this.threadBlock = threadBlock;
        this.list = new BlackList();
        this.ips = ips;
        this.lock = lock;
    }

    @Override
    public void run() {
        Socket connectionSocket = null;
        try {
            ServerSocket welcomeSocket = new ServerSocket(port);
            CoreConfiguration.print("** waiting connections on port=" + port);
            while (true) {
                connectionSocket = welcomeSocket.accept();
//                if (!list.contains(connectionSocket.getRemoteSocketAddress().toString().split(":")[0])) { 
                String ip = connectionSocket.getRemoteSocketAddress().toString().split(":")[0];
                System.out.println("IP=" + ip);
//                if (ip.contains("192.168.2.35")) {
                    PreReplicaReceiveThread request = new PreReplicaReceiveThread(ips, lock, connectionSocket, inQueue, threadBlock, list);
                    new Thread(request).start();
//                } else if (trinco) {//
//                    trinco = false;
//                    System.out.println("ERRRO!!!!");
//                    Message m = new Message(Message.CHG_PREREPLICA_REQUEST, 7, 0, new byte[]{1});
//                    byte[] data = m.serialize(serialized1);
////                        inQueue.clear();
//                    inQueue.put(new ByteArrayWrap(data, data.length));
//                }
            }
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
        }
    }
}
