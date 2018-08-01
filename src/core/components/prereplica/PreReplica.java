/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica;

/**
 *
 * @author miguel
 */
public class PreReplica {

    private int ID;
    private Thread exec;

    public PreReplica(int mode, int id) {
        this.ID = id;
        exec = new Thread(new PreReplicaExecutor(ID));
    }

    public void start() {
        exec.run();
    }

    public void stop() {
        exec.stop();
    }
}
