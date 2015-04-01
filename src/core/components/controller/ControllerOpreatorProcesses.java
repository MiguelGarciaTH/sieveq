/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.controller;

import core.management.CoreConfiguration;
import java.io.IOException;

/**
 *
 * @author miguel
 */
public class ControllerOpreatorProcesses extends ControllerOperator {

    @Override
    void createPreReplica(int id) {
        CoreConfiguration.print("create pre-replica=" + id);
        // String cmd = "echo 1";
        String cmd = "scripts/add-prereplica.sh " + id + " ";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "createPreReplica()", ex.getMessage());
        }
    }

    @Override
    void destroyPreReplica(int id) {
        CoreConfiguration.print("destroy pre-replica " + id);
        String cmd = "scripts/kill-prereplica.sh " + id + " ";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "destroyPreReplica()", ex.getMessage());
        }
    }

    @Override
    void createReplica(int id) {
        CoreConfiguration.print("create replica=" + id);
        // String cmd = "echo 1";
        String cmd = "scripts/add-replica.sh " + id + " ";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "createPreReplica()", ex.getMessage());
        }
    }

    @Override
    void destroyReplica(int id) {
        String cmd = "scripts/kill-replica.sh " + id + " ";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "destroyPreReplica()", ex.getMessage());
        }
    }

}
