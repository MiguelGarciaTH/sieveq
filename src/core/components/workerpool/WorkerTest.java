/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.crypto.CryptoSchemeFive;
import core.modules.crypto.tester;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
class WorkerTest extends Worker {

    KeyPairGenerator keyGen;
    KeyPair keyPair;
    Signature signature;
    Random r;

    CryptoSchemeFive crypto;
    CoreProperties prop;
    CoreConfiguration conf;

    private RemindTask task;
    private int numberofMessages = 0;
    private Timer timer;

    public WorkerTest(int tid, ArrayBlockingQueue in, ArrayBlockingQueue out) {
        super(tid, in, out);
        try {
            prop = CoreProperties.getProperties("config/client.properties", "client");
            conf = CoreConfiguration.getConfiguration(7, "client");

//            task = new RemindTask();
//            timer = new Timer();
//            timer.schedule(task, 0, 1000); //delay in milliseconds
            byte[] data = new byte[1000];
            Random r = new Random();
            r.nextBytes(data);
            crypto = new CryptoSchemeFive();
            int i = 0;
            while (i < 5000) {
                crypto.serverSecureMessage(data);
                i++;
            }
            i = 0;
            long timeI = System.nanoTime();
            while (i < 1000) {
                crypto.serverSecureMessage(data);
                i++;
            }
            long timeF = System.nanoTime();
            System.out.println("Elapsed=" + (timeF - timeI) / 1000000.0 + " msecs");

//            keyGen = KeyPairGenerator.getInstance("RSA");
//            keyGen.initialize(1024);
//            KeyPair keyPair = keyGen.generateKeyPair();
//            signature = Signature.getInstance("SHA256withRSA");
//            signature.initSign(keyPair.getPrivate());
        } catch (Exception ex) {
            System.out.println("EX==>" + ex.getMessage());
        }

    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] data = (byte[]) in.take();

                byte[] signedBytes = crypto.clientSecureMessage(data);
//                signature.update(data);
//                byte[] signedBytes= signature.sign();
                numberofMessages++;
                out.put(signedBytes);
            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.getLogger(tester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class RemindTask extends TimerTask {

        RemindTask() {
        }

        @Override
        public void run() {
            System.out.println("TID=" + tid + " messages=" + numberofMessages);
            numberofMessages = 0;
        }
    }
}
