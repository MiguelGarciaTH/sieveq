/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.experiments;

import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author miguel
 */
public class Latency implements Runnable {

    private boolean client;
    private FileWriter outputFileStream;
    private DatagramSocket socket;
    private InetAddress IPAddress;
    private DatagramPacket sendPacket;
    private Long[] sntTime;
    private Long[] rcvTime;
    private int[] rcvSeq;
    private int[] sentSeq;
    private BlockingQueue rcvQueue;
    private float avg;
    private boolean start = false;
    // private int counter = 0;
    private int ack_failure = 0, negative = 0;
    private int x, y;
    private LinkedBlockingQueue in;
    private long start_time;
    private LinkedList<Double> elapsed_secs;
    private int total = CoreProperties.messageRate * CoreProperties.experiment_rounds + CoreProperties.messageRate * CoreProperties.warmup_rounds;

    public Latency(int[] sentSeq, boolean target, Long[] sentTime, BlockingQueue queue, LinkedBlockingQueue in, String results) {
        try {
            elapsed_secs = new LinkedList<>();
            this.client = target;
            this.in = in;
            if (target) {
                System.out.println("new latency client");
                this.sntTime = sentTime;
                this.rcvTime = new Long[sentTime.length];
                this.sentSeq = sentSeq;
                this.rcvSeq = new int[sentTime.length];
                this.outputFileStream = new FileWriter(results);
                this.socket = new DatagramSocket(CoreProperties.latency_experiments_port);
                System.out.println("Client listen on port=" + CoreProperties.latency_experiments_port);
            } else {
                System.out.println("new latency server");
                this.rcvQueue = queue;
                this.socket = new DatagramSocket();
                System.out.println("Server sending to " + CoreProperties.experiments_ip + ":" + CoreProperties.latency_experiments_port);
                IPAddress = InetAddress.getByName(CoreProperties.experiments_ip);
            }
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "LatencyExperiments", ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            //client
            int r = 0;
            if (client) {
                boolean warmup = false;
                byte[] receiveData = new byte[50];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                start_time = System.nanoTime();
                while (true) {
                    socket.receive(receivePacket);
                    r++;
                    String sentence = new String(receivePacket.getData());
                    String[] seq = sentence.split(":");
                    if (seq[2].contains("ACK")) {
                        long t = System.nanoTime();
                        Integer i = Integer.valueOf(seq[0]);
//                        System.out.println("Ack received=" + i);
                        //Integer type = Integer.valueOf(seq[1]);
//                        if (type == Message.WARMUP_END) {
//                            x = i + 1;
//                            System.out.println("Index X=" + x);
//                            warmup = true;
//                        }
//                        if (warmup) {
                        rcvTime[i] = t;
                        rcvSeq[i] = i;
                        elapsed_secs.add((System.nanoTime() - start_time) / 1000000000.0);
                        //  counter++;
//                        }
                    }
                    if (seq[2].contains("END")) {
                        System.out.println("End request received");
                        socket.close();
                        break;
                    }
                    if (r == total) {
                        System.out.println("SAIU SOZINHO");
                        socket.close();
                        break;
                    }
                }
                writeFile();
                //server
            } else {
                while (true) {
                    Message m = (Message) rcvQueue.take();
//                    if (start) {
                    if (m.getType() == Message.SEND_REQUEST) {
                        //  counter++;
                        String s = m.getSeqNumber() + ":" + m.getType() + ":ACK";
                        sendPacket = new DatagramPacket(s.getBytes(), s.length(), IPAddress, CoreProperties.latency_experiments_port);
                        socket.send(sendPacket);
                    }
                    if (m.getType() == Message.END_REQUEST) {
                        String s = m.getSeqNumber() + ":" + m.getType() + ":END";
                        sendPacket = new DatagramPacket(s.getBytes(), s.length(), IPAddress, CoreProperties.latency_experiments_port);
                        socket.send(sendPacket);
                        System.out.println("Latency experiment: END REQUEST SENT");
                        break;
                    }
//                    }
//                    if (m.getType() == Message.WARMUP_END) {
//                        start = true;
//                        String s = m.getSeqNumber() + ":" + m.getType() + ":ACK";
//                        sendPacket = new DatagramPacket(s.getBytes(), s.length(), IPAddress, CoreProperties.latency_experiments_port);
//                        socket.send(sendPacket);
//                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
        }
    }

    private void writeFile() {
        System.out.println("Writing latency file");
        long write_start_time = System.nanoTime();
        try {

            int compared = 0;
            BufferedWriter out = new BufferedWriter(outputFileStream);
            out.write(" - latency(msecs)\n");
            out.flush();
            double elapsed = 0.0;
            int it = 0;
            //y = getIndex() - 1;
            x = CoreProperties.warmup_rounds * CoreProperties.messageRate;
            y = x;
            int subSize = elapsed_secs.size() - CoreProperties.warmup_rounds * CoreProperties.messageRate;
            System.out.println("total=" + subSize + " rcvTime X=" + x + " sntTime Y=" + y + " to analyze=" + Math.min(x, y));
            for (int k = 0; k < subSize; k++) {
                if (rcvTime[x] == null || sntTime[y] == null) {
                    elapsed = avg / (it + 1);
                    ack_failure++;
                } else {
                    elapsed = ((rcvTime[x] - sntTime[y]) / 1000000.0) - 0.317;  // - ping;
                }
                if (elapsed < 0) {
                    elapsed = avg / (it + 1);
                    negative++;
                }
                avg += elapsed;
                compared++;
                if (it < subSize) {
//                    System.out.println("Writing=" + elapsed + " || " + sentSeq[y] + "==" + rcvSeq[x]);
                    out.write(elapsed_secs.get(it) + " " + elapsed + " " + sentSeq[y] + "==" + rcvSeq[x] + "\n");
                    out.flush();
                    it++;
                    percentagePrint(it, subSize);
                }
                x++;
                y++;
            }
            out.close();
            outputFileStream.close();
            CoreConfiguration.print("\nAvg latency=" + avg / (it + 1) + " msecs  nulls=" + ack_failure + " negatives=" + negative + " compared=" + compared);
            double minutes = ((System.nanoTime() - write_start_time) / 1000000000.0) / 60;
            CoreConfiguration.print("Latency write finished elasped time=" + minutes + " minutes");
        } catch (IOException ex) {
            ex.printStackTrace();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "writeFile2", ex.getMessage());
        }
    }
    double history_percentage = 0;

    private void percentagePrint(int rounds, int total_file) {
        int remaining = total_file - rounds;
        double percent = 100 - ((remaining * 100) / total_file);
        if (percent % 10 == 0 && percent > history_percentage) {
            history_percentage = percent;
            System.out.print(percent + "%\t");
        }
    }

    private int getIndex() {
        Iterator<Message> t = in.iterator();
        int index = 0;
        while (t.hasNext()) {
            Message data = t.next();
            // Message m = Message.deserialize(data);
            if (data.getType() == Message.WARMUP_END) {
//                System.out.println("Index=" + index);
                return index;
            }
            index++;
        }
        return -1;
    }
}
