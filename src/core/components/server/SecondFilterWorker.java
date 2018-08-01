/*
 /*
 /*
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import core.management.ServerSession;
import core.management.Message;
import core.management.CoreConfiguration;
import core.modules.voter.FastestVoter;
import core.modules.voter.SimpleVoter;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class SecondFilterWorker implements Runnable {

    private final ConcurrentHashMap<Integer, ServerSession> sessions;
    private final FastestVoter voter;
    private final ArrayBlockingQueue in;
    private final ArrayBlockingQueue out;
    private final int tid;
    private final HashSet<Integer> history;

    public SecondFilterWorker(int tid, ArrayBlockingQueue in, ArrayBlockingQueue out, ConcurrentHashMap<Integer, ServerSession> sessions, FastestVoter voter) {
        this.tid = tid;
        this.in = in;
        this.out = out;
        this.sessions = sessions;
        this.voter = voter;
        this.history = new HashSet<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message resp = (Message) in.take();
                boolean quorom = voter.vote(resp.getSeqNumber(), resp.getData());
//                boolean quorom = voter.vote(resp.getType(), resp.getSrc(), sessions, resp.getSeqNumber(), resp.getData());
                //!history.contains(resp.getSeqNumber()) &&
                if ( quorom) {
//                    history.add(resp.getSrc());
                    out.add(resp);

                }

//                if (resp.getType() == Message.COUNTER) {
//                    out.add(resp);
//                }
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run tid=" + tid, ex.getMessage());
            }
        }
    }

}
