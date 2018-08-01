/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client;

import core.management.CoreConfiguration;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author miguel
 */
public class ClientTCPReceivePrereplica implements Runnable {

    private DataInputStream in;
    private Socket connectionSocket;
    private Forwarder reply;

    ClientTCPReceivePrereplica(Socket accept, Forwarder reply) {
        this.reply = reply;
        this.connectionSocket = accept;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(connectionSocket.getInputStream());
            while (true) {
                int len = in.readInt();
                if (len > 0 && len < 1500) {
                    byte[] data = new byte[len];
                    in.readFully(data);
                    reply.replyReceived(data);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            CoreConfiguration.print("connection close socket=" + connectionSocket.getLocalAddress().getHostAddress() + ":" + connectionSocket.getPort());
        }
    }

    public void close() {
        try {
            this.in.close();
            this.connectionSocket.close();
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "close()", ex.getMessage());
        }
    }
}
