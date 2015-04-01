/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.ServiceProxy;
import core.management.ServerSession;
import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.misc.Lock;
import core.modules.experiments.LatencyExperiments;
import core.modules.experiments.ThroughputExperiments;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
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

    private RemindTask task;

    private Timer timer;
    private BlockingQueue inQueue;
    private ConcurrentHashMap<Integer, ServerSession> sessions;
    private ServiceProxy proxy;
    private Lock lock;
    private int ID;
    private ReplyListener reply;
    private boolean primaryBackup;
    private ArrayBlockingQueue experimentQueue;
    private ThroughputExperiments throughput;
    private boolean warmup = true;
    private LatencyExperiments latency;

    private int numberofMessages = 0;
    protected ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    private int total = CoreProperties.messageRate * CoreProperties.experiment_rounds + CoreProperties.messageRate * CoreProperties.warmup_rounds;

    public ServerReplyManager(ServerExecutor aThis, int ID, BlockingQueue thirdQueue, ConcurrentHashMap<Integer, ServerSession> sessions, ServiceProxy proxy, Lock lock, boolean primaryBackup) {
        this.ID = ID;
        this.primaryBackup = primaryBackup;
        this.inQueue = thirdQueue;
        this.sessions = sessions;
        this.lock = lock;
        this.proxy = proxy;
        this.reply = aThis;

        this.experimentQueue = new ArrayBlockingQueue(total + 5);
//        this.throughput = new ThroughputExperiments(true, experimentQueue, CoreProperties.throughput_experiments_file);
        this.latency = new LatencyExperiments(null, false, null, experimentQueue, null, CoreProperties.latency_experiments_file);

        task = new RemindTask();
        timer = new Timer();

    }

    @Override

    public void run() {
        int[] processes = proxy.getViewManager().getCurrentViewProcesses();
        Message resp;
        boolean one = false;
        timer.schedule(task, 0, 1000); //delay in milliseconds
        new Thread(latency).start();
//      new Thread(throughput).start();
        while (true) {
            try {
                resp = (Message) inQueue.take();
                switch (resp.getType()) {
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
                    case Message.WARMUP_END:
                        System.out.println("WARMUP END!");
//                        experimentQueue.add(resp);
//                        warmup = false;
//                        inQueue.clear();
//                        resp = new Message(Message.WARMUP_END_ACK, ID, sessions.get(resp.getSrc()).incrementeOutSequenceNumber(), new byte[]{0});
//                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);

                        break;
                    case Message.SEND_REQUEST:
//                        if (!warmup) {
                        experimentQueue.add(resp);
                        numberofMessages++;
//                        }
//                        if (resp.getSeqNumber() % CoreProperties.ACK_RATE == 0) {
//                            String ack = resp.getSeqNumber() + "";
//                            Message resp2 = new Message(Message.ACK, ID, sessions.get(resp.getSrc()).incrementeOutSequenceNumber(), ack.getBytes());
//                            proxy.invokeAsynchronous(resp2.serialize(serialized1), reply, processes);
//                        }
                        break;

                    case Message.CHG_PREREPLICA_REQUEST:
                        resp = new Message(Message.ADD_PREREPLICA, ID, 0, new String("7:8").getBytes());
                        CoreConfiguration.print("Add pre-replica [simulation]");
                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);
                        resp = new Message(Message.CHG_PREREPLICA, ID, sessions.get(7).incrementeOutSequenceNumber(), "192.168.3.27:6504:8".getBytes());
                        CoreConfiguration.print("Change pre-replica needed [simulation]");
                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);
                        break;

                    case Message.CHG_REPLICA_REQUEST:
                        resp = new Message(Message.ADD_REPLICA, ID, 0, new String("2:2").getBytes());
                        CoreConfiguration.print("Add replica [simulation]");
                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);
//                        resp = new Message(Message.CHG_REPLICA, ID, sessions.get(7).incrementeOutSequenceNumber(), "192.168.3.27:6504:8".getBytes());
//                        CoreConfiguration.print("Change replica needed [simulation]");
//                        proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);
                        break;
                    case Message.END_REQUEST:
                        System.out.println("END REQUEST");
                        if (!one) {
                            experimentQueue.add(resp);
                            one = true;
                            CoreConfiguration.print("End ack to=" + resp.getSrc());
                            resp = new Message(Message.END_ACK, ID, sessions.get(resp.getSrc()).incrementeOutSequenceNumber(), new byte[]{0});
                            if (primaryBackup) {
                                proxy.invokeAsynchronousLeader(resp.serialize(serialized1), reply, new int[]{processes[0]});
                            } else {
                                proxy.invokeAsynchronous(resp.serialize(serialized1), reply, processes);
                            }
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

    private boolean addClients(int[] cli) {
        for (int i : cli) {
            if (!sessions.containsKey(i)) {
                sessions.put(i, new ServerSession(i, 0, new int[]{0, 0, 0, 0}, ID, true));
                CoreConfiguration.print("adding client= " + sessions.get(i));
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
                i++;
                if (i == 30) {
                    timer.cancel();
                }
            }
            System.out.println("Messages delivered=" + numberofMessages);
            numberofMessages = 0;
        }
    }
}
