/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client.network;

import core.components.workerpool.ThreadBlockQueue;
import core.message.ByteArrayWrap;
import core.management.CoreConfiguration;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author miguel
 */
public class ClientTCPSend implements Runnable {

    private RemindTask task;
    private int numberofMessages = 0;
    private Timer timer;
    private Socket socket = null;
    private ArrayBlockingQueue outQueue;
    private LinkedBlockingQueue inQueue;
    private ObjectOutputStream out = null;
    private Thread thisThread;

    public long rounds = 0;
    public long secondsOffset;
    private ThreadBlockQueue blockQueue;

    private String ip;
    private int port;

    public ClientTCPSend(String ip, int port, ArrayBlockingQueue outQueue, LinkedBlockingQueue inQueue, ThreadBlockQueue threaqueue) {
        this.outQueue = outQueue;
        this.inQueue = inQueue;
        this.blockQueue = threaqueue;
        this.ip = ip;
        this.port = port;
        task = new RemindTask();
        timer = new Timer();
        timer.schedule(task, 0, 1000); //delay in milliseconds
        try {
            socket = new Socket(ip, port);
            CoreConfiguration.print("Connected to=" + ip + ":" + port);
        } catch (IOException ex) {
            System.out.println("refused to connecto to=" + ip + ":" + port);
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "ClientTCPSend()", ex.getMessage());
            while (socket == null || !socket.isConnected()) {
                try {
                    System.out.println("trying to connecto to=" + ip + ":" + port);
                    CoreConfiguration.pause(1);
                    socket = new Socket(ip, port);
                } catch (IOException ex1) {
                    System.out.println("refused to connecto to=" + ip + ":" + port);
                    CoreConfiguration.printException(this.getClass().getCanonicalName(), "ClientTCPSend() - inside", ex1.getMessage());
                }
            }
            CoreConfiguration.print("Connected to=" + ip + ":" + port);
        }
    }

    @Override
    public void run() {
        thisThread = Thread.currentThread();
        ByteArrayWrap data;
        try {
            out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            while (true) {
                data = (ByteArrayWrap) outQueue.take();
                out.writeInt(data.getSize());
                out.write(data.getArr(), 0, data.getSize());
                out.flush();
                blockQueue.offer(data.getArr());
                numberofMessages++;
            }
        } catch (Exception ex) {
            close();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
        }
    }

    public void close() {
        try {
            if (timer != null) {
                timer.cancel();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (thisThread.isAlive()) {
                thisThread.interrupt();
            }
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "close()", ex.getMessage());

        }
    }

    class RemindTask extends TimerTask {

        int i = 0;
        int counter = 0;

        RemindTask() {
        }

        @Override
        public void run() {
            System.out.println("#sent=" + numberofMessages + " second=" + i++);// + " | > rate=" + moreRate + " | > 1sec=" + timeStop);
            if (numberofMessages == 0) {
                counter++;
                if (counter == 10) {
                    this.cancel();
                }
            }
            numberofMessages = 0;
        }
    }
}
