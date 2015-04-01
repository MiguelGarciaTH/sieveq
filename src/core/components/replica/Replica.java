/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import core.management.CoreProperties;

/**
 *
 * @author miguel
 */
public class Replica {

    private int ID;
    
    private CoreProperties prop;

    public Replica(int mode, int id) {
        this.ID = id;
        switch (mode) {
            case 1:
                new ReplicaExecutorOne(ID);//false
                break;
//            case 2:
//                new ReplicaExecutorTwo(ID);//true
//                break;
            case 3:
                new ReplicaExecutorOne(ID);//false
                break;
        }
    }
    
}
