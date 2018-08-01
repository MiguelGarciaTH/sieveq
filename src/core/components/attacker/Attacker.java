///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package core.components.attacker;
//
///**
// *
// * @author miguel
// */
//public class Attacker {
//
//    private Thread exec;
//
//    public Attacker(int type, int threads) {
//        switch (type) {
//            case 1:
//                exec = new Thread(new AttackerOne(threads));
//                break;
//            case 2:
//                exec = new Thread(new AttackerTwo(threads));
//                break;
//            case 3:
//                exec = new Thread(new AttackerThree(threads));
//                break;
//        }
//
//    }
//
//    public void start() {
//        exec.run();
//    }
//
//    public void stop() {
//        exec.stop();
//    }
//}
