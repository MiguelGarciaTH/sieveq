/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import core.management.Message;

/**
 *
 * @author miguel
 */
public class tester {

    public static void main(String[] args) {
        byte[] teste = new byte[]{1, 2, 3, 5};
        boolean t = Message.hasCrypto(teste);
        System.out.println("T=" + t);
        teste = new byte[]{0, 2, 3, 5};
        t = Message.hasCrypto(teste);

        System.out.println("T=" + t);
//        Message m;
//        LinkedList<Message> list = new LinkedList<Message>();
//        //        m = new Message(0, 1, 0, new byte[]{1});
//        
//        list.add(m);
//        for (Message messages : list) {
//            System.out.println("m=>" + m);
//        }
//        System.out.println("------------------");
//        m = new Message(1, 2, 2, new byte[]{1});
//        
//        list.add(m);
//         m = new Message(1, 3, 3, new byte[]{1});
//         list.add(m);
//        for (Message messages : list) {
//            System.out.println("m=>" + m);
//        }
//        System.out.println("------------------");
    }
}
