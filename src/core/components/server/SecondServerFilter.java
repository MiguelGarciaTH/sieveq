/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import core.management.ServerSession;
import core.management.CoreProperties;
import core.modules.voter.SimpleVoter;
import core.modules.voter.Vote;
import core.modules.voter.Voter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class SecondServerFilter implements Runnable {

    private ArrayBlockingQueue inQueue, outQueue;
    private ConcurrentHashMap<Integer, ServerSession> sessions;
    private Voter voter;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Vote>> votes;

    public SecondServerFilter(ArrayBlockingQueue inQueue, ArrayBlockingQueue outQueue, ConcurrentHashMap<Integer, ServerSession> sessions, Voter voter) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.votes = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Vote>>();
        this.voter = new SimpleVoter(CoreProperties.num_replicas, CoreProperties.quorom, votes);
        this.sessions = sessions;
    }

    @Override
    public void run() {
        for (int i = 0; i < CoreProperties.num_workers; i++) {
            new Thread(new SeconderServerFilterWorker(i, inQueue, outQueue, sessions, voter)).start();
        }
    }
}
