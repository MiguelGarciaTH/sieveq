/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.ServiceProxy;
import core.components.workerpool.ThreadBlockQueue;
import core.message.ByteArrayWrap;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class ClientExecutorOneSMART implements Runnable {

    private ServiceProxy proxy;
    private BlockingQueue out;
    private int[] processes;
    private ReplyListener reply;
    protected ByteBuffer serliazedBuf = ByteBuffer.allocate(5000).order(ByteOrder.BIG_ENDIAN);
    private ThreadBlockQueue threadQueue;
    private int numberofMessages;
    private Timer timer;
    private RemindTask task;

    public ClientExecutorOneSMART(ServiceProxy proxy, BlockingQueue out, int[] processes, ReplyListener reply, ThreadBlockQueue threadQueue) {
        this.proxy = proxy;
        this.out = out;
        this.processes = processes;
        this.reply = reply;
        this.threadQueue = threadQueue;
        task = new RemindTask();
        timer = new Timer();
        timer.schedule(task, 0, 1000); //delay in milliseconds
    }

    @Override
    public void run() {
        while (true) {
            try {
                ByteArrayWrap dataWrap = (ByteArrayWrap) out.take();
                new ByteArrayWrap().serialize(serliazedBuf, dataWrap.getArr(), dataWrap.getSize());
                proxy.invokeAsynchronous(dataWrap.getArr(), reply, processes);
                numberofMessages++;
                threadQueue.offer(dataWrap.getArr());
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientExecutorOneSMART.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class RemindTask extends TimerTask {

        int i = 0;
        int counter = 0;

        RemindTask() {
        }

        @Override
        public void run() {
            //        secondsOffset = rounds++ * 1000000000;// N segundos
//            System.out.println("N segundo=" + secondsOffset / 1000000000);
            System.out.println("#sent=" + numberofMessages + " second=" + i++);// + " | > rate=" + moreRate + " | > 1sec=" + timeStop);
            if (numberofMessages == 0) {
                counter++;
                if (counter == 10) {
                    this.cancel();
                }
            }
            numberofMessages = 0;
        }
    }

}
