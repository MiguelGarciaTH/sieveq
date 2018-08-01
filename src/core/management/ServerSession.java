/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import java.util.Arrays;

/**
 *
 * @author miguel
 */
public class ServerSession {

    private int clientID;
    private int prereplicaID;
    private int[] replicaID;
    private int serverID;
    private boolean alive;
    private int InSequenceNumberExpected;
    private int OutSequenceNumberExpected;
    

    public ServerSession(int clientID, int prereplicaID, int[] replicaID, int serverID, boolean alive) {
        this.clientID = clientID;
        this.prereplicaID = prereplicaID;
        this.replicaID = replicaID;
        this.serverID = serverID;
        this.alive = true;
        this.InSequenceNumberExpected = -1;
        this.OutSequenceNumberExpected = 0;
        
    }

    public int getDestination(int src) {
        return (clientID == src) ? serverID : clientID;

    }

    public int[] getDestinationArray(int src) {
        return (clientID == src) ? new int[]{serverID} : new int[]{clientID};
    }

    public String toString() {
        if (alive) {
            return "Session=[ client=" + clientID + " prereplica=" + prereplicaID + " replica= " + Arrays.toString(replicaID) + " seq numb expc=" + InSequenceNumberExpected + "]";
        } else {
            return "Session=[ closed ]";
        }
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public int getPrereplicaID() {
        return prereplicaID;
    }

    public void setPrereplicaID(int prereplicaID) {
        this.prereplicaID = prereplicaID;
    }

    public int[] getReplicaID() {
        return replicaID;
    }

    public void setReplicaID(int[] replicaID) {
        this.replicaID = replicaID;
    }

    public int getServerID() {
        return serverID;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getOutSequenceNumberExpected() {
        return OutSequenceNumberExpected;
    }

    /**
     * Increment the sequence number that is sent by the server
     *
     * @return the incremented sequence number
     */
    public int incrementeOutSequenceNumber() {
        return this.OutSequenceNumberExpected++;
    }

    public int getInSequenceNumberExpected() {
        return InSequenceNumberExpected;
    }

    /**
     * Increment the sequence number that is received on the server
     *
     * @return the incremented sequence number
     */
    public int incrementeInSequenceNumber() {
        return this.InSequenceNumberExpected++;
    }

}
