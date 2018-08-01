/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.AsynchServiceProxy;

import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessageType;
import core.components.controller.ControllerOpreatorProcesses;
import core.management.ServerSession;
import core.management.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.management.Lock;
import core.modules.experiments.Experiment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * I
 *
 * @author miguel
 */
public class ServerReplyManager implements Runnable {

    private final RemindTask task;
    private final Timer timer;
    private final BlockingQueue inQueue;
    private final ConcurrentHashMap<Integer, ServerSession> sessions;
//    private final ServiceProxy proxy;
    private final AsynchServiceProxy proxy;
    private final Lock lock;
    private final int ID;
    private final ReplyListener reply;
    private final ArrayBlockingQueue experimentQueue;
    private final Experiment experiment;
    private int numberofMessages = 0;
    protected final ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    private final int total = CoreProperties.messageRate * CoreProperties.experiment_rounds + CoreProperties.messageRate * CoreProperties.warmup_rounds;
    private int current = 0;
    private final int[] arrivalCounter;
    private final HashMap<Integer, int[]> monit;
    private final int[] podium;
    private final ControllerOpreatorProcesses operator;

//    public ServerReplyManager(ServerExecutor aThis, int ID, BlockingQueue thirdQueue, ConcurrentHashMap<Integer, ServerSession> sessions, ServiceProxy proxy, Lock lock, boolean primaryBackup, int[] arrivalCounter) {
    public ServerReplyManager(ServerExecutor aThis, int ID, BlockingQueue thirdQueue, ConcurrentHashMap<Integer, ServerSession> sessions, AsynchServiceProxy proxy, Lock lock, boolean primaryBackup, int[] arrivalCounter) {
        this.ID = ID;
        this.inQueue = thirdQueue;
        this.sessions = sessions;
        this.lock = lock;
        this.proxy = proxy;
        this.reply = aThis;
        this.experimentQueue = new ArrayBlockingQueue(total * 8);
        this.experiment = new Experiment(CoreProperties.experiment_type, null, null, experimentQueue);
        this.task = new RemindTask();
        this.timer = new Timer();
        this.monit = new HashMap<>();
        this.arrivalCounter = arrivalCounter;
        this.podium = new int[4];
        this.operator = new ControllerOpreatorProcesses();
    }

    private void monit(int id, int seq, int counter) {
        if (monit.containsKey(seq)) {
            int[] set = monit.get(seq);
            set[id] = counter;
            monit.put(seq, set);
        } else {
            monit.put(seq, new int[4]);
            int[] set = monit.get(seq);
            set[id] = counter;
            monit.put(seq, set);
        }
        current = seq;
    }

    @Override
    public void run() {
        int[] processes = proxy.getViewManager().getCurrentViewProcesses();
        Message resp;
        boolean one = false;
        timer.schedule(task, 0, 1000); //delay in milliseconds
        new Thread(experiment).start();
        while (true) {
            try {
                resp = (Message) inQueue.take();
                switch (resp.getType()) {
                    case Message.COUNTER:
                        monit(resp.getSrc(), resp.getSeqNumber(), byteArrayToInt(resp.getData()));
                        break;
                    case Message.HELLO_ACK:
                        CoreConfiguration.print("Hello ack received");
                        break;
                    case Message.CONNECT:
                        byte[] data = resp.getData();
                        int[] cli = bytesToInt(data);
                        this.lock.unlock();
                        if (addClients(cli)) {
                            CoreConfiguration.print("Other clients connected=" + Arrays.toString(cli));
                            CoreConfiguration.print("Server unlock");
                        }
                        break;
                    case Message.SEND_REQUEST:
                        experimentQueue.add(resp);
                        numberofMessages++;
//                        if (resp.getSeqNumber() % CoreProperties.ACK_RATE == 0) {
//                            String ack = resp.getSeqNumber() + "";
//                            Message resp2 = new Message(Message.ACK, ID, sessions.get(resp.getSrc()).incrementeOutSequenceNumber(), ack.getBytes());
//                            proxy.invokeAsynchronous(resp2.serialize(serialized1), reply, processes);
//                        }
                        break;
                    case Message.CHG_PREREPLICA_REQUEST:
                        resp = new Message(Message.ADD_PREREPLICA, ID, 0, new String("7:8").getBytes());
                        CoreConfiguration.print("Add pre-replica [simulation]");
//                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);

                        proxy.invokeAsynchRequest(resp.serialize(serialized1), processes, reply, TOMMessageType.UNORDERED_REQUEST);
                        resp = new Message(Message.CHG_PREREPLICA, ID, sessions.get(7).incrementeOutSequenceNumber(), "192.168.3.25:6502:8".getBytes());
                        proxy.cleanAsynchRequest(resp.getSeqNumber());
                        CoreConfiguration.print("Change pre-replica needed [simulation]");
                        proxy.invokeAsynchRequest(resp.serialize(serialized1), processes, reply, TOMMessageType.UNORDERED_REQUEST);
                        proxy.cleanAsynchRequest(resp.getSeqNumber());
//                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);

                        break;
                    case Message.CHG_REPLICA_REQUEST:
                        resp = new Message(Message.ADD_REPLICA, ID, 0, new String("2:2").getBytes());
                        CoreConfiguration.print("Add replica [simulation]");
                        proxy.invokeAsynchRequest(resp.serialize(serialized1), processes, reply, TOMMessageType.UNORDERED_REQUEST);
                        proxy.cleanAsynchRequest(resp.getSeqNumber());
//                        resp = new Message(Message.CHG_REPLICA, ID, sessions.get(7).incrementeOutSequenceNumber(), "192.168.3.27:6504:8".getBytes());
                        CoreConfiguration.print("Change replica needed [simulation]");
//                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);
                        break;
                    case Message.END_REQUEST:
                        System.out.println("END REQUEST");
                        if (!one) {
                            experimentQueue.add(resp);
                            one = true;
                            CoreConfiguration.print("End ack to=" + resp.getSrc());
                            resp = new Message(Message.END_ACK, ID, sessions.get(resp.getSrc()).incrementeOutSequenceNumber(), new byte[]{0});
//                            proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);
                            proxy.invokeAsynchRequest(resp.serialize(serialized1), processes, reply, TOMMessageType.UNORDERED_REQUEST);
                            CoreConfiguration.print("**FINISH**");
                            sessions.remove(resp.getSrc());
                        }
                        break;
                    default:
                        CoreConfiguration.print("Unknown type=" + resp);
                }

            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
            }
        }
    }

    private int[] bytesToInt(byte[] dst) {
        IntBuffer intBuf = ByteBuffer.wrap(dst).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF
                | (b[2] & 0xFF) << 8
                | (b[1] & 0xFF) << 16
                | (b[0] & 0xFF) << 24;
    }

    private boolean addClients(int[] cli) {
        for (int i : cli) {
            if (!sessions.containsKey(i)) {
                sessions.put(i, new ServerSession(i, 0, new int[]{0, 0, 0, 0}, ID, true));
//                CoreConfiguration.print("adding client= " + sessions.get(i));
                return true;
            }
        }
        return false;
    }

    class RemindTask extends TimerTask {

        int i = 0;

        RemindTask() {
        }

        @Override
        public void run() {
            if (numberofMessages == 0) {
                if (++i == 100) {
                    timer.cancel();
                }
            }
            System.out.println("Throughput= " + numberofMessages);
            numberofMessages = 0;
//            if (current > 0) {
//                int[] t = monit.get(current);
//                System.out.println("Seq=" + current + " counters=[ " + t[0] + "," + t[1] + ", " + t[2] + ", " + t[3] + " ]");
//                max(arrivalCounter, podium);
//                max(arrivalCounter, podium);
//                arrivalCounter[0] = 0;
//                arrivalCounter[1] = 0;
//                arrivalCounter[2] = 0;
//                arrivalCounter[3] = 0;
//                System.out.println("Faster=[ " + podium[0] + "," + podium[1] + ", " + podium[2] + ", " + podium[3] + " ]");
//                detector();
//            }
        }
    }
    boolean once = true;

    private void detector() {
        if (once) {
            if (current > 10) {
                int[] t = monit.get(current);
                if (t[2] > t[0] + (t[0] / 2)) {
                    System.out.println("KILLLING");
                    operator.createReplica(1);
                    once = false;
                }
            }
        }
    }

    double std_dev2(int a[], int n) {
        if (n == 0) {
            return 0.0;
        }
        double sum = 0;
        double sq_sum = 0;
        for (int i = 0; i < n; ++i) {
            sum += a[i];
            sq_sum += a[i] * a[i];
        }
        double mean = sum / n;
        double variance = sq_sum / n - mean * mean;
        return Math.sqrt(variance);
    }

    private void max(int[] arrivalCounter, int[] podium) {
        int max = -1;
        for (int i = 0; i < arrivalCounter.length; i++) {
            if (arrivalCounter[i] > max) {
                max = arrivalCounter[i];
                arrivalCounter[i] = 0;
                podium[i]++;
            }
        }
    }
}
