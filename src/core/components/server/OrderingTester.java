/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import core.message.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class OrderingTester {

    public static void main(String[] args) {

        ArrayBlockingQueue in = new ArrayBlockingQueue(5);
        ArrayBlockingQueue out = new ArrayBlockingQueue(5);
        OrderQueue order = new OrderQueue(in, out, 5);
        new Thread(order).start();
        Message m1, m2, m3, m4, m5, m6;
        m1 = new Message(1, 0, 1, new byte[]{1});
        m2 = new Message(1, 1, 2, new byte[]{1});
        m3 = new Message(1, 2, 3, new byte[]{1});
        m4 = new Message(1, 3, 4, new byte[]{1});
        m5 = new Message(1, 4, 5, new byte[]{1});
        m6 = new Message(1, 5, 6, new byte[]{1});
        try {
            in.put(m5);
            in.put(m4);
            in.put(m1);
            in.put(m3);
            in.put(m2);
            in.put(m6);
            while (true) {
                Message m = (Message) out.take();
                System.out.println("Resp=>" + m.getSeqNumber());
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(OrderingTester.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
