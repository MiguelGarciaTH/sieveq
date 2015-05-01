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
import core.message.Message;
import core.management.CoreConfiguration;
import core.modules.voter.Voter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class SeconderServerFilterWorker implements Runnable {

    private ConcurrentHashMap<Integer, ServerSession> sessions;
    private Voter voter;
    private ArrayBlockingQueue in, out;
    private int tid;

    public SeconderServerFilterWorker(int tid, ArrayBlockingQueue in, ArrayBlockingQueue out, ConcurrentHashMap<Integer, ServerSession> sessions, Voter voter) {
        this.tid = tid;
        this.in = in;
        this.out = out;
        this.sessions = sessions;
        this.voter = voter;

    }

    public void run() {
        while (true) {
            try {
                Message resp = (Message) in.take();
                
                boolean quorom = voter.vote(resp.getType(), resp.getSrc(), sessions, resp.getSeqNumber(), resp.getData());
                if (quorom) {
                    out.add(resp);
                }
                if(resp.getType() == Message.COUNTER){
                    out.add(resp);
                }
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run tid=" + tid, ex.getMessage());
            }
        }
    }

}
