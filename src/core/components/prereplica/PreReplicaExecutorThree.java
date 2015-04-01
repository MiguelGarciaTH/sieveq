  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessage;
import core.components.prereplica.network.PreReplicaTCPReceiveThread;
import core.components.prereplica.network.PreReplicaTCPSend;
import core.components.workerpool.ThreadBlockQueue;
import core.message.ByteArrayWrap;
import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.malicious.Malicious;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class PreReplicaExecutorThree implements ReplyListener, Runnable {

    private PreReplicaTCPSend snd;
    private PreReplicaTCPReceiveThread rcv;
    private ServiceProxy proxy;
    private int[] processes;
    private CoreProperties properties;
    private ArrayBlockingQueue outQueue;
    private ArrayBlockingQueue inQueue;
    protected Malicious malicious;

    protected ThreadBlockQueue threadBlock;

    private RemindTask task;
    private Timer timer;
    private int numberofMessages = 0;
    protected ByteBuffer serliazedBuf = ByteBuffer.allocate(2000);
    protected ByteBuffer deserliazedBuf = ByteBuffer.allocate(2000);

    PreReplicaExecutorThree(int ID) {
        this.proxy = new ServiceProxy(ID);
//       int totalSize=Message.HEADER_SIZE + CoreProperties.message_size + CoreProperties.signature_key_size + CoreProperties.hmac_key_size * 4;
        int totalSize = Message.HEADER_SIZE + CoreProperties.message_size + CoreProperties.signature_key_size + CoreProperties.hmac_key_size;
        this.threadBlock = new ThreadBlockQueue(CoreProperties.messageRate, totalSize);
        this.outQueue = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.inQueue = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.rcv = new PreReplicaTCPReceiveThread(properties.listen_port, inQueue, threadBlock);
        //this.malicious = MaliciousFactory.getMaliciousModule();
        new Thread(rcv).start();
//        task = new RemindTask();
//        timer = new Timer();
//        timer.schedule(task, 0, 1000); //delay in milliseconds
    }

    @Override
    public void replyReceived(TOMMessage reply) {
        byte[] resp = reply.getContent();
        outQueue.add(resp);
    }

    @Override
    public void run() {
        this.snd = new PreReplicaTCPSend(properties.ip, properties.destiny_port, outQueue);
        new Thread(snd).start();
        try {
            processes = proxy.getViewManager().getCurrentViewProcesses();
            while (true) {
                ByteArrayWrap dataWrap;
                try {
                    dataWrap = (ByteArrayWrap) inQueue.take();
                    new ByteArrayWrap().serialize(serliazedBuf, dataWrap.getArr(), dataWrap.getSize());
                    proxy.invokeAsynchronous(dataWrap.getArr(), this, processes);
                    threadBlock.offer(dataWrap.getArr());
                } catch (ClassCastException ex1) {
                    System.out.println("HERE=>" +ex1.getMessage());
                }
//                byte[] data = new byte[dataWrap.getSize()];
//                System.arraycopy(dataWrap.getArr(), 0, data, 0, dataWrap.getSize());
                //                if (Message.deserialize(data).getType() == Message.CHG_PREREPLICA_REQUEST) {
//                    System.out.println("**** CHANGE REPLICA REQUEST!!!");
//                    CoreConfiguration.pause(5);
//                }
                //data = malicious.corrupt(data);
//               numberofMessages++;
            }
        } catch (InterruptedException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
        }
    }

    class RemindTask extends TimerTask {

        RemindTask() {
        }

        @Override
        public void run() {
            System.out.println("Number of messages=" + numberofMessages);
            numberofMessages = 0;
        }
    }
}
