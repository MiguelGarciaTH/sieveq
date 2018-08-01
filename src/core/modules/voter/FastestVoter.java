/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.voter;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class FastestVoter {

    private final int replicas;
    private final int quorom;
    private final ConcurrentHashMap<Integer, Integer> votes;
    private final ConcurrentHashMap<Integer, Integer> hashBytes;
//    private final ConcurrentHashMap<Integer, Boolean> hasQuorom;
    private int min = 0;

    public FastestVoter(int replicas, int quorom) {
        this.replicas = replicas;
        this.quorom = quorom;
        this.votes = new ConcurrentHashMap<>();
        this.hashBytes = new ConcurrentHashMap<>();
//        this.hasQuorom = new ConcurrentHashMap<>();
    }

    public synchronized boolean vote(int sequence, byte[] data) {
        if (votes.containsKey(sequence)) {
            if (votes.get(sequence) == quorom) {
                return false;
            } else {
                if (hashBytes.get(sequence) == Arrays.hashCode(data)) {
                    int count = votes.get(sequence);
                    count++;
                    votes.put(sequence, count);
                    if (votes.get(sequence) == quorom) {
                        return true;
                    }
                }
            }
        } else {
            votes.put(sequence, 1);
            hashBytes.put(sequence, Arrays.hashCode(data));
        }
        if (sequence % 10000 == 0) {
            for (int i = min; i <= sequence - 1; i++) {
                synchronized (this) {
                    votes.remove(i);
                    hashBytes.remove(i);
                }
            }
            min = sequence;
        }
        return false;
    }

}
