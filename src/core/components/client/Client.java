/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

/**
 *
 * @author miguel
 */
public class Client {

    private int ID;
    private Thread exec;

    public Client(int mode, int id, int dst) {
        this.ID = id;
        switch (mode) {
//            case 1:
//                exec = new Thread(new ClientExecutorOne(ID, dst));
//                break;
//            case 2:
//                exec = new Thread(new ClientExecutorTwo(ID, dst));
//                break;
            case 3:
                exec = new Thread(new ClientExecutorThree(ID, dst));
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
