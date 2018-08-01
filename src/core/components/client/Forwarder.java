/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

/**
 *
 * @author miguel
 */
public interface Forwarder {

    public void replyReceived(byte[] reply);
}
