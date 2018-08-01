/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import bftsmart.communication.ServerCommunicationSystem;
import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.server.Replier;
import core.management.RouteTable;
import core.management.Message;
import core.management.CoreConfiguration;
import static core.management.CoreConfiguration.ID;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class ReplicaReplier implements Runnable, Replier {

    private final ArrayBlockingQueue out;
    private final ServerCommunicationSystem comm;
//    private MessageHandler handler;
    private final TreeMap<Integer, Integer> connected;
    private RouteTable route;
    private final ServiceReplica replica;
    private ReplicaContext replicaContext;

    private int counter = 0;

    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 2500).order(ByteOrder.BIG_ENDIAN);
    protected ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 2500).order(ByteOrder.BIG_ENDIAN);
    protected boolean attackMode;
    private int attackedCounter;

    private Timer t;
    private Counter task;
//    private ServerViewManager svm;
    private Integer server = null;

    public ReplicaReplier(ArrayBlockingQueue out, TreeMap<Integer, Integer> connected, int sharedID, RouteTable route, ServiceReplica replica) {
        this.out = out;
        this.connected = connected;
        this.route = route;
        this.replica = replica;
        this.comm = replica.getReplicaContext().getServerCommunicationSystem();
//        this.handler = comm.getMessageHander();
        this.replica.setReplyController(this);
//        this.svm = replica.getSVManager();
//        this.task = new Counter();

        this.t = new Timer();
        task = new Counter();
        t.schedule(task, 0, 1000); //delay in milliseconds
    }

    public void setRoute(RouteTable route) {
        this.route = route;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TOMMessage request = (TOMMessage) out.take();
                manageReply(request, null);
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }
    }

    @Override
    public void manageReply(TOMMessage request, MessageContext msgCtx) {
        if (request.reply.getContent() != null) {
            Message rcv = new Message().deserialize(request.reply.getContent(), deserialized);
            int[] dst;
            Message msg2;
            TOMMessage tomMsg2;
            switch (rcv.getType()) {
                case Message.SEND_REQUEST:
                    counter++;
//                    System.out.println("Here = " + rcv);
                    dst = route.getDestinationArray(rcv.getSrc());
                    request.reply.setContent(request.reply.getContent());
                    comm.send(dst, request.reply);
                    break;
                case Message.ACK:
                    dst = route.getDestinationArray(rcv.getSrc());
                    comm.send(dst, request.reply);
                    break;
                case Message.CONTROLLER_HELLO:
                    msg2 = new Message(Message.HELLO_ACK, rcv.getSrc(), rcv.getSeqNumber(), new byte[]{1});
                    tomMsg2 = new TOMMessage(CoreConfiguration.ID, request.getSession(), request.getSequence(), msg2.serialize(serialized1), request.getViewID(), request.getReqType());
                    comm.send(new int[]{rcv.getSrc()}, tomMsg2);
                    break;
                case Message.HELLO:
                    msg2 = new Message(Message.HELLO_ACK, rcv.getSrc(), rcv.getSeqNumber(), new byte[]{1});
                    tomMsg2 = new TOMMessage(CoreConfiguration.ID, request.getSession(), request.getSequence(), msg2.serialize(serialized1), request.getViewID(), request.getReqType());
                    comm.send(new int[]{request.getSender()}, tomMsg2);
                    CoreConfiguration.print("Sent to = " + request.getSender() + " of " + msg2.getSrc());
                    if (connected.size() > 1) {
                        int[] dst2 = getDst();
                        CoreConfiguration.print("src=" + rcv.getSrc() + " dst=" + dst2[0]);
                        route.addDestinyRoute(dst2[0], rcv.getSrc());
                        route.addRoute(rcv.getSrc(), dst2[0]);
                        Message msg = new Message(Message.CONNECT, rcv.getSrc(), rcv.getSeqNumber(), intTobytes(dst2));
                        TOMMessage tomMsg = new TOMMessage(CoreConfiguration.ID, request.getSession(), request.getSequence(), msg.serialize(serialized1), request.getViewID(), request.getReqType());
                        comm.send(new int[]{request.getSender()}, tomMsg);
//                    CoreConfiguration.print("Sending connected list to=" + Arrays.toString(dst2));
                    } else {
                        server = rcv.getSrc();
                    }
                    break;
                case Message.DISCARD:
                    CoreConfiguration.print("message discarded >" + rcv);
                    break;
                case Message.CHG_PREREPLICA_REQUEST:
                    dst = route.getDestinationArray(rcv.getSrc());
                    System.out.println("HERE...." + "dst= " + Arrays.toString(dst));
                    comm.send(dst, request.reply);
                    break;
                case Message.WARMUP_END:
                    dst = route.getDestinationArray(rcv.getSrc());
                    comm.send(dst, request.reply);
                    break;
                case Message.WARMUP_END_ACK:
                    dst = route.getDestinationArray(rcv.getSrc());
                    comm.send(dst, request.reply);
                    break;
                case Message.END_REQUEST:
                    dst = route.getDestinationArray(rcv.getSrc());
                    comm.send(dst, request.reply);
                    break;
                case Message.CHG_PREREPLICA:
                    CoreConfiguration.print("Changing ... CHG_PREREPLICA modify state here later!");
                    dst = route.getDestinationArray(rcv.getSrc());
                    CoreConfiguration.print(" CHG_PREREPLICA > DST=> " + Arrays.toString(dst));
                    comm.send(dst, request.reply);
                    CoreConfiguration.print(" to client chg preplica > DST=> " + Arrays.toString(dst));
                    break;
                case Message.ADD_REPLICA:
                    CoreConfiguration.print("Changing ... replicas modify state here later!");
                    dst = route.getDestinationArray(rcv.getSrc());
                    CoreConfiguration.print(" CHG_REPLICA > DST=> " + Arrays.toString(dst));
                    comm.send(dst, request.reply);
                    break;
                case Message.RMV_PREREPLICA:
                    CoreConfiguration.print("Removing pre replica to Controller!");
                    dst = route.getDestinationArray(ID);
                    comm.send(dst, request.reply);
                    break;
                case Message.ADD_PREREPLICA:
                    CoreConfiguration.print("Adding pre replica to Controller!");
                    dst = route.getDestinationArray(ID);
                    CoreConfiguration.print("Connected=> " + connected.toString());
                    comm.send(dst, request.reply);
                    break;
                case Message.END_ACK:
                    dst = route.getDestinationArray(rcv.getSrc());
                    comm.send(dst, request.reply);
                    break;
                default:
                    attackedCounter++;
                    if (attackedCounter == 4) {
                        CoreConfiguration.print("Changing ... replicas modify state here later!");
                        dst = route.getDestinationArray(rcv.getSrc());
                        CoreConfiguration.print("CHG_REPLICA > DST=> " + Arrays.toString(dst));
                        comm.send(dst, request.reply);
                    }
                    CoreConfiguration.print("Nothing to send: type received=" + rcv.type() + " from=" + rcv.getSrc());
            }
        }

    }

    private int[] getDst() {
        int i = 0;
        int[] dst = new int[connected.size()];
        for (Integer t : connected.keySet()) {
            dst[i++] = t;
        }
        return dst;
    }

    protected byte[] intTobytes(int[] dst) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(dst.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(dst);
        return byteBuffer.array();
    }

    @Override
    public void setReplicaContext(ReplicaContext rc) {
        this.replicaContext = replicaContext;
    }

    class Counter extends TimerTask {

        int i = 0;
        int[] dst;
        int seq = 0;

        Counter() {
        }

        @Override
        public void run() {
            CoreConfiguration.print("Counter=" + counter);
            counter = 0;
//            int counterValue = handler.getCounter();
//            dst = new int[]{6};
//            int[] counter = new int[]{counterValue};
//            Message msg = new Message(Message.COUNTER, ID, seq++, intTobytes(counter));
//            TOMMessage tomMsg = new TOMMessage(CoreConfiguration.ID, 0, 0, msg.serialize(serialized1), svm.getCurrentViewId(), TOMMessageType.ORDERED_REQUEST);
//            comm.send(dst, tomMsg);
        }
    }

}
