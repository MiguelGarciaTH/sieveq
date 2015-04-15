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
import core.message.Message;
import core.management.CoreConfiguration;
import static core.management.CoreConfiguration.ID;
import core.modules.malicious.Malicious;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class ExecutorReplierOne implements Runnable, Replier {

    private ArrayBlockingQueue out;
    private ServerCommunicationSystem comm;
    private TreeMap<Integer, Integer> connected;
    private int sharedID;
    private RouteTable route;
    private ServiceReplica replica;
    private ReplicaContext replicaContext;

    private RemindTask task;
    private int numberofMessages = 0;
    private Timer timer;
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected boolean attackMode;
    protected Malicious malicious;
    private int attackedCounter;

    public ExecutorReplierOne(ArrayBlockingQueue out, TreeMap<Integer, Integer> connected, int sharedID, RouteTable route, ServiceReplica replica, Malicious malicious) {
        this.out = out;
        this.connected = connected;
        this.sharedID = sharedID;
        this.route = route;
        this.replica = replica;
        this.comm = replica.getReplicaContext().getServerCommunicationSystem();
        this.replica.setReplyController(this);
    
//        task = new RemindTask();
//        timer = new Timer();
//        timer.schedule(task, 0, 1000); //delay in milliseconds
    }

    public void setAttackMode() {
        System.out.println("EM MODO ATAQUE");
//        attackMode = true;
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(ExecutorReplierOne.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                TOMMessage request = (TOMMessage) out.take();
                manageReply(request, null);
            } catch (InterruptedException ex) {
//                ex.printStackTrace();
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }
    }

    @Override
    public void manageReply(TOMMessage request, MessageContext msgCtx) {
        Message rcv = new Message().deserialize(request.reply.getContent(), deserialized);
        int[] dst;
        Message msg2;
        TOMMessage tomMsg2;

//        numberofMessages++;
//        if (ID==1) {
//            
//            System.out.println("*");
////            rcv.setType(Message.DISCARD);
////            System.out.println("Always discarding!! ");
////            try {
////                Thread.sleep(2000);
////            } catch (InterruptedException ex) {
////                Logger.getLogger(ExecutorReplierOne.class.getName()).log(Level.SEVERE, null, ex);
////            }
//        }
        switch (rcv.getType()) {
            case Message.SEND_REQUEST:
                dst = route.getDestinationArray(rcv.getSrc());
                request.reply.setConent(request.reply.getContent());
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
                comm.send(new int[]{rcv.getSrc()}, tomMsg2);
                if (connected.size() > 1) {
                    int[] dst2 = getDst();
                    Message msg = new Message(Message.CONNECT, rcv.getSrc(), rcv.getSeqNumber(), intTobytes(dst2));
                    TOMMessage tomMsg = new TOMMessage(CoreConfiguration.ID, request.getSession(), request.getSequence(), msg.serialize(serialized1), request.getViewID(), request.getReqType());
                    comm.send(dst2, tomMsg);
                    CoreConfiguration.print("Sending connected list to=" + Arrays.toString(dst2));
                }
                break;
            case Message.DISCARD:
                CoreConfiguration.print("message discarded >" + rcv);
                break;
            case Message.CHG_PREREPLICA_REQUEST:
                dst = route.getDestinationArray(rcv.getSrc());
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
                CoreConfiguration.print("Changing ... replicas modify state here later!");
                dst = route.getDestinationArray(rcv.getSrc());
                CoreConfiguration.print(" CHG_PREREPLICA > DST=> " + Arrays.toString(dst));
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
                CoreConfiguration.print(" ADD RPREREPLICA enviar DST=> " + Arrays.toString(dst));
                CoreConfiguration.print("Connected=> " + connected.toString());
                comm.send(dst, request.reply);
                break;
            case Message.END_ACK:
                dst = route.getDestinationArray(rcv.getSrc());
                comm.send(dst, request.reply);
                break;
            case Message.ADD_ROUTE:
                dst = route.getDestinationArray(rcv.getSrc());
                CoreConfiguration.print("new route=" + rcv.getSrc() + " > " + Arrays.toString(dst));
                break;
            default:
                attackedCounter++;
                if (attackedCounter == 4) {
                    CoreConfiguration.print("Changing ... replicas modify state here later!");
                    dst = route.getDestinationArray(rcv.getSrc());
                    CoreConfiguration.print(" CHG_REPLICA > DST=> " + Arrays.toString(dst));
                    comm.send(dst, request.reply);
                }
                CoreConfiguration.print("Nothing to send: type received=" + rcv.type() + " from=" + rcv.getSrc());
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

    class RemindTask extends TimerTask {

        int i = 0;

        RemindTask() {
        }

        @Override
        public void run() {
//            if (i < 3) {
//                if (numberofMessages == 0) {
//                    i++;
//                }
            System.out.println("Messages delivered=" + numberofMessages);
            numberofMessages = 0;
//            } else {
//                timer.cancel();
//            }
        }

    }

}
