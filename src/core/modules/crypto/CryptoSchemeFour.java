/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 * This crypto scheme established authentication between the client and each replica. Client HMACs the message with 3f+1 HMACs, and each replica verifies its own HMAC. Also, the client signs each
 * message, and server verifies the signature.
 *
 * roles affected: client and replicas and server
 *
 * @author miguel
 */
public class CryptoSchemeFour extends CryptoScheme {

    private byte[] local_data, local_mac, local_signature, total_data;
    private int macSize = 0, signatureSize = 0;
    private ByteBuffer complete;

    private PrivateKey priv;
    private PublicKey pub;
    private Signature signer;
    private SecretKey key;
    private ArrayList<SecretKey> keys;
    private Mac mac;
    private ArrayList<Mac> macs;
    private KeyStorage ks;
    private ArrayList<KeyStorage> kss;

    public CryptoSchemeFour() {
        this.complete = ByteBuffer.allocateDirect(10000);
        description = "off";
        try {
            this.ks = new KeyStorage();
            switch (CoreConfiguration.role) {
                case "client":
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
                    this.local_signature = new byte[signatureSize];
                    this.priv = ks.loadPrivateKey(CoreProperties.private_key_path);
                    this.pub = ks.loadPublicKey(CoreProperties.public_key_path);
                    this.signer = Signature.getInstance("SHA256withRSA");
                    break;
                case "replica":
                    description = "on";
                    this.ks = new KeyStorage(CoreProperties.shared_key_path + CoreConfiguration.ID, CoreProperties.algorithm);
                    this.complete = ByteBuffer.allocate(1000000);
                    this.key = ks.generateSecretKeyFromFile(CoreProperties.shared_key_path + CoreConfiguration.ID);
                    this.mac = Mac.getInstance(key.getAlgorithm());
                    this.mac.init(key);
                    this.macSize = this.mac.getMacLength();
                    break;
                case "server":
                    description = "on";
                    this.local_signature = new byte[signatureSize];
                    this.priv = ks.loadPrivateKey(CoreProperties.private_key_path);
                    this.pub = ks.loadPublicKey(CoreProperties.public_key_path);
                    this.signer = Signature.getInstance("SHA256withRSA");
                    this.macSize = CoreProperties.hmac_key_size;
                    break;
            }
        } catch (Exception e) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "CryptoSchemeFour()", e.getMessage());
        }
    }

    @Override
    public byte[] clientSecureMessage(byte[] data) {
        try {
            complete.position(0);
            signer.initSign(priv);
            signer.update(data);
            local_signature = signer.sign();
            complete.put(data);
            complete.put(local_signature);
            for (int i = 0; i < CoreProperties.num_replicas; i++) {
                byte[] temp_mac = calculateMAC(data, i);
                complete.put(temp_mac);
            }
            complete.position(0);

            total_data = new byte[data.length + macSize * CoreProperties.num_replicas + signatureSize];
            complete.get(total_data);
            return total_data;
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "clientSecureMessage()", ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean filterVerifyMessage(byte[] data) {
        complete.clear();
        complete.put(data);
        int offset = (CoreConfiguration.ID * macSize);
        int dataSize = data.length - (macSize * CoreProperties.num_replicas) - signatureSize;
        local_mac = new byte[macSize];
        local_data = new byte[dataSize];
        complete.position(0);
        complete.get(local_data);
        complete.position(dataSize + signatureSize + offset);
        complete.get(local_mac);
        byte[] mac2 = calculateMAC(local_data);
        return Arrays.equals(mac2, local_mac);
    }

    @Override
    public boolean serverVerifyMessage(byte[] data) {
        try {
            splitData(data);
            signer.initVerify(pub);
            signer.update(local_data);
            return (signer.verify(local_signature));
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "serverVerifyMessage()", ex.getMessage());
        }
        return false;
    }

    private void splitData(byte[] data) {
        complete.clear();
        complete.put(data);
        complete.position(0);
        local_data = new byte[data.length - (signatureSize + (macSize * CoreProperties.num_replicas))];
        complete.get(local_data);
        complete.get(local_signature);
    }

    private byte[] calculateMAC(byte[] data, int index) {
        return macs.get(index).doFinal(data);
    }

    private byte[] calculateMAC(byte[] data) {
        return mac.doFinal(data);
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
    public byte[] clientSecureMessage(byte[] data, int data_size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean prefilterVerifyMessage(byte[] data, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean filterVerifyMessage(byte[] data, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
