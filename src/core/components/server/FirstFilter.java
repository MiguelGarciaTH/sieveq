/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import core.management.ServerSession;
import core.management.Message;
import core.management.CoreConfiguration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class FirstFilter implements Runnable {

    private BlockingQueue inQueue, outQueue;
    private ConcurrentHashMap<Integer, ServerSession> sessions;
    private int ID;

    public FirstFilter(int ID, BlockingQueue inQueue, BlockingQueue outQueue, ConcurrentHashMap<Integer, ServerSession> sessions) {
        this.ID = ID;
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.sessions = sessions;
    }

    @Override
    public void run() {
        ServerSession session;
        while (true) {
            Message resp = null;
            try {
                resp = (Message) inQueue.take();
                session = sessions.get(resp.getSrc());
                if (resp.getSeqNumber() >= session.getInSequenceNumberExpected()) {
                    outQueue.add(resp);
                }
                if (resp.getType() == Message.CHG_PREREPLICA_REQUEST) {
                    outQueue.put(resp);
                }
//                   if (resp.getType() == Message.COUNTER) {
//                    outQueue.put(resp);
//                }
            } catch (NullPointerException ex) {
                if (resp.getSrc() == ID) {
                    sessions.put(ID, new ServerSession(0, 0, new int[]{0, 0, 0, 0}, ID, true));
                    session = sessions.get(ID);
                } else {
                    sessions.put(resp.getSrc(), new ServerSession(resp.getSrc(), 0, new int[]{0, 0, 0, 0}, ID, true));
                    session = sessions.get(resp.getSrc());
                }
                if (resp.getSeqNumber() > session.getInSequenceNumberExpected()) {
                    try {
                        outQueue.put(resp);
                    } catch (InterruptedException ex1) {
                        CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex1.getMessage());
                    }
                }
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run2()", ex.getMessage());
            }
        }
    }
}
