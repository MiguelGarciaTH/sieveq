/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import core.management.Message;
import core.management.ByteArrayWrap;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class CryptoWorkerClient2 extends Worker {

    private CryptoScheme crypto;
    private CryptoSchemeFactory fact;

    private RemindTask task;
    private int numberofMessages = 0;
    private Timer timer;
    private Thread myThread;
    //private byte[] serl = new byte[Message.HEADER_SIZE + CoreProperties.message_size + CoreProperties.signature_key_size + CoreProperties.hmac_key_size];
    private byte[] serl = new byte[Message.HEADER_SIZE + CoreProperties.message_size + (CoreProperties.hmac_key_size * 4)];
    private ByteBuffer serialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    private DataBlockQueue threadQueue;

    CryptoWorkerClient2(int tid, ArrayBlockingQueue in, ArrayBlockingQueue out, DataBlockQueue threadQueue) {
        super(tid, in, out);
        this.fact = new CryptoSchemeFactory();
        this.crypto = fact.getNewCryptoScheme(CoreProperties.crypto_scheme);
        this.threadQueue = threadQueue;
        //        this.task = new RemindTask();
//        timer = new Timer();
//        timer.schedule(task, 0, 1000); //delay in milliseconds
        
    }

    @Override
    public void run() {
        myThread = Thread.currentThread();
        Message msg = null;
        try {
            while (true) {
                msg = (Message) in.take();
                serl = threadQueue.take();
                int numbytes = msg.serialize(serl, serialized);
                crypto.clientSecureMessage(serl, numbytes);
                //out.put(new ByteArrayWrap(serl, (numbytes + CoreProperties.signature_key_size + CoreProperties.hmac_key_size)));
                threadQueue.offer(serl);
            }
        } catch (InterruptedException ex) {
            cancel();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
        }
    }

    public void cancel() {
//        threadQueue.clear();
        if (timer != null) {
            timer.cancel();
        }
        if (myThread != null) {
            myThread.interrupt();
        }
    }

    class RemindTask extends TimerTask {

        int stop_counter = 2;

        RemindTask() {

        }

        @Override
        public void run() {
            if (numberofMessages == 0) {
                stop_counter--;
            }
            if (stop_counter == 0) {
                timer.cancel();
            }
            System.out.println("TID=" + tid + " messages=" + numberofMessages);
            numberofMessages = 0;

        }
    }
}
