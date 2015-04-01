/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto;

import core.message.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;

/**
 * The workshop version - the final version. Client HMAC with Pre-filter, and the client signs for Filters.
 *
 * @author miguel
 */
public class CryptoSchemeFive extends CryptoScheme {

    private byte[] local_data, local_mac, local_signature, mac2;
    private ByteBuffer complete;
    private PrivateKey priv;
    private PublicKey pub;
    private Signature signer;
    private SecretKey key;
    private Mac mac;
    private KeyStorage ks;
    private int signatureSize, macSize;

    public CryptoSchemeFive() {
        description = "off";
        signatureSize = CoreProperties.signature_key_size;
        try {
            this.ks = new KeyStorage();
            switch (CoreConfiguration.role) {
                case "client":
                    this.description = "on";
                    this.ks = new KeyStorage(CoreProperties.shared_key_path + CoreConfiguration.ID, CoreProperties.algorithm);
                    this.key = ks.generateSecretKeyFromFile(CoreProperties.shared_key_path + CoreConfiguration.ID);
                    this.mac = Mac.getInstance(key.getAlgorithm());
                    this.mac.init(key);
                    this.macSize = this.mac.getMacLength();
                    this.local_signature = new byte[signatureSize];
                    this.priv = ks.readPrivateKeyFromFile(CoreProperties.private_key_path);
                    this.signer = Signature.getInstance("SHA256withRSA");
                    this.signer.initSign(priv);
                    complete = ByteBuffer.allocate(Message.HEADER_SIZE + CoreProperties.message_size + signatureSize + macSize);
                    break;
                case "prereplica":
                    this.description = "on";
                    this.ks = new KeyStorage(CoreProperties.shared_key_path + CoreConfiguration.ID, CoreProperties.algorithm);
                    this.key = ks.generateSecretKeyFromFile(CoreProperties.shared_key_path + CoreConfiguration.ID);
                    this.mac = Mac.getInstance(key.getAlgorithm());
                    this.mac.init(key);
                    this.macSize = this.mac.getMacLength();
                    this.mac2 = new byte[macSize];
                    this.local_mac = new byte[macSize];
                    complete = ByteBuffer.allocate(Message.HEADER_SIZE + CoreProperties.message_size + signatureSize + macSize + 4);
                    break;
                case "replica":
                    this.description = "on";
                    this.local_signature = new byte[signatureSize];
                    this.priv = ks.loadPrivateKey(CoreProperties.private_key_path);
                    this.pub = ks.readPublicKeyFromFile(CoreProperties.public_key_path);
                    this.signer = Signature.getInstance("SHA256withRSA");
                    this.macSize = CoreProperties.hmac_key_size;
                    complete = ByteBuffer.allocate(Message.HEADER_SIZE + CoreProperties.message_size + signatureSize + macSize);
                    signer.initVerify(pub);
                    break;
            }
        } catch (Exception e) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "CryptoSchemeFive()", e.getMessage());
        }
    }

    @Override
    public byte[] clientSecureMessage(byte[] data, int data_size) {
        try {
            signer.update(data, 0, data_size);
            signer.sign(data, data_size, signatureSize);
            mac.update(data, 0, data_size + signatureSize);
            mac.doFinal(data, data_size + signatureSize);
        } catch (IllegalStateException | SignatureException | ShortBufferException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "clientSecureMessage()", ex.getMessage());
        }
        return data;
    }

    @Override
    public boolean prefilterVerifyMessage(byte[] data, int size) {
        int dataSize = size - macSize;
        complete.clear();
        complete.rewind();
        complete.put(data);
        //complete.flip();
        complete.position(dataSize);
        complete.get(local_mac);
        mac.update(data, 0, dataSize);
        return Arrays.equals(mac.doFinal(), local_mac);
    }

    @Override
    public boolean filterVerifyMessage(byte[] data, int size) {
        try {
            int len = size - (signatureSize + macSize);
            signer.update(data, 0, len);
            return (signer.verify(data, len, signatureSize));
        } catch (SignatureException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "filterVerifyMessage()", ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean filterVerifyMessage(byte[] data) {
        try {
            int len = data.length - (signatureSize + macSize);
            signer.update(data, 0, len);
            return (signer.verify(data, len, signatureSize));
        } catch (SignatureException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "filterVerifyMessage()", ex.getMessage());
        }
        return false;
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
        try {
            complete.rewind();
            complete.put(data);
            signer.update(data);
            local_signature = signer.sign();
            complete.put(local_signature);
            local_mac = mac.doFinal(data);
//            System.out.println("MAC=>" + Arrays.toString(local_mac));
            complete.put(local_mac);
            byte[] cpl = new byte[data.length + signatureSize + macSize];
            complete.rewind();
            complete.get(cpl);
            return cpl;
        } catch (SignatureException ex) {
            Logger.getLogger(CryptoSchemeFive.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean prefilterVerifyMessage(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
