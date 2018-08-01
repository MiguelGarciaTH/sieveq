/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import core.management.Message;
import core.management.CoreConfiguration;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class ReliableChannel extends Channel {

    private HashMap<Integer, Message> buffer;
    private int max_buffered;

    /**
     *
     * @param queue
     */
    public ReliableChannel(BlockingQueue queue, int max_buffered) {
        super(queue);
        this.max_buffered = max_buffered;
        this.buffer = new HashMap<Integer, Message>();
    }

    @Override
    public void queueMessage(Message msg) {
        garbageCollector();
        buffer.put(msg.getSeqNumber(), msg);
    }

    private void garbageCollector() {
        if (buffer.size() == max_buffered) {
            buffer.clear();
        }
    }

    @Override
    public void retrieveMessage(Message msg) {
        int seqs[] = getSequences(msg.getData());
        
        for (int i : seqs) {
            Message m = buffer.remove(i);
            try {
                CoreConfiguration.print("Retreiving seq="+m.getSeqNumber());
                queue.put(m);
            } catch (InterruptedException ex) {
                Logger.getLogger(ReliableChannel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int[] getSequences(byte[] data) {
        String sequence = new String(data); //  1, 2, 3
        int[] sequences = null;
        String[] str;
        if (sequence.contains("-")) {
            str = sequence.split("-");
            int i = Integer.parseInt(str[0]);
            int j = Integer.parseInt(str[1]);
            sequences = new int[j - i + 1];
            for (int k = 0; i <= j; k++) {
                sequences[k] = i++;
            }
        } else {
            str = sequence.split(",");
            sequences = new int[str.length];
            int k = 0;
            for (String string : str) {
                sequences[k++] = Integer.parseInt(string);
            }
        }
        return sequences;
    }
}
