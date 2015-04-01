/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessage;
import core.management.ServerSession;
import core.message.Message;
import core.management.CoreConfiguration;
import java.util.Iterator;

/**
 *
 * @author miguel
 */
public class ClientExecutorOne extends ClientExecutor implements Runnable, ReplyListener {

    private ServiceProxy proxy;
    private int[] processes;
    private ClientExecutorOneSMART client;

    /**
     *
     * @param ID
     */
    public ClientExecutorOne(int ID, int dst) {
        super(ID, dst);
        this.proxy = new ServiceProxy(ID);
        proxy.setReplyListener(this);
        processes = proxy.getViewManager().getCurrentViewProcesses();
        this.client = new ClientExecutorOneSMART(proxy, out, processes, this,threadQueue);
        new Thread(client).start();
    }

    @Override
    void send(byte[] cmd) {

    }

    @Override
    public void replyReceived(TOMMessage reply) {
        Message resp = new Message().deserialize(reply.getContent(), deserialized);
        ServerSession session = sessions.get(resp.getSrc());
        boolean quorom = voter.vote(resp.getSrc(), session, resp.getSeqNumber(), resp.getData());
        if (quorom) {
            int type = resp.getType();
            switch (type) {
                case Message.HELLO_ACK:
                    CoreConfiguration.print("Hello ack received");
                    break;
                case Message.CONNECT:
                    byte[] data2 = resp.getData();
                    int[] cli = bytesToInt(data2);
                    addClients(cli);
                    CoreConfiguration.print("server unlocked");
                    lock = false;
                    break;
//                case Message.WARMUP_END_ACK:
//                    lock2 = false;
//                    CoreConfiguration.print("server unlocked WARMUP_END_ACK received");
//                    break;
                case Message.SEND_REQUEST:
                    break;
                case Message.ACK:
                    int ack = Integer.parseInt(new String(resp.getData()));
                    cleanFromAck(ack);
                    break;
                case Message.END_ACK:
                    finishTime = System.nanoTime();
                    double elapsed = finishTime - initTime / 1000000.0;
                    CoreConfiguration.print("Elapsed time: " + elapsed);
//                    CoreConfiguration.print("Latency overall=>" + elapsed / (prop.experiment_rounds * prop.messageRate));
//                    CoreConfiguration.print("More than 1sec: " + snd.timeStop);
//                    CoreConfiguration.print("More than rate: " + snd.moreRate);
                    break;
                default:
                    CoreConfiguration.print("Unknown type=" + resp.getType());
            }
        }
    }

    private synchronized void cleanFromAck(int ack) {
        Iterator it;
        it = inAux.iterator();
        while (it.hasNext()) {
            Message m = (Message) it.next();
            if (m.getSeqNumber() <= ack) {
                it.remove();
            }
            if (m.getSeqNumber() > ack) {
                break;
            }

        }
    }

}
