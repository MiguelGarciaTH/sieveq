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
//import core.management.CoreProperties;
//import java.net.Socket;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.Random;
//
///**
// *
// * @author miguel
// */
//public class AttackerThree implements Runnable, ReplyListener {
//
//    private String[] ip;
//    private int[] port;
//    private Socket[] socket;
//    private CoreProperties prop;
//    static int i = 0;
//    ServiceProxy proxy;
//    ByteBuffer buf = ByteBuffer.allocate(100).order(ByteOrder.BIG_ENDIAN);
//    private int threads;
//
//    public AttackerThree(int threads) {
////        try {
//        this.threads = threads;
////            proxy = new ServiceProxy(8);
////            String sCurrentLine;
////            ip = new String[4];
////            port = new int[4];
////            socket = new Socket[4];
////            BufferedReader br = new BufferedReader(new FileReader("config/hosts.config"));
////            int idx = 0;
////            br.readLine();
////            while ((sCurrentLine = br.readLine()) != null) {
////                String[] a = sCurrentLine.split(" ");
////                ip[idx] = a[1];
////                port[idx] = Integer.parseInt(a[2]);
////                idx++;
////                if (idx == 3) {
////                    break;
////                }
////            }
////        } catch (IOException ex) {
////            Logger.getLogger(AttackerThree.class.getName()).log(Level.SEVERE, null, ex);
////        }
//
//    }
//
//    @Override
//    public void run() {
//        int i = 0;
//        Random r = new Random();
//        byte[] array = new byte[4];
//        r.nextBytes(array);
//        int k = 7;
//        while (i++ < threads) {
//            new Thread(new AttackerThreeAttack(ip, port, array, k++)).start();
//        }
//        System.out.println("Attack started...");
////        int k = 0;
////        int[] processes = new int[]{0};
////        byte[] payload = new byte[]{5, 1};
////        byte[] data = new byte[100];
////        Message mal = new Message(15, 0, 0, payload);
////        int size = mal.serialize(data, buf);
////        ByteArrayWrap wrapper = new ByteArrayWrap(data, size);
////        wrapper.serialize(buf, data, size);
////        int i = 0;
////        while (true) {
////            System.out.println("i=" + i++);
//////            proxy.invokeAsynchronous(wrapper.getArr(), this, processes);
////            while (k < 4) {
////                try {
////                    socket[k] = new Socket(ip[k], port[k]);
////                    if (socket[k].isConnected()) {
////                        System.out.println("Connected= " + ip[k] + ":" + port[k]);
////                    }
////                    socket[k].close();
////                    k++;
////                } catch (Exception ex) {
////                    CoreConfiguration.printException(this.getClass().getCanonicalName(), "run()", ex.getMessage());
////                }
////            }
////            k = 0;
////        }
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
//}
