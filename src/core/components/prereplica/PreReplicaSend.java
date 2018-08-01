/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica;

import core.management.CoreConfiguration;
import core.management.Lock;
import core.management.Message;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author miguel
 */
public class PreReplicaSend implements Runnable {

    private Socket socket;
    private BlockingQueue outQueue;
    private IPList ip;
    private int port;
    private DataOutputStream out = null;
    private Lock lock;
    private ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 2500).order(ByteOrder.BIG_ENDIAN);

    public PreReplicaSend(IPList ip, Lock lock, int port, BlockingQueue outQueue) {
        this.outQueue = outQueue;
        this.port = port;
        this.ip = ip;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (lock.isLocked()) {
            CoreConfiguration.pause(1);
//            System.out.print(" *");
        }
        lock.lock();
        byte[] data;
        try {
            data = (byte[]) outQueue.take();
            Message m = new Message().deserialize(data, deserialized);
            if (m.getSrc() != 7) {
                port = port + m.getSrc();
            }
            CoreConfiguration.print("trying to connect to=" + ip.getIP(m.getSrc()) + ":" + port);
            socket = new Socket(ip.getIP(m.getSrc()), port);
            while (ip.getIP(m.getSrc()) == null) {
                CoreConfiguration.print("trying to connect to=" + ip.getIP(m.getSrc()) + ":" + port);
                socket = new Socket(ip.getIP(m.getSrc()), port);
                CoreConfiguration.pause(1);
            }
            while (socket == null) {
                CoreConfiguration.print("trying to connect to=" + ip + ":" + port);
                socket = new Socket(ip.getIP(m.getSrc()), port);
            }
            CoreConfiguration.print("Connected to=" + ip.getIP(m.getSrc()) + ":" + port);
            out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(data.length);
            out.write(data);
            out.flush();
            while (true) {
                data = (byte[]) outQueue.take();
                out.writeInt(data.length);
                out.write(data);
                out.flush();
            }
        } catch (Exception ex) {
            close();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
        }
    }

    public void close() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                this.socket.close();
            }
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "close()", ex.getMessage());
        }
    }

}
