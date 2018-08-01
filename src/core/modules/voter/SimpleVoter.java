/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.voter;

import core.management.ServerSession;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class SimpleVoter {

    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Vote>> votes;
    private final int replicas;
    private final int quorom;
    private int expected = 0;
    private Vote vote = null;
    private boolean hasQuorom = false;
    private final HashSet<String> history;

    public SimpleVoter(int replicas, int quorom) {
        this.replicas = replicas;
        this.quorom = quorom;
        this.votes = new ConcurrentHashMap<>();
        this.history = new HashSet<>();

    }

    public SimpleVoter(int replicas, int quorom, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Vote>> votes) {
        this.replicas = replicas;
        this.quorom = quorom;
        this.votes = votes;
        this.history = new HashSet<>();
    }

    public synchronized boolean vote(int type, int src, ConcurrentHashMap<Integer, ServerSession> sessions, int sequenceNumber, byte[] message) {
        vote = getVote(src, sequenceNumber, getSequenceNumberList(src), message);
        hasQuorom = checkVote(vote, message);
//&& !history.contains(src+" "+sequenceNumber)
        if (hasQuorom) {
//            history.add(src+" "+sequenceNumber);
//            System.out.println("Message =" + sequenceNumber);
//            expected++;
//            sessions.get(src).incrementeInSequenceNumber();
            if (sequenceNumber % 1000 == 0) {
                cleanVotes(sessions.get(src));
            }
            return hasQuorom;
        }
        return false;

    }

    public boolean vote(int src, ServerSession session, int sequenceNumber, byte[] message) {
        vote = getVote(src, sequenceNumber, getSequenceNumberList(src), message);
        hasQuorom = checkVote(vote, message);
        if (hasQuorom) {
            session.incrementeInSequenceNumber();
            if (sequenceNumber % 1000 == 0) {
                cleanVotes(session);
            }
        }
        return hasQuorom;
    }

    private ConcurrentHashMap<Integer, Vote> getSequenceNumberList(int src) {
        if (votes.get(src) == null) {
            votes.putIfAbsent(src, new ConcurrentHashMap<>());
        }
        return votes.get(src);
    }

    private Vote getVote(int src, int sequenceNumber, ConcurrentHashMap<Integer, Vote> seqList, byte[] data) {
        if (seqList.get(sequenceNumber) != null) {
            return seqList.get(sequenceNumber);
        } else {
            seqList.put(sequenceNumber, new Vote(replicas, quorom, data));
            votes.putIfAbsent(src, seqList);
            return seqList.get(sequenceNumber);
        }
    }

    private boolean checkVote(Vote vote, byte[] data) {
        return vote.add(data);
    }

    private void cleanVotes(ServerSession session) {
        Iterator<Map.Entry<Integer, ConcurrentHashMap<Integer, Vote>>> srcs = votes.entrySet().iterator();
        while (srcs.hasNext()) {
            Iterator<Map.Entry<Integer, Vote>> k = srcs.next().getValue().entrySet().iterator();
            while (k.hasNext()) {
                Map.Entry<Integer, Vote> v = k.next();
                if (v.getKey() < session.getOutSequenceNumberExpected() || v.getValue().clean()) {
                    k.remove();
                }
            }
        }
    }

    public void printState() {
        Set<Integer> keys = votes.keySet();
        for (Integer key : keys) {
            ConcurrentHashMap<Integer, Vote> values = votes.get(key);
            Set<Integer> keys2 = values.keySet();
            System.out.println("Src=" + key + " pendentMessages=" + keys.size());
            for (Integer key2 : keys2) {
                System.out.println("Sequence Number=" + key2);
                //System.out.println("\tNumber of votes=" + values.get(key2).getCounter());
            }
            System.out.println("_____________________________________________________");
        }

    }

}
