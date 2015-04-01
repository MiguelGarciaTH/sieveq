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
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author miguel
 */
public class LatencyExperimentsSelective implements Runnable {

    private boolean client;
    private FileWriter outputFileStream;
    private DatagramSocket socket;
    private InetAddress IPAddress;
    private DatagramPacket sendPacket;
    private LinkedList<Long> sntTime;
    private LinkedList<Long> rcvTime;
    private BlockingQueue rcvQueue;
    private float avg;
    private boolean start = false;
    private int counter = 0;
    private int ack_failure = 0, negative = 0;
    private int x, y;
    private LinkedBlockingQueue in;
    private int acks = 0;
    private int j = 0;

    private long start_time = 0;

    public LatencyExperimentsSelective(boolean target, LinkedList<Long> sentTime, BlockingQueue queue, LinkedBlockingQueue in, String results) {
        try {
            this.client = target;
            this.in = in;
            if (target) {
                this.sntTime = sentTime;
                this.rcvTime = new LinkedList<Long>();
                this.outputFileStream = new FileWriter(results);
                this.socket = new DatagramSocket(CoreProperties.latency_experiments_port);
            } else {
                this.rcvQueue = queue;
                this.socket = new DatagramSocket();
                IPAddress = InetAddress.getByName(CoreProperties.experiments_ip);
            }
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "LatencyExperiments", ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            if (client) {
                byte[] receiveData = new byte[50];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                start_time = System.nanoTime();
                while (true) {
                    socket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    String[] seq = sentence.split(":");
                    if (seq[2].contains("ACK")) {
                        rcvTime.add(System.nanoTime());
                        counter++;
                    }
                    if (seq[2].contains("END")) {
                        System.out.println("End request received");
                        socket.close();
                        break;
                    }
                }
                writeFile();
            } else {
                while (true) {
                    Message m = (Message) rcvQueue.take();
                    if (m.getType() == Message.SEND_REQUEST) {
                        acks++;
                        counter++;
                        if (acks == 10) {
                            String s = m.getSeqNumber() + ":" + m.getType() + ":ACK";
                            sendPacket = new DatagramPacket(s.getBytes(), s.length(), IPAddress, CoreProperties.latency_experiments_port);
                            socket.send(sendPacket);
                            acks = 0;
                        }
                    }
                    if (m.getType() == Message.END_REQUEST) {
                        String s = m.getSeqNumber() + ":" + m.getType() + ":END";
                        sendPacket = new DatagramPacket(s.getBytes(), s.length(), IPAddress, CoreProperties.latency_experiments_port);
                        socket.send(sendPacket);
                        System.out.println("END REQUEST SENT");
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
        }
    }

    private void writeFile() {
        try {
            int compared = 0;
            BufferedWriter out = new BufferedWriter(outputFileStream);
            out.write(" - latency(msecs)\n");
            out.flush();
            double elapsed = 0.0;
            int it = 0;
            x = 0;
            y = 0;
            System.out.println("sent Len=" + sntTime.size() + " rcv len=" + rcvTime.size());
            //System.out.println("rcvTime X=" + x + " sntTime Y=" + y + " to analyze=" + CoreProperties.messageRate * CoreProperties.experiment_rounds);
            for (int k = 0; k < Math.min(sntTime.size(), rcvTime.size()); k++) {
                if (rcvTime.get(x) == null || sntTime.get(y) == null) {
                    elapsed = avg / (it + 1);
                    ack_failure++;
                } else {
                    //System.out.println("elapsed" + rcvTime.get(x) + "-" + sntTime.get(y));
                    elapsed = (((rcvTime.get(x) - sntTime.get(y)) / 1000000.0)) / 10;  // - ping;
                }
                if (elapsed < 0) {
                    elapsed = avg / (it + 1);
                    negative++;
                }
                x++;
                y++;
                avg += elapsed;
                compared++;
                for (int l = 0; l < 10; l++) {
                    it++;
                    double time_now = (System.nanoTime() - start_time) / 1000000000.0;
                    out.write(time_now + " " + elapsed + "\n");
                    out.flush();
                }
            }

            out.close();
            outputFileStream.close();
            CoreConfiguration.print("Avg latency=" + avg / (it + 1) + " msecs  nulls=" + ack_failure + " negatives=" + negative + " compared=" + compared);
            CoreConfiguration.print("Latency write finished");
        } catch (IOException ex) {
            ex.printStackTrace();
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "writeFile2", ex.getMessage());
        }
    }

//    private int getIndex() {
//        Iterator<Message> t = in.iterator();
//        int index = 0;
//        while (t.hasNext()) {
//            Message data = t.next();
//            // Message m = Message.deserialize(data);
//            if (data.getType() == Message.WARMUP_END) {
//                return index + 1;
//            }
//            index++;
//        }
//        return -1;
//    }
}
