/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto.tools;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.crypto.KeyStorage;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class CryptoSignatureSign implements Runnable {

    private ArrayBlockingQueue toSign;
    private ArrayBlockingQueue signDone;
    private Signature signer;
    private KeyStorage ks;
    private PrivateKey priv;
    private PublicKey pub;
    

    public CryptoSignatureSign(ArrayBlockingQueue toSign, ArrayBlockingQueue signDone, String path, String privKeyPath) {
        try {
            this.toSign = toSign;
            this.signDone = signDone;
            this.ks = new KeyStorage(path, CoreProperties.algorithm);
            this.priv = ks.loadPrivateKey(privKeyPath);
            this.signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(priv);
        } catch (Exception ex) {
            CoreConfiguration.printException(this.getClass().getName(), "CryptoSignature", ex.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] data = (byte[]) toSign.take();
                signer.update(data);
                signDone.put(signer.sign());
            } catch (Exception ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }
    }

}
