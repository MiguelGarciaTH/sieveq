/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

import core.components.client.network.ClientTCPSend;
import core.components.client.network.ClientTCPReceiveThread;
import core.components.workerpool.WorkerPool;
import core.management.ServerSession;
import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.util.Iterator;

/**
 *
 * @author miguel
 */
public class ClientExecutorThree extends ClientExecutor implements Runnable, Forwarder {

    private ClientTCPSend snd;
    private ClientTCPReceiveThread rcv;
    private Thread t1, t2;
    private boolean trinco = true;

    ClientExecutorThree(int ID, int dst) {
        super(ID, dst);
        rcv = new ClientTCPReceiveThread(prop.listen_port, this);
        snd = new ClientTCPSend(prop.ip, prop.destiny_port, out, inQueue, threadQueue);
        t1 = new Thread(snd);
        new Thread(rcv).start();
        t1.start();
    }

    @Override
    void send(byte[] cmd) {
    }

    @Override
    public void replyReceived(byte[] data) {
        Message resp = new Message().deserialize(data, deserialized);
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
                case Message.WARMUP_END_ACK:
                    lock2 = false;
                    CoreConfiguration.print("server unlocked WARMUP_END_ACK received");
                    break;
                case Message.SEND_REQUEST:
                    break;
                case Message.CHG_PREREPLICA:
                    if (trinco) {
                        CoreConfiguration.print("changing pre-replica");
                        updatePrereplica(resp.getData());
                        trinco = false;
                    }
                    break;
                case Message.ACK:
                    int ack = Integer.parseInt(new String(resp.getData()));
                    cleanFromAck(ack);
                    break;
                case Message.END_ACK:
                    finishTime = System.nanoTime();
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

    private synchronized void updatePrereplica(byte[] data) {
        String[] addr = new String(data).split(":");
        CoreConfiguration.updateId(Integer.parseInt(addr[2]));
        CoreProperties.destiny_port = Integer.parseInt(addr[1]);
        t1.interrupt();
        pool.stopWorkers();
        CoreConfiguration.print("Pool stoped");
        in.clear();
        out.clear();
        CoreConfiguration.print("in and out clear");
        this.pool = new WorkerPool(in, out, threadQueue, "crypto", CoreProperties.num_workers);
        CoreConfiguration.print("new worker pool");
        CoreConfiguration.print("\n\n \tNeed to re-send=" + inAux.size());
        in.addAll(inAux);
        snd = new ClientTCPSend(addr[0], Integer.parseInt(addr[1]), out, inQueue, threadQueue);
        new Thread(snd).start();
        CoreConfiguration.print("ClientTCPSend (thread) start");
        inAux.clear();
    }
}
