package core.modules.crypto;

import core.components.workerpool.ThreadBlockQueue;
import core.components.workerpool.WorkerPool;
import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.misc.Lock;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class tester {

    static CryptoSchemeFive crypto, crypto2;
    static CoreProperties prop, prop2;
    static CoreConfiguration conf, conf2;
    static ByteArrayOutputStream bos = new ByteArrayOutputStream();
    static Lock lock;

    public static void main(String[] args) throws Exception {
        lock = new Lock();
        prop = CoreProperties.getProperties("config/client.properties", "client");

        conf = CoreConfiguration.getConfiguration(7, "client");
        int size = Integer.parseInt(args[0]);
        byte[] data = new byte[size];//980, 480, 80 
        byte[] ser = new byte[size + Message.HEADER_SIZE + CoreProperties.signature_key_size + CoreProperties.hmac_key_size * 4];

        ByteBuffer bufSer = ByteBuffer.allocate(ser.length);
        Random r = new Random();

        int warmup = 50000;
        int rounds = 50000;
        ArrayBlockingQueue in = new ArrayBlockingQueue(warmup + rounds);
        ArrayBlockingQueue out = new ArrayBlockingQueue(warmup + rounds);

        ThreadBlockQueue threadQueue = new ThreadBlockQueue(warmup, 1200);
        WorkerPool testPool = new WorkerPool(in, out, threadQueue, "crypto2", Integer.parseInt(args[1]));
        r.nextBytes(data);
        //crypto = new CryptoSchemeFive();

//        Receiver rcv = new Receiver(out, time, warmup, lock);
//        Thread t = new Thread(rcv);
//        t.start();
        int i = 0;
        System.out.println("Data size=" + data.length);
        System.out.println("** warmup started...");
        Message m = new Message(Message.ADD_ROUTE, 1, i, data);
        long time = System.nanoTime();
        while (i < warmup) {
            in.put(m);
            //int s = m.serialize(ser, bufSer);
            // crypto.clientSecureMessage(ser, s);
            i++;
        }
        System.out.println("Queue full in=" + in.size());
        while (in.size() != 0);
        double total = (System.nanoTime() - time) / 1000000000.0;
        System.out.println("Queue empty in=" + in.size());
        System.out.println("** warmup ended...");
        System.out.println("Signatures=" + i);
        System.out.println("Total=" + total + " secs");
        System.out.println("Signatures per second=" + i / total + " Total");
        //reset
        //in.clear();
        out.clear();
        i = 0;
        time = System.nanoTime();
        int l = 0;

        while (i < rounds) {
            // int s = m.serialize(ser, bufSer);
            //crypto.clientSecureMessage(ser, s);
            in.put(m);
            i++;
        }
        System.out.println("Queue full in=" + in.size());
        System.out.println("waiting to finish...");
        while (in.size() != 0);
        total = (System.nanoTime() - time) / 1000000000.0;
        System.out.println("Queue empty in=" + in.size());
        System.out.println("Signatures=" + i);
        System.out.println("Total=" + total + " secs");
        System.out.println("Signatures per second=" + i / total + " Total");
        System.out.println("Rounds=" + rounds);
    }

    static class Receiver implements Runnable {

        ArrayBlockingQueue out;
        long time_init;
        int stop;
        Lock lock;

        public Receiver(ArrayBlockingQueue out, long time_init, int stop, Lock lock) {
            this.out = out;
            this.time_init = time_init;
            this.stop = stop;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                int i = 0;
                long time_search = System.nanoTime();
                long time_final;
                while (true) {
                    out.take();
                    i++;
                    if (i == stop) {
                        lock.unlock();
                        System.out.println("Unlock");
                        time_final = System.nanoTime();
                        break;
                    }
                }
                double total_time_search = (time_final - time_search) / 1000000000.0;
                double total = (time_final - time_init) / 1000000000.0;
                System.out.println("Total elapsed time=" + total + "secs");
//                System.out.println("Search elapsed time=" + total_time_search + "secs");
//                double signatures = total - total_time_search;
//                System.out.println("Signatures elapsed time=" + signatures + "secs");
                System.out.println("Total signatures=" + i);
                System.out.println("Signatures per second=" + i / total + " Total");
//                System.out.println("Signatures per second=" + i / signatures + " Signatures");
            } catch (Exception ex) {

            }
        }
    }
}
