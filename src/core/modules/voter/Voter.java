/*            if (toRemove(src, sequenceNumber)) {
 System.out.println("To remove");
 bloom.remove(message);
 return false;
 }
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.voter;

import core.management.ServerSession;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public abstract class Voter {

    //public abstract boolean vote(int src, Session session, int sequenceNumber, byte[] message);

    public abstract boolean vote(int type, int src, ConcurrentHashMap<Integer, ServerSession> sessions, int sequenceNumber, byte[] message);
    public abstract boolean vote(int src, ServerSession session, int sequenceNumber, byte[] message);

    public abstract boolean vote(int src, int sequenceNumber, byte[] message);

}
