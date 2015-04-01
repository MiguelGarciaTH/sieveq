///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package core.modules.voter;
//
//import core.management.ServerSession;
//import java.util.HashMap;
//import java.util.concurrent.ConcurrentHashMap;
//import orestes.bloomfilter.CBloomFilter;
//
///**
// *
// * @author miguel
// */
//public class BloomFilterVoter extends Voter {
//
//    private HashMap<Integer, CBloomFilter> votes;
//    private HashMap<Integer, HashMap<Integer, Integer>> counterVotes;
//    private HashMap<Integer, Integer> sequences;
//    private int quorom;
//    private CBloomFilter bf;
//    private double bloomFilterSize = 1000000;
//    private double falsePositiveRate = 0.99;
//    private int countingBits = 4;
//
//    public BloomFilterVoter(int quorom) {
//        this.quorom = quorom;
//        this.counterVotes = new HashMap<>();
//        this.votes = new HashMap<>();
//    }
//
//    @Override
//    public boolean vote(int src, int sequenceNumber, byte[] message) {
//        try {
//            bf = votes.get(src);
//            bf.getC();
//        } catch (Exception e) {
//            bf = new CBloomFilter(bloomFilterSize, falsePositiveRate, countingBits);
//            votes.put(src, bf);
//            bf = votes.get(src);
//        }
//        if (bf.contains(message)) {
//            if (hasQuorom(src, sequenceNumber)) {
//                return true;
//            }
//            if (toRemove(src, sequenceNumber)) {
//                bf.remove(message);
//                return false;
//            }
//            return false;
//        } else {
//            bf.add(message);
//            createCounterVotes(src, sequenceNumber);
//            return false;
//        }
//    }
//
//    private void createCounterVotes(int src, int seq_numb) {
//        try {
//            sequences = counterVotes.get(src);
//            sequences.put(seq_numb, 1);
//        } catch (Exception e) {
//            sequences = new HashMap<Integer, Integer>();
//            sequences.put(seq_numb, 1);
//        }
//        counterVotes.put(src, sequences);
//    }
//
//    private boolean hasQuorom(int src, int seq_numb) {
//        sequences = counterVotes.get(src);
//        int t = sequences.get(seq_numb).intValue();
//        t++;
//        sequences.put(seq_numb, t);
//        return (sequences.get(seq_numb) == quorom);
//    }
//
//    private boolean toRemove(int src, int seq_numb) {
//        try {
//            sequences = counterVotes.get(src);
//            if (sequences.get(seq_numb) == quorom + 1) {
//                votes.get(src).remove(seq_numb);
//                counterVotes.get(src).remove(seq_numb);
//                return true;
//            }
//            return false;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public boolean vote(int src, ServerSession session, int sequenceNumber, byte[] message) {
//        try {
//            bf = votes.get(src);
//            bf.getC();
//        } catch (Exception e) {
//            bf = new CBloomFilter(bloomFilterSize, falsePositiveRate, countingBits);
//            votes.put(src, bf);
//            bf = votes.get(src);
//        }
//        if (bf.contains(message)) {
//            if (hasQuorom(src, sequenceNumber)) {
//                return true;
//            }
//            if (toRemove(src, sequenceNumber)) {
//                bf.remove(message);
//                return false;
//            }
//            return false;
//        } else {
//            bf.add(message);
//            createCounterVotes(src, sequenceNumber);
//            return false;
//        }
//    }
//
//    @Override
//    public boolean vote(int type, int src, ConcurrentHashMap<Integer, ServerSession> sessions, int sequenceNumber, byte[] message) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//}
