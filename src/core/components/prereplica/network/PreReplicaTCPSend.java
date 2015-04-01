/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica.network;

import core.management.CoreConfiguration;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class PreReplicaTCPSend implements Runnable {

    private Socket socket;
    private BlockingQueue outQueue;
    private String ip;
    private int port;
    private DataOutputStream out = null;

    public PreReplicaTCPSend(String ip, int port, BlockingQueue outQueue) {
        this.outQueue = outQueue;
        this.port = port;
        this.ip = ip;

    }

    @Override
    public void run() {

        byte[] data;
        try {
            data = (byte[]) outQueue.take();
            CoreConfiguration.print("trying to connect to=" + ip + ":" + port);
            socket = new Socket(ip, port);
            while (socket == null) {
                CoreConfiguration.print("trying to connect to=" + ip + ":" + port);
                socket = new Socket(ip, port);
            }
            CoreConfiguration.print("Connected to=" + ip + ":" + port);
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
            //ex.printStackTrace();
            close();
//            if (ex.getMessage().contains("refused")) {
//                new PreReplicaTCPSend(ip, port, outQueue).run();
//            }
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
