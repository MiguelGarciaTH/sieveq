/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.attacker;

import java.util.Scanner;

/**
 *
 * @author miguel
 */
public class ReplicaDoS implements Runnable {

    AttackerTwo attack;
    Thread t;

    public ReplicaDoS(int numberOfThreads, int reqs, String ip, int port, Thread t) {
        this.attack = new AttackerTwo(numberOfThreads, reqs, ip, port);
        this.t = t;
    }

    @Override
    public void run() {
        System.out.println("Waiting for another enter to start! ");
        Scanner n = new Scanner(System.in);
        n.next();
        t.interrupt();
        new Thread(attack).start();

    }

}
