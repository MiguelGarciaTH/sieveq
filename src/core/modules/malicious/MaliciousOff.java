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
public class MaliciousOff extends Malicious {

    @Override
    public byte[] corrupt(byte[] data) {
        //System.out.println("empty");
        return data;
    }

}
