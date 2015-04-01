/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.reliableChannel;

import core.message.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author miguel
 */
public class tester {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10);
        ReliableChannel channel = new ReliableChannel(queue,3);
        Message m1 = new Message(0, 1, 1, new String("").getBytes());
        Message m2 = new Message(0, 1, 2, new String("").getBytes());
        Message m3 = new Message(0, 1, 3, new String("").getBytes());
        Message m4 = new Message(0, 1, 4, new String("").getBytes());
        Message m5 = new Message(0, 1, 5, new String("").getBytes());
        Message m6 = new Message(0, 1, 6, new String("").getBytes());
        Message m = new Message(0, 1, 5, new String("5-6").getBytes());
        channel.queueMessage(m1);
        channel.queueMessage(m2);
        channel.queueMessage(m3);
        channel.queueMessage(m4);
        channel.queueMessage(m5);
        channel.queueMessage(m6);
        channel.retrieveMessage(m);

    }

}
