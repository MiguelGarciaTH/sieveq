/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.prop
 */
package core.components.server;

import core.management.CoreProperties;

/**
 *
 * @author miguel
 */
public class Server {

    private final int ID;
    private final Thread exec;
    private final CoreProperties prop;

    public Server(int mode, int id, CoreProperties prop) {
        this.prop = prop;
        this.ID = id;
        exec = new Thread(new ServerExecutor(ID));
    }

    public void start() {
        exec.run();
    }

    public void stop() {
        exec.stop();
    }
}
