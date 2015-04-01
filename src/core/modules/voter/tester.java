/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.voter;

/**
 *
 * @author miguel
 */
public class tester {

    public static void main(String[] args) {
        
        byte[] data1= new byte[]{1,2,3,5};
        System.out.println("data1 " + data1.length);
        
        
        data1 = new byte[]{1,2,3,5,5,6,7,2,3,5,5,6,7,2,3,5,5,6,7,2,3,5,5,6,7};
        System.out.println("data1 " + data1.length);
//        byte[] msg = new byte[]{1, 2};
//        byte[] msg2 = new byte[]{1, 3};
//        SimpleVoter voter = new SimpleVoter(4, 2);
//        int src = 0;
//        int dst = 1;
//        int sequenceNumber = 0;
//        int sequenceNumber2 = 2;
//        Session session = new Session(dst, 1);
//
//
//        for (int i = 0; i < 10; i++) {
//            System.out.println("vote=" + voter.vote(src, session, sequenceNumber, msg));
//            System.out.println("vote=" + voter.vote(src, session, sequenceNumber, msg));
//            System.out.println("vote=" + voter.vote(src, session, sequenceNumber, msg));
//            System.out.println("vote=" + voter.vote(src, session, sequenceNumber, msg));
//            System.out.println("vote=" + voter.vote(src, session, sequenceNumber, msg));
//            System.out.println("vote=" + voter.vote(src, session, sequenceNumber, msg));
//            System.out.println("vote=" + voter.vote(src, session, sequenceNumber, msg));
//            System.out.println("______________________________________________________________");
//        }
    }
}
