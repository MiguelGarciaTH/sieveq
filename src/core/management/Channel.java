/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core.management;

import core.management.Message;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author miguel
 */
public abstract class Channel  {

    protected BlockingQueue queue;
    public Channel(BlockingQueue queue) {
        this.queue=queue;
    }
    
    public abstract void queueMessage(Message msg);
    
    public abstract void retrieveMessage(Message msg);
    
    
}
