///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
//
// */
//package core.components.attacker;
//
////import bftsmart.communication.ServerCommunicationSystem;
//import bftsmart.communication.SystemMessage;
//import bftsmart.tom.ServiceReplica;
//import bftsmart.tom.core.messages.TOMMessage;
//import core.management.CoreProperties;
//import java.util.Random;
//import java.util.Scanner;
//
///**
// *
// * @author miguel
// */
//public class ReplicaDoS implements Runnable {
//
////    private AttackerTwo attack;
//    private Thread t;
////    private ServerCommunicationSystem comm;
//    private byte[] payload;
//    private int[] dst;
//    private TOMMessage msg;
//    private ServiceReplica replica;
//    private Thread[] attackers;
////    private ServerViewManager svm;
//
//    public ReplicaDoS(ServiceReplica replica, int numberOfThreads, int reqs, String ip, int port, Thread t) {
//        this.t = t;
//        this.comm = replica.getReplicaContext().getServerCommunicationSystem();
//        this.payload = new byte[100];
//        Random n = new Random();
//        n.nextBytes(payload);
//        this.replica = replica;
////        this.svm = replica.getSVManager();
//        this.dst = new int[]{CoreProperties.targetID};
//        attackers = new Thread[CoreProperties.attack_threads];
//        for (int i = 0; i < CoreProperties.attack_threads; i++) {
//            attackers[i] = new Thread(new Attack());
//        }
//
//    }
//
//    @Override
//    public void run() {
//        System.out.println("Waiting for another enter to start! ");
//        Scanner n = new Scanner(System.in);
//        n.next();
//        t.interrupt();
//        for (int i = 0; i <= CoreProperties.attack_threads; i++) {
//            attackers[i].start();
//        }
//    }
//
//    class Attack implements Runnable {
//
//        SystemMessage sm;
//        int it = 0;
//        
//        Attack() {
////            sm = new SMMessage(svm.getStaticConf().getProcessId(),
////                svm.getTomLayer().getStateManager().getWaiting(), TOMUtil.SM_REQUEST, replica.getID(), null, null, -1, -1);
////       
//           // sm = new SMMessage(1, 0, TOMUtil.SYNC, 0, null, null, 0, 0);
////            sm = new PaxosMessage(MessageFactory.PROPOSE, CoreProperties.targetID, it, 1);
////            msg = new TOMMessage(1, 0, 0, payload, 0);
////            sm = new ForwardedMessage(it, msg);
//        }
//
//        @Override
//        public void run() {
//            while (true) {
//                comm.send(dst, sm);
//                if (it++ == 10000) {
//                    System.out.print(" x");
//                    it = 0;
//                }
//            }
//        }
//
//    }
//
//}
