/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import bftsmart.reconfiguration.ServerViewManager;
import bftsmart.tom.core.messages.TOMMessage;
import core.management.RouteTable;
import core.message.Message;
import core.management.CoreConfiguration;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class Executor implements Runnable {

    private ArrayBlockingQueue in, out;
    private RouteTable route;
    private TreeMap<Integer, Integer> connected;
    private ServerViewManager SVM;
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);

    public Executor(ArrayBlockingQueue in, ArrayBlockingQueue out, RouteTable route, TreeMap<Integer, Integer> connected, ServerViewManager SVM) {
        this.in = in;
        this.out = out;
        this.route = route;
        this.connected = connected;
        this.SVM = SVM;
    }  
    
    @Override
    public void run() {
        while (true) {
            try {
                TOMMessage request = (TOMMessage) in.take();
                byte[] response = request.getContent();
                Message msg = new Message().deserialize(response, deserialized);
                switch (msg.getType()) {
                    case Message.SEND_REQUEST:
                        break;
                    case Message.ACK:
                        break;
                    case Message.HELLO:
                        connected.put(msg.getSrc(), msg.getSeqNumber());
                        CoreConfiguration.print("Connected=" + Arrays.toString(connected.keySet().toArray()));
                        break;
                    case Message.CONTROLLER_HELLO:
                        CoreConfiguration.print("Controller hello received!");
                        route.addRoute(CoreConfiguration.ID, msg.getSrc());
                        break;
                    case Message.ADD_ROUTE:
                        int[] dst = bytesToInt(msg.getData());
                        CoreConfiguration.print("Add route from=" + msg.getSrc() + " to=" + dst[0]);
                        route.addRoute(msg.getSrc(), dst[0]);
                        route.addRoute(dst[0], msg.getSrc());
                        break;
                    case Message.END_REQUEST:
                        CoreConfiguration.print("End request from=" + msg.getSrc());
                        break;
                    case Message.CHG_PREREPLICA:
                        break;
                    case Message.CHG_PREREPLICA_REQUEST:
                        CoreConfiguration.print("* CHG PRE REPLICA request " + msg.getSrc() + " is under attack!");
                        break;
                    case Message.WARMUP_END:
                        CoreConfiguration.print("Warmup end (>)");
                        break;
                    case Message.WARMUP_END_ACK:
                        CoreConfiguration.print("Warmup end ack (<)");
                        break;
                    case Message.RMV_PREREPLICA:
                        CoreConfiguration.print("* RMV PREPLICA request " + msg.getSrc() + " is under attack!");
                        String toRMV = new String(msg.getData());
                        route.removeSource(toRMV);
                        route.removeDestination(Integer.toString(msg.getSrc()), toRMV);
                        break;
                    case Message.ADD_PREREPLICA:
                        String[] addr = new String(msg.getData()).split(":");
                        int dst2 = Integer.parseInt(addr[1]);
                        route.addRoute(msg.getSrc(), dst2);
                        route.addRoute(dst2, msg.getSrc());
                        connected.put(dst2, 0);
                        connected.remove(Integer.parseInt(addr[0]));
                        CoreConfiguration.print("* ADD PREPLICA request " + addr[0] + " is under attack!");
                        break;
                    case Message.END_ACK:
                        CoreConfiguration.print("SRC: " + msg.getSrc() + " dst: " + Arrays.toString(route.getDestinationArray(msg.getSrc())));
                        break;
                    default:
                        CoreConfiguration.print("case default=" + msg);
                        Message m = new Message(Message.DISCARD, msg.getSrc(), msg.getSeqNumber(), new byte[]{-1, -1, -1});
                        request.reply = new TOMMessage(CoreConfiguration.ID, request.getSession(), request.getSequence(), m.serialize(serialized1), SVM.getCurrentViewId());
                }
                request.reply = new TOMMessage(CoreConfiguration.ID, request.getSession(), request.getSequence(), response, SVM.getCurrentViewId());
                out.put(request);
            } catch (Exception ex) {
                ex.printStackTrace();
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }

    }

    protected int[] bytesToInt(byte[] dst) {
        IntBuffer intBuf = ByteBuffer.wrap(dst).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }

}
