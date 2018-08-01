///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package core.components.attacker;
//
//import bftsmart.communication.client.ReplyListener;
//
//import bftsmart.tom.core.messages.TOMMessage;
//import com.google.common.util.concurrent.RateLimiter;
//import core.management.CoreConfiguration;
//import core.management.CoreProperties;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.util.Random;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author miguel
// */
//public class AttackerTwo implements Runnable {
//
//    private final String ip;
//    private final int port;
//    private CoreProperties prop;
//    static int i = 0;
//    private final int threads;
//    private final Attack[] attack;
//    private int rate;
//
//    public AttackerTwo(int threads) {
//        this.ip = prop.ip;
//        this.port = prop.destiny_port;
//        this.threads = threads;
//
//        this.attack = new Attack[threads];
//        for (int j = 0; j <= threads; j++) {
//            System.out.println("");
//            attack[j] = new Attack(10, rate, prop.message_size, prop.target_ip, prop.target_port);
//        }
//
//    }
//
//    public AttackerTwo(int threads, int limit, String ip, int port) {
//        this.ip = prop.ip;
//        this.port = prop.destiny_port;
//        this.threads = threads;
//        System.out.println("Threads=" + threads);
//        this.attack = new Attack[threads];
//        for (int j = 0; j < threads; j++) {
//            attack[j] = new Attack(j, limit, prop.message_size, prop.target_ip, prop.target_port);
//            System.out.println("thread=" + j + "done");
//        }
//
//    }
//
//    @Override
//    public void run() {
//        try {
//            for (int j = 0; j < threads; j++) {
//                new Thread(attack[j]).start();
//            }
//        } catch (Exception ex) {
//            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run() do start thread", ex.getMessage());
////            new AttackerTwo(threads, rate, ip, port).run();
//        }
//    }
//
//    class Attack implements Runnable, ReplyListener {
//
//        private int rate;
//        private byte[] array;
//        private String ip;
//        private int port;
//        DatagramSocket socket = null;
//        InetAddress host;
//        DatagramPacket dp;
//        private int id;
//
//        public Attack(int id, int limit, int size, String ip, int port) {
//            this.id = id;
//            this.port = port;
//            this.ip = ip;
//            this.rate = limit;
//            this.array = new byte[size];
//            new Random().nextBytes(array);
//            try {
//                socket = new DatagramSocket();
//                host = InetAddress.getByName(this.ip);
//                dp = new DatagramPacket(array, array.length, host, port);
//            } catch (Exception ex) {
//                System.out.println("No constructor ATTACK() ");
////                ex.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            RateLimiter rateLimiter = RateLimiter.create(rate);
//            try {
//                while (true) {
////                    System.out.print(" ["+id+"] ");
////                    rateLimiter.acquire();
//                    socket.send(dp);
//                }
//            } catch (Exception ex) {
//                try {
//                    if (socket != null) {
//                        socket.close();
//                    }
//                    CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
//                    new Attack(id, rate, prop.message_size, ip, port).run();
//                } catch (Exception ex1) {
//                    Logger.getLogger(AttackerTwo.class.getName()).log(Level.SEVERE, null, ex1);
//                }
//            }
//        }
//
//        @Override
//        public void replyReceived(TOMMessage reply) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
////        @Override
////        public void replyReceived(RequestContext context, TOMMessage reply) {
////            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////        }
//    }
//}
