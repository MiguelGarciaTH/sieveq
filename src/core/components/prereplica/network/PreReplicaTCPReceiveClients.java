/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica.network;

import core.components.workerpool.ThreadBlockQueue;
import core.components.workerpool.WorkerPool;
import core.management.BlackList;
import core.message.ByteArrayWrap;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class PreReplicaTCPReceiveClients implements Runnable {

    private ObjectInputStream in;
    private Socket connectionSocket;
    private ArrayBlockingQueue toMAC;
    private boolean attacked = false;
    private WorkerPool pool;
    private ThreadBlockQueue ThreadQueue;
    private Thread thisThread;

    PreReplicaTCPReceiveClients(Socket accept, ArrayBlockingQueue inQueue, ThreadBlockQueue threadBlock, BlackList list) {
        this.thisThread = Thread.currentThread();
        this.connectionSocket = accept;
        this.ThreadQueue = threadBlock;
        // this.inQueue = inQueue;
        this.toMAC = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.pool = new WorkerPool(toMAC, inQueue, threadBlock, "cryptoprereplica", CoreProperties.num_workers);
        list.add(connectionSocket.getRemoteSocketAddress().toString().split(":")[0]);
        System.out.println("Added to list=" + connectionSocket.getRemoteSocketAddress().toString());
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(new BufferedInputStream(connectionSocket.getInputStream()));
            int i = 0;
            while (true) {
                int len = in.readInt();
//                if (len > 0 && len < 1500) {
                    byte[] data = ThreadQueue.take();
                    in.readFully(data, 0, len);
                    toMAC.add(new ByteArrayWrap(data, len));
                    i++;
                    if (i % 1000 == 0) {
                        System.out.print(". ");
                    }
//                }
            }
        } catch (Exception ex) {
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
