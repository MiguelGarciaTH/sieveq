/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.voter;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author miguel
 */
public class Vote {

    private int counter;
    private final CopyOnWriteArrayList<byte[]> messages;
    private final int Quorom;
    private boolean clean;

    public Vote(int replicas, int quorom, byte[] message) {
        this.Quorom = quorom;
        this.messages = new CopyOnWriteArrayList();
        this.counter = 1;
        this.clean = false;
    }

    public boolean clean() {
        return clean;
    }

    public synchronized boolean add(byte[] data) {
        if (!clean) {
            for (byte[] msg : messages) {
                if (Arrays.equals(msg, data)) {
                    counter++;
//                    break;
                }
            }
            messages.add(data);
//            System.out.println("Counter= " + counter);
            if (counter == Quorom) {
                clean = true;
//                System.out.println("Counter = " + counter + " Locked ! ");
                return true;
            }
        }
        return false;
    }

    public int getCounter() {
        return this.counter;
    }

    public String toString() {
        return "" + messages.size();
    }
}
