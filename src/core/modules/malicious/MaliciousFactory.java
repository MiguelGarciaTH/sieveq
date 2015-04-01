/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.malicious;

import core.management.CoreConfiguration;
import core.management.CoreProperties;

/**
 *
 * @author miguel
 */
public class MaliciousFactory {

    protected static Malicious malicious;

    public static Malicious getMaliciousModule() {
        if (malicious != null) {
            return malicious;
        } else {
            return CoreProperties.malicious && CoreConfiguration.ID == CoreProperties.malicious_id ? new MaliciousOn() : new MaliciousOff();
        }
    }
}
