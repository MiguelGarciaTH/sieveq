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
public class NonreliableChannel extends Channel {

    public NonreliableChannel(BlockingQueue queue) {
        super(queue);
    }

    @Override
    public void queueMessage(Message msg) {

    }

    @Override
    public void retrieveMessage(Message msg) {

    }

}
