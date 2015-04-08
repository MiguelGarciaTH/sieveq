/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import bftsmart.communication.ServerCommunicationSystem;
import bftsmart.tom.MessageContext;
import bftsmart.tom.core.messages.TOMMessage;

/**
 *
 * @author miguel
 */
public class ReplicaExecutorOne extends ReplicaExecutor {

    protected ServerCommunicationSystem comm;
    private ExecutorReplierOne replier;

    public ReplicaExecutorOne(int id) {
        super(id);
        this.replier = new ExecutorReplierOne(out, connected, sharedID, route, replica, malicious);
        Thread t = new Thread(replier);
        t.start();
//        if (id == 1) {
//            System.out.println("************ Attacker ************");
//            ReplicaDoS attacker = new ReplicaDoS(CoreProperties.attack_threads, CoreProperties.rate_message, CoreProperties.target_ip, CoreProperties.target_port,t);
//            new Thread(attacker).start();
//        }
    }

}
