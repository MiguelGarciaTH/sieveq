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
public class ControllerOperatorFactory {

    static ControllerOperator operator = null;

    public static ControllerOperator getControllerOperator(String type) {
        if (operator != null) {
            return operator;
        }
        switch (type) {
            case "processes":
                return new ControllerOpreatorProcesses();
            default:
                return null;

        }

    }

}
