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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author miguel
 */
public class ThroughputExperiments implements Runnable {

    private BlockingQueue experimentQueue;
    private long globalTimeInit, globalTimeFinit;
    private ArrayList<Integer> messagesPerSec;
    private RemindTaskTwo taskTwo;
    private int numberofMessages=0, sum = 0;
    private Timer timer2;
    private Message message;
    private boolean target;
    private FileWriter outputFileStream;
    private DatagramSocket socket;
    private InetAddress IPAddress;
    private DatagramPacket sendPacket;

    public ThroughputExperiments(boolean target, BlockingQueue experimentQueue, String results) {
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
        try {
            BufferedWriter out = new BufferedWriter(outputFileStream);
            int i = 0;
            out.write(" - msg/sec \n");
            for (Integer t : messagesPerSec) {
                try {
                    out.write(i++ + " " + t + "\n");
                    sum += t;
                    out.flush();
                } catch (IOException ex) {
                    CoreConfiguration.printException(this.getClass().getCanonicalName(), "writeFile", ex.getMessage());
                }
            }
            out.close();
            outputFileStream.close();
            System.out.println("");
            double elapsedTime = ((double) (globalTimeFinit - globalTimeInit) / 1000000000.0);
            CoreConfiguration.print("[Exp]Throughput write finished");
            CoreConfiguration.print("[Exp]Total msg (sum)=" + sum);
            CoreConfiguration.print("[Exp]Elapsed time=" + elapsedTime + " secs");
            CoreConfiguration.print("[Exp]Sum/elapsed =" + sum / elapsedTime);

        } catch (IOException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "writeFile2", ex.getMessage());
        }
    }

    class RemindTaskTwo extends TimerTask {

        int i = 0;

        RemindTaskTwo() {
        }

        @Override
        public void run() {
            
            synchronized (this) {
//                System.out.println("experiment: #messages=" + numberofMessages + " second=" + i++);
                messagesPerSec.add(numberofMessages);
                numberofMessages = 0;
            }
        }
    }

}
