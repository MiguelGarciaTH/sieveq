/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import core.management.ByteArrayWrap;
import core.management.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class CryptoWorkerPrereplica extends Worker {

    private final CryptoScheme crypto;
    private final CryptoSchemeFactory fact = new CryptoSchemeFactory();

    private RemindTask task;
    private int numberofMessages = 0;
    private Timer timer;
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);

    CryptoWorkerPrereplica(int tid, ArrayBlockingQueue toMac, ArrayBlockingQueue inQueue, DataBlockQueue threadBlock) {
        super(tid, toMac, inQueue);
        this.crypto = fact.getNewCryptoScheme(CoreProperties.crypto_scheme);
//        task = new RemindTask();
//        timer = new Timer();
//        timer.schedule(task, 0, 1000); //delay in milliseconds

    }

    @Override
    public void run() {
        while (true) {
            try {
                ByteArrayWrap data = (ByteArrayWrap) in.take();
                if (Message.hasCrypto(data.getArr())) {
                    if (crypto.prefilterVerifyMessage(data.getArr(), data.getSize())) {
                        out.put(data);
                    } else {
                        System.out.println("TID=" + tid + "  Corrupted!=" + new Message().deserialize(data.getArr(), deserialized));
                    }
                } else {
                    out.put(data);
                }
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }
    }

    class RemindTask extends TimerTask {

        RemindTask() {
        }

        @Override
        public void run() {
//            System.out.println("IN queue capacity cipher>" + in.size());
//            System.out.println("OUT queue capacity cipher>" + out.size());
            //   System.out.println("TID=" + tid + " messages=" + numberofMessages);
            numberofMessages = 0;
        }
    }
}
