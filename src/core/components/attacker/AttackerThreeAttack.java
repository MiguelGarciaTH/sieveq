///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package core.components.attacker;
//
//import bftsmart.communication.client.ReplyListener;
//import bftsmart.tom.ServiceProxy;
//import bftsmart.tom.core.messages.TOMMessage;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author miguel
// */
//public class AttackerThreeAttack implements Runnable, ReplyListener {
//
//    private String[] ip;
//    private int[] port;
//    private ServiceProxy proxy;
//    byte[] array;
//    int id;
//
//    public AttackerThreeAttack(String[] ip, int[] port, byte[] array, int id) {
//        this.ip = ip;
//        this.port = port;
//        this.array = array;
//        this.id = id;
//    }
//
//    @Override
//    public void run() {
//        int i = 0;
//        this.proxy = new ServiceProxy(id);
//        int[] dest = new int[]{0};
//        int k = 0;
//        while (true) {
//            try {
//                while (k++ < 10) {
////                    proxy.sendMessageToTargets(array, dest, TOMMessageType.REPLY);
//                    this.proxy.invokeAsynchronous(array, this, dest);
//                    i++;
//                    if (i % 1000 == 0) {
//                        System.out.println("sent=" + i);
//                    }
//                }
//                k=0;
//                Thread.sleep(1000);
//                
//
//            } catch (Exception ex) {
//                Logger.getLogger(AttackerThreeAttack.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
//
////    @Override
////    public void replyReceived(TOMMessage reply) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////    }
//
////    @Override
////    public void replyReceived(RequestContext context, TOMMessage reply) {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////    }
//
//    @Override
//    public void replyReceived(TOMMessage reply) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//}
