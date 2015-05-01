/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import core.components.attacker.ReplicaDoS;
import core.management.CoreProperties;
import core.management.RouteTable;

/**
 *
 * @author miguel
 */
public class ReplicaExecutorOne extends ReplicaExecutor {

    private Thread t;
    private ExecutorReplierOne replier;

    public ReplicaExecutorOne(int id) {
        super(id);
        replier = new ExecutorReplierOne(out, connected, sharedID, route, replica, malicious);
        t = new Thread(replier);
        t.start();
        if (id == 1) {
            System.out.println("************ Attacker ************");
            ReplicaDoS attacker = new ReplicaDoS(replica, CoreProperties.attack_threads, CoreProperties.rate_message, CoreProperties.target_ip, CoreProperties.target_port,t);
            new Thread(attacker).start();
        }
    }

    public void setRoute(RouteTable table) {
        replier.setRoute(table);
    }

}
