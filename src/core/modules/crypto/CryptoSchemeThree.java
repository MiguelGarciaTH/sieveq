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

/**
 * This crypto scheme established authentication between the client and server. The client signs each message, and server verifies the signature.
 *
 * roles affected: client and replicas and server
 *
 * @author miguel
 */
public class CryptoSchemeThree extends CryptoScheme {

    private PrivateKey priv;
    private PublicKey pub;
    private KeyStorage keystore;
    private ByteBuffer complete;
    private Signature signer;
    private byte[] dataArray;
    private byte[] signature;
    private int signatureSize;

    public CryptoSchemeThree() {
        description = "off";
        if (CoreConfiguration.role.equals("client") || CoreConfiguration.role.equals("server")) {
            description = "on";
            this.keystore = new KeyStorage();
            try {
                this.complete = ByteBuffer.allocate(1000000);
                if (CoreProperties.signature_key_size == 1024) {
                    signatureSize = 128;
                }
                if (CoreProperties.signature_key_size == 512) {
                    signatureSize = 65;
                }
                this.signature = new byte[signatureSize];
                this.priv = keystore.loadPrivateKey(CoreProperties.private_key_path);
                this.pub = keystore.loadPublicKey(CoreProperties.public_key_path);
                this.signer = Signature.getInstance("SHA1withRSA");
            } catch (Exception ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "CryptoSchemeThree()", ex.getMessage());
            }
        }
    }

    @Override
    public byte[] clientSecureMessage(byte[] data) {
        try {
            signer.initSign(priv);
            signer.update(data);
            signature = signer.sign();
            return addSignatureToData(data, signature);
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "clientSecureMessage()", ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean serverVerifyMessage(byte[] data) {
        try {
            splitData(data);
            signer.initVerify(pub);
            signer.update(dataArray);
            return (signer.verify(signature));
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "serverVerifyMessage()", ex.getMessage());
        }
        return false;
    }

    public byte[] addSignatureToData(byte[] data, byte[] signature) {
        byte[] total = new byte[data.length + signature.length];
        complete.position(0);
        complete.put(data);
        complete.put(signature);
        complete.position(0);
        complete.get(total);
        return total;
    }

    private void splitData(byte[] data) {
        complete.clear();
        complete.put(data);
        complete.position(0);
        dataArray = new byte[Math.abs(data.length - signatureSize)];
        complete.get(dataArray);
        complete.get(signature);
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
    public boolean filterVerifyMessage(byte[] data) {
        return true;
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
