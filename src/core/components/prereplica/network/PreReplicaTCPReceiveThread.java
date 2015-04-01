/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica.network;

import core.components.workerpool.ThreadBlockQueue;
import core.management.BlackList;
import core.message.Message;
import core.management.CoreConfiguration;
import core.message.ByteArrayWrap;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class PreReplicaTCPReceiveThread implements Runnable {

    private int port;
    private ArrayBlockingQueue inQueue;
    private BlackList list;
    private ThreadBlockQueue threadBlock;
    private ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    private boolean trinco = true;

    public PreReplicaTCPReceiveThread(int port, ArrayBlockingQueue inQueue, ThreadBlockQueue threadBlock) {
        this.port = port;
        this.inQueue = inQueue;
        this.threadBlock = threadBlock;
        this.list = new BlackList();

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
                    PreReplicaTCPReceiveClients request = new PreReplicaTCPReceiveClients(connectionSocket, inQueue, threadBlock, list);
                    Thread thread = new Thread(request);
                    thread.start();
//                } else {
//                    if (trinco) {
//                        Message m = new Message(Message.CHG_PREREPLICA_REQUEST, 7, 0, new byte[]{1});
//                        byte[] data = m.serialize(serialized1);
////                        inQueue.clear();
//                        inQueue.put(new ByteArrayWrap(data, data.length));
//                        trinco = false;
//                    }
//                }
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
        }
    }
}
