/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import bftsmart.demo.bftmap.MapOfMaps;
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
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public abstract class ReplicaExecutor extends DefaultSingleRecoverable {

    protected int ID;
    protected ServiceReplica replica;
    protected RouteTable route;
    protected HashMap<Integer, Integer> connected;
    protected int sharedID = 0;
    protected ReplicaContext replicaContext;
    protected CryptoScheme crypto;
    protected Malicious malicious;
    // protected MessageContext msgCtx;

    private WorkerPool workers;
    private Executor exec;
    protected ArrayBlockingQueue out, inCrypto, outCrypto;
    protected ConcurrentHashMap<Integer, LinkedList<Integer>> route_struct;

    public ReplicaExecutor(int id) {
        this.ID = id;
        this.replica = new ServiceReplica(id, this, this, true);
        this.route_struct = new ConcurrentHashMap<Integer, LinkedList<Integer>>();
        this.route = new RouteTable(route_struct);
        this.connected = new HashMap<Integer, Integer>();
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

    private byte[] execute(TOMMessage cmd) {
        
        inCrypto.add(cmd);
        return null;
    }

    @Override
    public byte[] executeOrdered(TOMMessage command, MessageContext msgCtx) {
        return execute(command);
    }

    @Override
    public byte[] executeUnordered(TOMMessage command, MessageContext msgCtx) {
        return execute(command);
    }

    @Override
    public void installSnapshot(byte[] state) {
        System.out.println("Recovering state");
        try {

            // serialize to byte array and return
            ByteArrayInputStream bis = new ByteArrayInputStream(state);
            ObjectInput in = new ObjectInputStream(bis);
            ID = (int) in.readObject();
            route = (RouteTable) in.readObject();
            connected = (HashMap<Integer, Integer>) in.readObject();
            sharedID = (int) in.readObject();
            replicaContext = (ReplicaContext) in.readObject();
            crypto = (CryptoScheme) in.readObject();
            malicious = (Malicious) in.readObject();
            this.out = (ArrayBlockingQueue) in.readObject();
            inCrypto = (ArrayBlockingQueue) in.readObject();
            outCrypto = (ArrayBlockingQueue) in.readObject();
            route_struct = (ConcurrentHashMap<Integer, LinkedList<Integer>>) in.readObject();

            in.close();
            bis.close();

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ReplicaExecutor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReplicaExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public byte[] getSnapshot() {
        System.out.println("Making state snapshot");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(ID);
            out.flush();
            out.writeObject(route);
            out.flush();
            out.writeObject(connected);
            out.flush();
            out.writeObject(sharedID);
            out.flush();
            out.writeObject(replicaContext);
            out.flush();
            out.writeObject(crypto);
            out.flush();
            out.writeObject(malicious);
            out.flush();
            out.writeObject(this.out);
            out.flush();
            out.writeObject(inCrypto);
            out.flush();
            out.writeObject(outCrypto);
            out.flush();
            out.writeObject(route_struct);
            out.flush();

            bos.flush();
            out.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(ReplicaExecutor.class.getName()).log(Level.SEVERE, null, ex);
            return new byte[0];
        }
    }

    @Override
    public byte[] appExecuteOrdered(byte[] command, MessageContext msgCtx) {
        return executeOrdered(command, msgCtx);
    }
}
