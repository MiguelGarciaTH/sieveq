/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.reliableChannel;

import core.management.CoreProperties;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author miguel
 */
public class ChannelFactory {

    public static Channel channel = null;

    public static Channel getChannel(boolean reliable, BlockingQueue queue) {
        if (channel != null) {
            return channel;
        } else {
            if (reliable) {
                return (channel= new ReliableChannel(queue, CoreProperties.MAX_BUFFERED));
            } else {
                return (channel=new NonreliableChannel(queue));
            }
        }
    }
}
