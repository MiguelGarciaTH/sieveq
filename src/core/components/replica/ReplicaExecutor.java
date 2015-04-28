/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import bftsmart.tom.MessageContext;
import bftsmart.tom.ReplicaContext;
import bftsmart.tom.ServiceReplica;
import bftsmart.tom.core.messages.TOMMessage;
import bftsmart.tom.server.defaultservices.DefaultSingleRecoverable;
import core.components.workerpool.WorkerPool;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import core.management.RouteTable;
import core.management.CoreProperties;
import core.modules.malicious.Malicious;
import core.modules.malicious.MaliciousFactory;
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
public abstract class ReplicaExecutor extends DefaultSingleRecoverable {

    protected int ID;
    protected ServiceReplica replica;
    protected RouteTable route;
    protected TreeMap<Integer, Integer> connected;
    protected int sharedID = 0;
    protected CryptoScheme crypto;
    protected Malicious malicious;
    private int state = 0;
    private WorkerPool workers;
    private Executor exec;
    protected ArrayBlockingQueue out, inCrypto, outCrypto;

    public ReplicaExecutor(int id) {
        this.ID = id;
        this.replica = new ServiceReplica(id, this, this, true);
        this.route = new RouteTable();
        this.connected = new TreeMap<Integer, Integer>();
        this.crypto = CryptoSchemeFactory.getCryptoScheme(null);
        this.malicious = MaliciousFactory.getMaliciousModule();
        this.inCrypto = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.outCrypto = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.out = new ArrayBlockingQueue(CoreProperties.queue_size * 2);
        this.workers = new WorkerPool(inCrypto, outCrypto, "cryptoreplica", CoreProperties.num_workers);
        this.exec = new Executor(outCrypto, out, route, connected, replica.getSVManager());
        new Thread(exec).start();
    }

    @Override
    public void setReplicaContext(ReplicaContext replicaContext) {
//        this.replicaContext = replicaContext;
    }

    @Override
    public byte[] appExecuteOrdered(TOMMessage command, MessageContext msgCtx) {
        inCrypto.add(command);
        return null;
    }

    @Override
    public void installSnapshot(byte[] state) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            this.state = (int) in.readInt();
            this.route = (RouteTable) in.readObject();
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

    private void printState() {
        if (route != null) {
            System.out.println("State <<");
            this.route.prettyPrint();
            System.out.print("connected=" + this.connected.size());
            System.out.println(">>");
        }
    }

    public abstract void setRoute(RouteTable tablle);
}
