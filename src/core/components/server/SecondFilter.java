/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import core.management.ServerSession;
import core.management.CoreProperties;
import core.modules.voter.FastestVoter;
import core.modules.voter.SimpleVoter;
import core.modules.voter.Vote;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class SecondFilter implements Runnable {

    private final ArrayBlockingQueue inQueue;
    private final ArrayBlockingQueue outQueue;
    private final ConcurrentHashMap<Integer, ServerSession> sessions;
//    private final SimpleVoter voter;
    private final FastestVoter voter;
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Vote>> votes;

    public SecondFilter(ArrayBlockingQueue inQueue, ArrayBlockingQueue outQueue, ConcurrentHashMap<Integer, ServerSession> sessions, SimpleVoter voter) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.votes = new ConcurrentHashMap<>();
//        this.voter = new SimpleVoter(CoreProperties.num_replicas, CoreProperties.quorom, votes);
        this.voter = new FastestVoter(CoreProperties.num_replicas, CoreProperties.quorom);
        this.sessions = sessions;
    }

    @Override
    public void run() {
        for (int i = 0; i < CoreProperties.num_workers; i++) {
            new Thread(new SecondFilterWorker(i, inQueue, outQueue, sessions, voter)).start();
        }
    }
}
