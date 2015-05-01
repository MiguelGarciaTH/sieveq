/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

import com.google.common.util.concurrent.RateLimiter;
import core.components.workerpool.ThreadBlockQueue;
import core.components.workerpool.WorkerPool;
import core.management.ServerSession;
import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.experiments.Experiment;
import core.modules.malicious.Malicious;
import core.modules.voter.SimpleVoter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public abstract class ClientExecutor implements Runnable {

    protected int ID;
    protected SimpleVoter voter;
    protected long timeInit;
    protected boolean lock, lock2;
    protected ConcurrentHashMap<Integer, ServerSession> sessions;
    protected int destination;
    protected CoreProperties prop;
    protected Experiment experiment;
    protected Long[] sent;
    protected long initTime, finishTime;
    private final int total;
    private double history_percentage = 0;
    protected Malicious malicious;
    protected WorkerPool pool;
    protected ArrayBlockingQueue<Message> in;
    protected LinkedBlockingQueue<Message> inAux;
    protected LinkedBlockingQueue inQueue;
    protected ArrayBlockingQueue<byte[]> out;
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected ThreadBlockQueue threadQueue;
    protected int[] senSeq;
    int msg_len;

//    private OGEvemtGenerator generator;

    ClientExecutor(int ID, int dst) {
        this.ID = ID;
        this.destination = dst;
        this.sessions = new ConcurrentHashMap<>();
        this.voter = new SimpleVoter(prop.num_replicas, prop.quorom);
        this.sessions.put(ID, new ServerSession(ID, ID, new int[]{0, 0, 0, 0}, dst, true));
        this.sessions.put(dst, new ServerSession(ID, ID, new int[]{0, 0, 0, 0}, dst, true));
        this.sessions.put(0, new ServerSession(ID, ID, new int[]{0, 0, 0, 0}, dst, true));
        this.lock = true;
        this.lock2 = true;
        this.total = CoreProperties.messageRate * CoreProperties.experiment_rounds + CoreProperties.warmup_rounds * CoreProperties.messageRate + 10;
        this.in = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.inAux = new LinkedBlockingQueue<>();
        this.inQueue = new LinkedBlockingQueue<>();
        //Conf 2!!!        
//        int size = Message.HEADER_SIZE + prop.message_size + (prop.hmac_key_size*4);
//        msg_len = Message.getHeaderSize() + (prop.hmac_key_size * 4);

        //Conf 1!!!
        int size = Message.HEADER_SIZE + prop.message_size + prop.signature_key_size + prop.hmac_key_size;
        msg_len = Message.getHeaderSize() + prop.hmac_key_size + prop.signature_key_size;

        this.threadQueue = new ThreadBlockQueue(prop.messageRate * prop.experiment_rounds + 10, size + 4);
        this.out = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.pool = new WorkerPool(in, out, threadQueue, "crypto", CoreProperties.num_workers);
        this.sent = new Long[total + 5];
        this.senSeq = new int[sent.length];
        this.experiment = new Experiment(CoreProperties.experiment_type, senSeq, sent, inQueue);
//        this.generator = new OGEvemtGenerator();
//        this.generator.loadEventFile("./config/maxi-test-log.log");
//        this.generator.getStatistics();
    }

    abstract void send(byte[] cmd);

    private void send(int type, int sequenceNumber, byte[] cmd) {
        Message send = new Message(type, ID, sequenceNumber, cmd);
        in.add(send);
        inQueue.add(send);
        senSeq[sequenceNumber] = sequenceNumber;
        sent[sequenceNumber] = System.nanoTime();
//        inAux.add(send);
    }

    @Override
    public void run() {
        try {
            int payloadSize = prop.message_size > msg_len ? (prop.message_size - msg_len) : prop.message_size;
            byte[] cmd = new byte[payloadSize];
            ByteBuffer dst = ByteBuffer.allocate(4);
            new Random().nextBytes(cmd);
            int seq = sessions.get(0).incrementeOutSequenceNumber();
            new Thread(experiment).start();
            send(Message.HELLO, seq, cmd);
            CoreConfiguration.print("HELLO SENT");
            while (lock) {
                CoreConfiguration.pause(1);
                System.out.print(". ");
            }
            dst.putInt(destination);
            send(Message.ADD_ROUTE, sessions.get(0).incrementeOutSequenceNumber(), dst.array());
            CoreConfiguration.pause(3);
            seq = sessions.get(destination).incrementeOutSequenceNumber();
            RateLimiter rateLimiter = RateLimiter.create(CoreProperties.messageRate);
            while (true) {
                rateLimiter.acquire();
                //cmd = generator.getRandomEvent().getBytes();
                send(Message.SEND_REQUEST, seq++, cmd);
                if (seq == (CoreProperties.warmup_rounds * CoreProperties.messageRate + CoreProperties.experiment_rounds * CoreProperties.messageRate)) {
                    break;
                }
            }
            send(Message.END_REQUEST, seq++, cmd);
            System.out.println("\t\t******Client Executor Finished*******");
//            generator.getStatistics();
        } catch (Exception ex) {
            Logger.getLogger(ClientExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected int[] bytesToInt(byte[] dst) {
        IntBuffer intBuf = ByteBuffer.wrap(dst).asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);
        return array;
    }

    protected void addClients(int[] cli) {
        for (int i : cli) {
            if (i != ID) {
                if (!sessions.containsKey(i)) {
                    sessions.put(i, new ServerSession(ID, 0, new int[]{0, 0, 0, 0}, i, true));
                }
            }
        }
    }

    private void percentagePrint(int rounds) {
        int remaining = total - (rounds * prop.messageRate);
        double percent = 100 - ((remaining * 100) / total);
        if (percent % 10 == 0 && percent > history_percentage) {
            history_percentage = percent;
            System.out.print(percent + "%\t");
        }
    }
}
