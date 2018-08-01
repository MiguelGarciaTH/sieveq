/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import bftsmart.communication.client.ReplyListener;
import bftsmart.reconfiguration.views.View;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;

import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.util.Logger;
import core.management.CoreConfiguration;
import core.management.ServerSession;
import core.management.Message;
import core.management.CoreProperties;
import core.management.Lock;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import core.modules.voter.SimpleVoter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author miguel
 */
public class ServerExecutor implements Runnable, ReplyListener {

    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
//    protected ServiceProxy proxy;
    protected AsynchServiceProxy proxy;
    protected int ID;
    protected SimpleVoter voter;
    protected ConcurrentHashMap<Integer, ServerSession> sessions;
    protected int destination;
    protected int[] processes;
    protected Lock lock;
    private final ArrayBlockingQueue firstQueue;
    private final ArrayBlockingQueue secondQueue;

    private final ArrayBlockingQueue thirdQueue;
//    private final ArrayBlockingQueue orderedQueue;
    private final FirstFilter firstFilter;
    private final SecondFilter secondFilter;
//    private OrderQueue order;
    private final Thread first;
    private final Thread second;
    private final Thread third;
//    private Thread fourth;
    private final ServerReplyManager replyManager;
    private final byte[] cmd;
    private final CryptoScheme crypto;
    private final int[] arrivalCounter;
//    private SimpleVoter voter;
//    private Vo

    private final HashMap<Integer, byte[]> memory;

    public ServerExecutor(int ID) {
//        this.proxy = new ServiceProxy(ID);
        this.proxy = new AsynchServiceProxy(ID);
        this.ID = ID;
//        this.destination = 7;
        this.voter = new SimpleVoter(CoreProperties.num_replicas, CoreProperties.quorom);
        this.sessions = new ConcurrentHashMap<>();
        this.sessions.put(ID, new ServerSession(-1, -1, new int[]{0, 0, 0, 0}, ID, true)); // server -> replicas
        this.lock = new Lock();
        this.lock.lock();
        this.firstQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.secondQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.thirdQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
//        this.orderedQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.firstFilter = new FirstFilter(ID, firstQueue, secondQueue, sessions);
        this.secondFilter = new SecondFilter(secondQueue, thirdQueue, sessions, voter);
        this.arrivalCounter = new int[4];
        this.replyManager = new ServerReplyManager(this, ID, thirdQueue, sessions, proxy, lock, false, arrivalCounter);
        this.first = new Thread(firstFilter);
        this.second = new Thread(secondFilter);
        this.third = new Thread(replyManager);
        this.memory = new HashMap<Integer, byte[]>();

        this.crypto = CryptoSchemeFactory.getCryptoScheme(null);
        this.cmd = new byte[1]; //resolver depois
        new Random().nextBytes(cmd);
        this.proxy.setReplyListener(this);
        
    }

    @Override
    public void run() {
        first.start();
        second.start();
        third.start();
        this.processes = proxy.getViewManager().getCurrentViewProcesses();
        send(Message.HELLO, sessions.get(ID).incrementeOutSequenceNumber(), cmd);
        CoreConfiguration.print("Hello sent waiting confirmation");
        while (lock.isLocked()) {
            System.out.print(".");
            CoreConfiguration.pause(1);
        }
    }

    private Message validate(byte[] data) {
        return new Message().deserialize(data, deserialized);
    }

    private void send(int type, int sequenceNumber, byte[] cmd) {
        Message resp = new Message(type, ID, sequenceNumber, cmd);
        byte[] snt = resp.serialize(serialized1);
//        proxy.invokeAsynchronous(snt, this, processes);
        int ID = proxy.invokeAsynchRequest(snt, processes, this, TOMMessageType.ORDERED_REQUEST);
        proxy.cleanAsynchRequest(sequenceNumber);
//        memory.put(ID, snt);

    }

    private void reconfigureTo(View v) {
        Logger.println("Installing a most up-to-date view with id=" + v.getId());
        proxy.getViewManager().reconfigureTo(v);
        proxy.getViewManager().getViewStore().storeView(v);
        
        proxy.getCommunicationSystem().updateConnections();
    }

    private int invalidId = -10;

    @Override
    public void replyReceived(RequestContext context, TOMMessage reply) {
        
//        if (reply.getSequence()== invalidId) {
//            System.out.println(" IS INVALID!!!");
//            return;
//        }
//        System.out.println("New message seq="+reply.getSequence());
//        if (proxy.getViewManager().getCurrentViewId() != reply.getViewID()) {
//            System.out.println("MUDANÇA DE VIEW!!! VIEW="+reply.getViewID());
//            reconfigureTo((View) TOMUtil.getObject(reply.getContent()));
//            proxy.invoke(memory.get(reply.getId()), TOMMessageType.ORDERED_REQUEST);
//            invalidId = reply.getSequence();
//            return;
//
//        }
//        System.out.println("Mensagem nova vai processar SEQ = " + reply.getSequence());
        if (reply.getContent() != null) {
            byte[] data = reply.getContent();
            try {
                Message resp = validate(data);
//            arrivalCounter[reply.getSender()]++;
//                System.out.println(" PUT MESSAGE  = " + resp);
                firstQueue.put(resp);
            } catch (InterruptedException ex) {
                firstQueue.clear();
                CoreConfiguration.print("Clearing queue... ");
                CoreConfiguration.pause(1);
            }
        }

    }

}
