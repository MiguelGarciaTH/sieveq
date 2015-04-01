/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.ServiceProxy;
import core.management.ServerSession;
import core.message.Message;
import core.management.CoreProperties;
import core.misc.Lock;
import core.modules.voter.SimpleVoter;
import core.modules.voter.Voter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public abstract class ServerExecutor implements Runnable, ReplyListener {

    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected ServiceProxy proxy;
    protected int ID;
    protected Voter voter;
    protected ConcurrentHashMap<Integer, ServerSession> sessions;
    protected int destination;
    protected int[] processes;
    protected Lock lock;

    public ServerExecutor(int ID) {
        this.proxy = new ServiceProxy(ID);
        this.ID = ID;
        this.destination = 7;
        this.voter = new SimpleVoter(CoreProperties.num_replicas, CoreProperties.quorom);
        this.sessions = new ConcurrentHashMap<Integer, ServerSession>();
        this.sessions.put(ID, new ServerSession(-1, -1, new int[]{0, 0, 0, 0}, ID, true)); // server -> replicas
        this.lock = new Lock();
        this.lock.lock();
    }
}
