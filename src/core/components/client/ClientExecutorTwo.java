/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessage;
import core.message.Message;
import core.management.CoreConfiguration;

/**
 *
 * @author miguel
 */
public class ClientExecutorTwo extends ClientExecutor implements Runnable, ReplyListener {

    private ServiceProxy proxy;
    private int[] processes;

    public ClientExecutorTwo(int ID, int dst) {
        super(ID, dst);
        this.proxy = new ServiceProxy(ID);
        proxy.setReplyListener(this);
        processes = proxy.getViewManager().getCurrentViewProcesses();
    }

    @Override
    void send(byte[] cmd) {
        proxy.invokeAsynchronous(cmd, this, processes); // find some way to find who is the lider.
    }

    @Override
    public void replyReceived(TOMMessage reply) {
        Message resp = new Message().deserialize(reply.getContent(),deserialized);
        switch (resp.getType()) {
            case Message.HELLO_ACK:
                CoreConfiguration.print("HELLO_ACK received");
                break;
            case Message.CONNECT:
                byte[] data = resp.getData();
                int[] cli = bytesToInt(data);
                addClients(cli);
                lock = false;
                CoreConfiguration.print("server unlocked");
                break;
            case Message.END_ACK:
                CoreConfiguration.print("END_ACK received");
                long total = System.nanoTime() - timeInit;
                double seconds = (double) total / 1000000000.0;
                System.out.println("Elapsed time: " + seconds);
                System.out.println("Time Msg/Sec: " + (seconds / prop.experiment_rounds));
                break;
            default:
                System.out.println("Unknown type=" + resp.getType());
        }
    }
}
