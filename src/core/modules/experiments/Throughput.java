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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author miguel
 */
public class Throughput implements Runnable {

    private BlockingQueue experimentQueue;
    private long globalTimeInit, globalTimeFinit;
    private ArrayList<Integer> messagesPerSec;
    private RemindTaskTwo taskTwo;
    private int numberofMessages = 0, sum = 0;
    private Timer timer2;
    private Message message;
    private boolean target;
    private FileWriter outputFileStream;
    private DatagramSocket socket;
    private InetAddress IPAddress;
    private DatagramPacket sendPacket;

    public Throughput(boolean target, BlockingQueue experimentQueue, String results) {
        try {
            this.target = target;
            if (target) {
                this.experimentQueue = experimentQueue;
                this.outputFileStream = new FileWriter(results);
                messagesPerSec = new ArrayList<>();
                socket = new DatagramSocket();
                IPAddress = InetAddress.getByName(CoreProperties.experiments_ip);
                sendPacket = new DatagramPacket("END".getBytes(), "END".length(), IPAddress, CoreProperties.throughput_experiments_port);
            } else {
                socket = new DatagramSocket(CoreProperties.throughput_experiments_port);
            }
        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "ThroughputExperiments", ex.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            if (target) {
                taskTwo = new RemindTaskTwo();
                timer2 = new Timer();
                timer2.schedule(taskTwo, 0, 1000); //delay in milliseconds
                experimentQueue.take();
                globalTimeInit = System.nanoTime();
                while (true) {
                    message = (Message) experimentQueue.take();
                    numberofMessages++;
                    if (message.getType() == Message.END_REQUEST) {
                        globalTimeFinit = System.nanoTime();
                        System.out.println("Experiment end request sent!!!!!");
                        socket.send(sendPacket);
                        if (timer2 != null) {
                            timer2.cancel();
                        }
                        break;
                    }
                }
                writeFile();
            } else {
                globalTimeInit = System.nanoTime();
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                while (true) {
                    socket.receive(receivePacket);
                    String sentence = new String(receivePacket.getData());
                    if (sentence.equals("END")) {
                        globalTimeFinit = System.nanoTime();
                        break;
                    }
                }
                CoreConfiguration.print("Elapsed time=" + (double) (globalTimeFinit - globalTimeInit) / 1000000000.0 + " secs");
            }
        } catch (InterruptedException | IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
        }

    }

    private void writeFile() {
        System.out.println("[Experiments] writing file results");
        try {
            BufferedWriter out = new BufferedWriter(outputFileStream);
            int i = 0;
            out.write(" - msg/sec \n");
      
            List<Integer> sublist = messagesPerSec.subList(CoreProperties.warmup_rounds+1, messagesPerSec.size());
//            System.out.println("init="+CoreProperties.warmup_rounds);
//            System.out.println("end="+sublist.size() +" before="+messagesPerSec.size());
            for (Integer t : sublist) {
                try {
                    out.write(i + " " + t + "\n");
                    out.flush();
                    percentagePrint(i, sublist.size());
                    i++;
                } catch (IOException ex) {
                    CoreConfiguration.printException(this.getClass().getCanonicalName(), "writeFile", ex.getMessage());
                }
            }
            out.close();
            outputFileStream.close();
            double elapsedTime = ((double) (globalTimeFinit - globalTimeInit) / 1000000000.0);
            CoreConfiguration.print("[Experiments] write finished");
            CoreConfiguration.print("[Exp]Elapsed time=" + elapsedTime + " secs");
        } catch (IOException ex) {
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

    class RemindTaskTwo extends TimerTask {


        RemindTaskTwo() {
        }

        @Override
        public void run() {

//            synchronized (this) {
                messagesPerSec.add(numberofMessages);
                sum += numberofMessages;
                numberofMessages = 0;

//            }
        }
    }

}
