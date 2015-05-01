/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.core.messages.TOMMessage;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author miguel
 */
public class ServerExecutorTwo extends ServerExecutor implements Runnable, ReplyListener {

    private BlockingQueue firstQueue, secondQueue, thirdQueue;
    private FirstServerFilter firstFilter;
    private Thread first;
    private ServerReplyManager replyManager;
    private final Thread third;
    private byte[] cmd;
    private CryptoScheme crypto;

    /**
     *
     * @param ID
     */
    public ServerExecutorTwo(int ID) {
        super(ID);
        this.firstQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.secondQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.firstFilter = new FirstServerFilter(ID, firstQueue, secondQueue, sessions);
        this.replyManager = new ServerReplyManager(this, ID, secondQueue, sessions, proxy, lock, true, new int[4]);
        this.first = new Thread(firstFilter);
        this.third = new Thread(replyManager);
        this.cmd = new byte[5];
        this.crypto = CryptoSchemeFactory.getCryptoScheme(null);
    }

    @Override
    public void run() {
        first.start();
        third.start();
        new Random().nextBytes(cmd);
        this.processes = proxy.getViewManager().getCurrentViewProcesses();
        send(Message.HELLO, sessions.get(0).incrementeOutSequenceNumber(), cmd);
        while (lock.isLocked()) {
            try {
                CoreConfiguration.print("sleeping\t ");
                Thread.sleep(2000);
                send(Message.HELLO, sessions.get(0).incrementeOutSequenceNumber(), cmd);
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
            }
        }
        ByteBuffer dst = ByteBuffer.allocate(4);
        dst.putInt(destination);
        send(Message.ADD_ROUTE, sessions.get(0).incrementeOutSequenceNumber(), dst.array());
    }

    @Override
    public void replyReceived(TOMMessage reply) {
        byte[] data = reply.getContent();
        Message resp = null;
        try {
            if (Message.hasCrypto(data)) {
                if (crypto.serverVerifyMessage(data)) {
                    resp = new Message().deserialize(data, deserialized);
                    firstQueue.put(resp);
                } else {
                    CoreConfiguration.print("Verification error");
                }
            } else {
                resp = new Message().deserialize(data,deserialized);
                firstQueue.put(resp);
            }
        } catch (InterruptedException ex) {
            CoreConfiguration.print("Clear Queue...");
            firstQueue.clear();
        }
    }

    private void send(int type, int sequenceNumber, byte[] cmd) {
        Message resp = new Message(type, ID, sequenceNumber, cmd);
        proxy.invokeAsynchronous(resp.serialize(serialized1), this, new int[]{processes[0]});
    }
}
