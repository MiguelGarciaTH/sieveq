/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica;

import core.components.workerpool.DataBlockQueue;
import core.components.workerpool.WorkerPool;
import core.management.BlackList;
import core.management.ByteArrayWrap;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.management.Lock;
import core.management.Message;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class PreReplicaReceiveThread implements Runnable {

    private ObjectInputStream in;
    private final Socket connectionSocket;
    private final ArrayBlockingQueue toMAC;

    private final WorkerPool pool;
    private final DataBlockQueue ThreadQueue;
    private final Thread thisThread;
    private final IPList ips;
    private final Lock lock;
    private boolean trinco = true;
    private String ipaux;
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);

    PreReplicaReceiveThread(IPList ips, Lock lock, Socket accept, ArrayBlockingQueue inQueue, DataBlockQueue threadBlock, BlackList list) {
        this.thisThread = Thread.currentThread();
        this.connectionSocket = accept;
        this.ThreadQueue = threadBlock;
        this.ips = ips;
        this.lock = lock;
        this.toMAC = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.pool = new WorkerPool(toMAC, inQueue, threadBlock, "cryptoprereplica", CoreProperties.num_workers);
        ipaux = connectionSocket.getRemoteSocketAddress().toString().split(":")[0];
        ipaux = ipaux.replaceAll("/", "");
        System.out.println("Received= " + ipaux);
        list.add(ipaux);
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
//            int i = 0;
            while (true) {
                int len = in.readInt();
//                if (len > 0 && len < 1500) {
                byte[] data = ThreadQueue.take();
                in.readFully(data, 0, len);
                if (trinco) {
                    Message m = new Message().deserialize(data, deserialized);
                    ips.addIP(m.getSrc(), ipaux);
                    lock.unlock();
                    trinco = false;
                }
                toMAC.add(new ByteArrayWrap(data, len));
//                i++;
//                if (i % 1000 == 0) {
//                    System.out.print(". ");
//                }
//                }
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
            close();
        }
    }

    public void close() {
        
        try {
            if (in != null) {
                this.in.close();
            }
            if (connectionSocket != null) {
                this.connectionSocket.close();
            }
            if (thisThread != null) {
                this.thisThread.interrupt();
            }

        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "close()", ex.getMessage());
        }
    }
}
