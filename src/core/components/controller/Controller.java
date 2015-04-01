/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.controller;

/**
 *
 * @author miguel
 */
public class Controller {

    private int ID;
    private Thread exec;
    private ControllerOperator controller;

    public Controller(int id, int mode, String controllerType) {
        this.ID = id;
        controller = ControllerOperatorFactory.getControllerOperator(controllerType);
        switch (mode) {
            case 3:
                exec = new Thread(new ControllerSMaRt(ID, controller));
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
