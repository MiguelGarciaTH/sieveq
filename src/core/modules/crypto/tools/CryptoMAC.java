/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto.tools;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.crypto.KeyStorage;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ArrayBlockingQueue;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 *
 * @author miguel
 */
public class CryptoMAC implements Runnable {

    private ArrayBlockingQueue toMac;
    private ArrayBlockingQueue macDone;
    private Mac mac;
    private SecretKey key;
    private KeyStorage ks;
    private int macSize;

    public CryptoMAC(ArrayBlockingQueue toMac, ArrayBlockingQueue macDone, String key_path) {
        this.toMac = toMac;
        this.macDone = macDone;
        this.ks = new KeyStorage(key_path, CoreProperties.algorithm);
        this.key = ks.generateSecretKeyFromFile(key_path);
        try {
            this.mac = Mac.getInstance(key.getAlgorithm());
            this.mac.init(key);
            this.macSize = this.mac.getMacLength();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "CryptoMAC()", ex.getMessage());
        }
    }

    public int macSize() {
        return this.macSize;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] data = (byte[]) toMac.take();
                macDone.add(mac.doFinal(data));
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }
    }

}
