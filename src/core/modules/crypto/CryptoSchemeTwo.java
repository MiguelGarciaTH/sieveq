/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 * This crypto scheme established authentication between the client and each replica. Client HMACs the message with 3f+1 HMACs, and each filter verifies its own HMAC.
 *
 * roles affected: client and
 *
 * @author miguel
 */
public class CryptoSchemeTwo extends CryptoScheme {

    private byte[] local_data;
    private byte[] local_mac;
    private ArrayList<SecretKey> keys;
    private ArrayList<Mac> macs;
    private ArrayList<KeyStorage> kss;
    private int macSize = 0, offset;
    private ByteBuffer complete;

    private SecretKey key;
    private Mac mac;
    private KeyStorage ks;

    public CryptoSchemeTwo() {
        this.complete = ByteBuffer.allocate(5000);
        description = "off";
        try {
            if (CoreConfiguration.role.equals("replica")) {
                description = "on";
                this.ks = new KeyStorage(CoreProperties.shared_key_path + CoreConfiguration.ID, CoreProperties.algorithm);
                this.key = ks.generateSecretKeyFromFile(CoreProperties.shared_key_path + CoreConfiguration.ID);
                this.mac = Mac.getInstance(key.getAlgorithm());
                this.mac.init(key);
                this.macSize = this.mac.getMacLength();
                this.offset = (CoreConfiguration.ID * macSize);
            }
            if (CoreConfiguration.role.equals("client")) {
                description = "on";
                keys = new ArrayList<>();
                macs = new ArrayList<>();
                kss = new ArrayList<>();
                for (int i = 0; i < CoreProperties.num_replicas; i++) {
                    KeyStorage ksi = new KeyStorage(CoreProperties.shared_key_path + i, CoreProperties.algorithm);
                    kss.add(ksi);
                    keys.add(ksi.generateSecretKeyFromFile(CoreProperties.shared_key_path + i));
                    Mac keysi = Mac.getInstance(keys.get(i).getAlgorithm());
                    macs.add(keysi);
                    macs.get(i).init(keys.get(i));
                }
                this.macSize = this.macs.get(0).getMacLength();
            }
            this.local_mac = new byte[macSize];
        } catch (InvalidKeyException | NoSuchAlgorithmException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "CryptoSchemeTwo()", ex.getMessage());
        }
    }

    private byte[] calculateMAC(byte[] data, int index) {
        return macs.get(index).doFinal(data);
    }

    private byte[] calculateMAC(byte[] data) {
        return mac.doFinal(data);
    }

    @Override
    public byte[] clientSecureMessage(byte[] data, int size) {
        complete.clear();
        complete.put(data, 0, size);
        byte[] data2 = new byte[size];
        complete.position(0);
        complete.get(data2);
        complete.position(size);
        for (int i = 0; i < CoreProperties.num_replicas; i++) {
            byte[] temp_mac = calculateMAC(data2, i);
            complete.put(temp_mac);
        }
        complete.position(0);
        complete.get(data, 0, (size + (macSize * CoreProperties.num_replicas)));
        return local_data;
    }

    @Override
    public boolean filterVerifyMessage(byte[] data, int size) {
        complete.clear();
        complete.put(data, 0, size);
        int dataSize = Math.abs(size - (macSize * CoreProperties.num_replicas));
        local_data = new byte[dataSize];
        complete.position(0);
        complete.get(local_data);
        complete.position(dataSize + offset);
        complete.get(local_mac);
        byte[] temp_mac = calculateMAC(local_data);
        return Arrays.equals(temp_mac, local_mac);
    }

    @Override
    public boolean clientVerifyMessage(byte[] data) {
        return true;
    }

    @Override
    public byte[] prefilterSecureMessage(byte[] data) {
        return data;
    }

    @Override
    public boolean prefilterVerifyMessage(byte[] data) {
        return true;
    }

    @Override
    public byte[] filterSecureMessage(byte[] data) {
        return data;
    }

    @Override
    public byte[] serverSecureMessage(byte[] data) {
        return data;
    }

    @Override
    public boolean serverVerifyMessage(byte[] data) {
        return true;
    }

    @Override
    public byte[] clientSecureMessage(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean prefilterVerifyMessage(byte[] data, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean filterVerifyMessage(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
