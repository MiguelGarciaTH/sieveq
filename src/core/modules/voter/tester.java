/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.voter;

import core.management.ServerSession;

/**
 *
 * @author miguel
 */
public class tester {

    public static void main(String[] args) {

        byte[] msg1 = new byte[]{1, 2, 3, 5};
        byte[] msg2 = new byte[]{1, 2, 3, 5, 5, 6, 7, 2, 3, 5, 5, 6, 7, 2, 3, 5, 5, 6, 7, 2, 3, 5, 5, 6, 7};

        FastestVoter vote = new FastestVoter(4, 2);
        System.out.println("1 " + vote.vote(0, msg1));
        System.out.println("2 " + vote.vote(0, msg1));
        System.out.println("3 " + vote.vote(0, msg1));
        System.out.println("4 " + vote.vote(1, msg2));
        System.out.println("4 " + vote.vote(1, msg2));

//        SimpleVoter voter = new SimpleVoter(4, 2);
//        int src = 0;
//        int dst = 1;
//        int sequenceNumber = 0;
//        int sequenceNumber2 = 2;
//        ServerSession session = new ServerSession(dst, src, new int[]{0,1,2,3}, src, true);
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
