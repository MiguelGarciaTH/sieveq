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
public abstract class Firewall {
    
    abstract public boolean fw_prefilter(byte[] message);
    
    abstract public boolean fw_filter(byte[] message);
    

}
