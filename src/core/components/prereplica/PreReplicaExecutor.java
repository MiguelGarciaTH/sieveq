/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica;

import bftsmart.communication.client.ReplyListener;
import bftsmart.reconfiguration.views.View;
import bftsmart.tom.AsynchServiceProxy;
import bftsmart.tom.RequestContext;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.core.messages.TOMMessageType;
import bftsmart.tom.util.Logger;
import bftsmart.tom.util.TOMUtil;
import core.components.workerpool.DataBlockQueue;
import core.management.ByteArrayWrap;
import core.management.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.management.Lock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class PreReplicaExecutor implements ReplyListener, Runnable {

    private final PreReplicaReceive rcv;
//    private ServiceProxy proxy;
    private final AsynchServiceProxy proxy;
    private int[] processes;
    private CoreProperties properties;
    private final ArrayBlockingQueue inQueue;
    protected DataBlockQueue threadBlock;
    private final RemindTask task;
    private final Timer timer;
    private int numberofMessages = 0;
    protected ByteBuffer serliazedBuf = ByteBuffer.allocate(2500);
    protected ByteBuffer deserliazedBuf = ByteBuffer.allocate(2500);
    protected IPList ips;
    protected Lock lock;
    private final HashMap<Integer, ArrayBlockingQueue> outQueues;
    private final HashMap<Integer, PreReplicaSend> senders;
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 2500).order(ByteOrder.BIG_ENDIAN);

    private final HashMap<Integer, byte[]> memory;

    PreReplicaExecutor(int ID) {
//        this.proxy = new ServiceProxy(ID);
        this.proxy = new AsynchServiceProxy(ID);
        this.ips = new IPList();
        this.lock = new Lock();
        lock.lock();
//       int totalSize=Message.HEADER_SIZE + CoreProperties.message_size + CoreProperties.signature_key_size + CoreProperties.hmac_key_size * 4;
        int totalSize = Message.HEADER_SIZE + CoreProperties.message_size + CoreProperties.signature_key_size + CoreProperties.hmac_key_size;
        this.threadBlock = new DataBlockQueue(CoreProperties.messageRate * 2, totalSize);
        this.inQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.rcv = new PreReplicaReceive(properties.listen_port, ips, lock, inQueue, threadBlock);
        new Thread(rcv).start();
        this.outQueues = new HashMap<>();
        this.senders = new HashMap<>();
        task = new RemindTask();
        timer = new Timer();
        timer.schedule(task, 0, 1000); //delay in milliseconds
        this.memory = new HashMap<Integer, byte[]>();
        this.proxy.setReplyListener(this);
    }

//    @Override
//    public void replyReceived(TOMMessage reply) {
//        byte[] resp = reply.getContent();
//        Message m = new Message().deserialize(resp, deserliazedBuf);
//        if (outQueues.containsKey(m.getSrc())) {
//            outQueues.get(m.getSrc()).add(resp);
//        } else {
//            ArrayBlockingQueue out = new ArrayBlockingQueue(CoreProperties.queue_size);
//            outQueues.put(m.getSrc(), out);
//            PreReplicaSend snd = new PreReplicaSend(ips, lock, properties.destiny_port, out);
//            senders.put(m.getSrc(), snd);
//            new Thread(snd).start();
//        }
//    }
    @Override
    public void run() {
        try {
            processes = proxy.getViewManager().getCurrentViewProcesses();
            while (true) {
                ByteArrayWrap dataWrap = null;
                try {
                    dataWrap = (ByteArrayWrap) inQueue.take();
                    new ByteArrayWrap().serialize(serliazedBuf, dataWrap.getArr(), dataWrap.getSize());
//                    System.out.println("Sending="+ new Message().deserialize(dataWrap.getArr(), deserliazedBuf).getData().length);
//                    proxy.invokeAsynchronous(dataWrap.getArr(), this, processes);

                    int ID = proxy.invokeAsynchRequest(dataWrap.getArr(), processes, this, TOMMessageType.ORDERED_REQUEST);
                    proxy.cleanAsynchRequest(new Message().deserialize(dataWrap.getArr(), deserialized).getSeqNumber());
//                    memory.put(ID, dataWrap.getArr());
                    numberofMessages++;
                    threadBlock.offer(dataWrap.getArr());
                } catch (ClassCastException ex1) {
                    CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex1.getMessage());
                }
//                byte[] data = new byte[dataWrap.getSize()];
//                System.arraycopy(dataWrap.getArr(), 0, data, 0, dataWrap.getSize());
//                if (new Message().deserialize(data,deserialized).getType() == Message.CHG_PREREPLICA_REQUEST) {
//                    System.out.println("**** CHANGE REPLICA REQUEST!!!");
//                    CoreConfiguration.pause(5);
//                }
//                data = malicious.corrupt(data);
//               numberofMessages++;
            }
        } catch (InterruptedException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
        }

    }

//    @Override
//    public void replyReceived(TOMMessage reply) {
//             byte[] resp = reply.getContent();
//        Message m = new Message().deserialize(resp, deserliazedBuf);
//        if (outQueues.containsKey(m.getSrc())) {
//            outQueues.get(m.getSrc()).add(resp);
//        } else {
//            ArrayBlockingQueue out = new ArrayBlockingQueue(CoreProperties.queue_size);
//            outQueues.put(m.getSrc(), out);
//            PreReplicaSend snd = new PreReplicaSend(ips, lock, properties.destiny_port, out);
//            senders.put(m.getSrc(), snd);
//            new Thread(snd).start();
//        }
//    }
    private void reconfigureTo(View v) {
        Logger.println("Installing a most up-to-date view with id=" + v.getId());
        proxy.getViewManager().reconfigureTo(v);
        proxy.getViewManager().getViewStore().storeView(v);
        // = new TOMMessage[proxy.getViewManager().getCurrentViewN()];
        proxy.getCommunicationSystem().updateConnections();
    }
    int invalidId = -1;

    @Override
    public void replyReceived(RequestContext context, TOMMessage reply) {
        byte[] resp = reply.getContent();
//        if (reply.getId() == invalidId) {
//            return;
//        }
//
//        if (proxy.getViewManager().getCurrentViewId() != reply.getViewID()) {
//            reconfigureTo((View) TOMUtil.getObject(reply.getContent()));
//            proxy.invoke(memory.get(reply.getId()), TOMMessageType.ORDERED_REQUEST);
//            return;
//
//        }

        if (resp != null) {
            Message m = new Message().deserialize(resp, deserliazedBuf);
            if (outQueues.containsKey(m.getSrc())) {
                outQueues.get(m.getSrc()).add(resp);
            } else {
                ArrayBlockingQueue out = new ArrayBlockingQueue(CoreProperties.queue_size);
                outQueues.put(m.getSrc(), out);
                PreReplicaSend snd = new PreReplicaSend(ips, lock, properties.destiny_port, out);
                senders.put(m.getSrc(), snd);
                new Thread(snd).start();
            }
        }
    }

    class RemindTask extends TimerTask {

        RemindTask() {
        }

        @Override
        public void run() {
            System.out.println("Number of messages=" + numberofMessages);
            numberofMessages = 0;
        }
    }

}
