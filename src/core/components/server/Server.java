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

    private int ID;
    private Thread exec;
    private CoreProperties prop;
    public Server(int mode, int id, CoreProperties prop) {
        this.prop=prop;
        this.ID = id;
        switch (mode) {
            case 1:
                exec = new Thread(new ServerExecutorOne(ID));
                break;
            case 2:
                exec = new Thread(new ServerExecutorTwo(ID));
                break;
            case 3:
                exec = new Thread(new ServerExecutorOne(ID));
                break;
        }
    }

    public void start() {
        exec.run();
    }

    public void stop() {
        exec.stop();
    }
}
