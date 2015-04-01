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
public class FirewallEmpty extends Firewall {

    @Override
    public boolean fw_prefilter(byte[] message) {
        return true;
    }

    @Override
    public boolean fw_filter(byte[] message) {
        return true;
    }

}
