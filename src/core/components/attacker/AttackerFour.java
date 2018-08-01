/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.attacker;

/**
 *
 * @author miguel
 */
public class AttackerFour implements Runnable {

    private int numberOfThreads;
    private DoSThread[] bot;
    private Thread[] botThread;

    private int requestsPerSecond;
    private String IP;
    private int PORT;

    private String[] IPs;
    private int[] PORTs;

    public AttackerFour(int numberOfThreads, int reqs, String ip, int port) {
        this.numberOfThreads = numberOfThreads;
        bot = new DoSThread[numberOfThreads];
        botThread = new Thread[numberOfThreads];
        IP = ip;
        PORT = port;
        requestsPerSecond = reqs;
        for (int i = 0; i < numberOfThreads; i++) {
            bot[i] = new DoSThread(requestsPerSecond, IP, PORT);
            botThread[i] = new Thread(bot[i]);
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < botThread.length; i++) {
            botThread[i].start();
        }

    }

    class DoSThread implements Runnable {

        private int requestsPerSecond;
        private String IP;
        private int PORT;
        private Thread bot;

        public DoSThread(int requestsPerSecond, String IP, int PORT) {
            this.requestsPerSecond = requestsPerSecond;
            this.IP = IP;
            this.PORT = PORT;
            bot = Thread.currentThread();
        }

        @Override
        public void run() {
            while (true) {
                attack(requestsPerSecond, IP, PORT);
            }
        }

        private void attack(int requestsPerSecond, String IP, int PORT) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
