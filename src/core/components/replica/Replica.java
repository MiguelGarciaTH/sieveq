/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.server.defaultservices.DefaultRecoverable;
import core.components.workerpool.WorkerPool;
import core.modules.crypto.CryptoScheme;
import core.management.RouteTable;
import core.management.CoreProperties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class Replica extends DefaultRecoverable {

    protected int ID;
    protected ServiceReplica replica;
    protected RouteTable route;
    protected TreeMap<Integer, Integer> connected;
    protected int sharedID = 0;
    protected CryptoScheme crypto;
    private int state = 0;
    private final WorkerPool workers;
    private final Executor exec;
    protected ArrayBlockingQueue out, inCrypto, outCrypto;
    private final Thread t;
    private final ReplicaReplier replier;

    public Replica(int id) {
        this.ID = id;
        this.replica = new ServiceReplica(id, this, this);
        this.route = new RouteTable();
        this.connected = new TreeMap<>();
//        this.crypto = CryptoSchemeFactory.getCryptoScheme(null);
        this.inCrypto = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.outCrypto = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.out = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.workers = new WorkerPool(inCrypto, outCrypto, "cryptoreplica", CoreProperties.num_workers);
//        this.exec = new Executor(outCrypto, out, route, connected, replica.getSVManager());
        this.exec = new Executor(outCrypto, out, route, connected, replica.getReplicaContext().getSVController());

        replier = new ReplicaReplier(out, connected, sharedID, route, replica);
        t = new Thread(replier);
        t.start();
//        if (id == 1) {
//            System.out.println("************ Attacker ************");
//            ReplicaDoS attacker = new ReplicaDoS(replica, CoreProperties.attack_threads, CoreProperties.rate_message, CoreProperties.target_ip, CoreProperties.target_port,t);
//            new Thread(attacker).start();
//        }
        new Thread(exec).start();

    }

    @Override
    public void installSnapshot(byte[] state) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            this.state = (int) in.readInt();
            this.route = (RouteTable) in.readObject();
            this.route.prettyPrint();
            setRoute(this.route);
            this.connected = (TreeMap<Integer, Integer>) in.readObject();
            System.out.println("Recovering state=" + this.state);
            in.close();
            bis.close();
        } catch (Exception ex) {
            System.out.println("Install snapshot:");
            ex.printStackTrace();
        }
    }

    @Override
    public byte[] getSnapshot() {
        System.out.println("Saving state:" + this.state++);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeInt(this.state);
            out.flush();
            out.writeObject(this.route);
            out.flush();
//            this.route.prettyPrint();
            out.writeObject(this.connected);
            out.flush();
            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (Exception ex) {
            System.out.println("load snapshot:");
            ex.printStackTrace();
            return new byte[0];
        }
    }

    public void setRoute(RouteTable table) {
        replier.setRoute(table);

    }

    @Override
    public byte[][] appExecuteBatch(byte[][] commands, MessageContext[] msgCtxs) {
        byte[][] replies = new byte[commands.length][];
        for (int i = 0; i < commands.length; i++) {
            inCrypto.add(msgCtxs[i].recreateTOMMessage(commands[i]));

        }

        return replies;
    }

    @Override
    public byte[] appExecuteUnordered(byte[] command, MessageContext msgCtx) {
        inCrypto.add(msgCtx.recreateTOMMessage(command));
        return command;
    }

    

}
