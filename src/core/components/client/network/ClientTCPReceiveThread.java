/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.client.network;

import core.components.client.Forwarder;
import core.management.CoreConfiguration;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author miguel
 */
public class ClientTCPReceiveThread implements Runnable {

    private int port;
    private Forwarder reply;

    public ClientTCPReceiveThread(int port, Forwarder reply) {
        this.port = port;
        this.reply = reply;
    }

    @Override
    public void run() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(port);
            Socket connectionSocket;
            CoreConfiguration.print("** waiting connections on port=" + port);
            while (true) {
                connectionSocket = welcomeSocket.accept();
                if (connectionSocket != null) {
                    CoreConfiguration.print("Accepted connection=" +connectionSocket.getInetAddress().getHostAddress());
                    ClientTCPReceivePrereplica request = new ClientTCPReceivePrereplica(connectionSocket, reply);
                    Thread thread = new Thread(request);
                    thread.start();
                }
            }
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
            new ClientTCPReceiveThread(port, reply).run();
        }
    }
}
