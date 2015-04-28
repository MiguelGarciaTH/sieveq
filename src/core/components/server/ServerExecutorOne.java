/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import bftsmart.tom.core.messages.TOMMessage;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class ServerExecutorOne extends ServerExecutor {

    private ArrayBlockingQueue firstQueue, secondQueue, thirdQueue, orderedQueue;
    private FirstServerFilter firstFilter;
    private SecondServerFilter secondFilter;
    private OrderQueue order;
    private Thread first, second, third, fourth;
    private ServerReplyManager replyManager;
    private byte[] cmd;
    private CryptoScheme crypto;

    /**
     *
     * @param ID
     */
    public ServerExecutorOne(int ID) {
        super(ID);
        this.firstQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.secondQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.thirdQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.orderedQueue = new ArrayBlockingQueue(CoreProperties.queue_size);
        this.firstFilter = new FirstServerFilter(ID, firstQueue, secondQueue, sessions);
        this.secondFilter = new SecondServerFilter(secondQueue, thirdQueue, sessions, voter);

        this.replyManager = new ServerReplyManager(this, ID, thirdQueue, sessions, proxy, lock, false);
        this.first = new Thread(firstFilter);
        this.second = new Thread(secondFilter);
        this.third = new Thread(replyManager);

        this.crypto = CryptoSchemeFactory.getCryptoScheme(null);
        this.cmd = new byte[1]; //resolver depois
            new Random().nextBytes(cmd);
    }

    @Override
    public void run() {
        first.start();
        second.start();
        third.start();
        this.processes = proxy.getViewManager().getCurrentViewProcesses();
        send(Message.HELLO, sessions.get(ID).incrementeOutSequenceNumber(), cmd);
        CoreConfiguration.print("Hello sent waiting confirmation");
        while (lock.isLocked()) {
            System.out.print(".");
            CoreConfiguration.pause(1);
        }
        ByteBuffer dst = ByteBuffer.allocate(4);
        dst.putInt(destination);
        send(Message.ADD_ROUTE, sessions.get(ID).incrementeOutSequenceNumber(), dst.array());
        CoreConfiguration.print("add route sent > " + sessions.get(destination));
    }

    @Override
    public void replyReceived(TOMMessage reply) {
        byte[] data = reply.getContent();
        try {
            Message resp = validate(data);
            firstQueue.put(resp);
        } catch (InterruptedException ex) {
            firstQueue.clear();
            CoreConfiguration.print("Clearing queue... ");
            CoreConfiguration.pause(1);
        }
    }

    private Message validate(byte[] data) {
        return new Message().deserialize(data, deserialized);
//        if (crypto.serverVerifyMessage(data)) {
//            return Message.deserialize(data);
//        } else {
//            if (!Message.hasCrypto(data)) {
//                return Message.deserialize(data);
//            } else {
//                CoreConfiguration.print("Verification error " + Message.deserialize(data));
//                return Message.deserialize(data);
//            }
        // HERE 
        // fazer um metodo que avalia que tipo de falha pode ser e envia de vez para o controller
    }

    private void send(int type, int sequenceNumber, byte[] cmd) {
        Message resp = new Message(type, ID, sequenceNumber, cmd);
        byte[] snt = resp.serialize(serialized1);
        proxy.invokeAsynchronous(snt, this, processes);
    }
}
