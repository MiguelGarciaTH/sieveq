/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.firewall;

/**
 *
 * @author miguel
 */
public class FirewallFactory {

    public static Firewall firewall = null;

    public static Firewall getFirewall(String mode) {
        if (firewall != null) {
            return firewall;
        } else {
            switch (mode) {
                case "one":
                    firewall = new FirewallCentralized();
                    return firewall;
                case "two":
                    firewall = new FirewallDistributed();
                    return firewall;
                case "three":
                    firewall = new FirewallDistributed();
                    return firewall;
                default:
                    firewall = new FirewallEmpty();
                    return firewall;
            }
        }
    }
}
