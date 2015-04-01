/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core.modules.malicious;

/**
 *
 * @author miguel
 */
public class TesterMalicious {
    public static void main (String args[]){
        
        MaliciousOn on = new MaliciousOn();
        byte[] data = new byte[]{1};
        on.corrupt(data);
        
        
    }
}
