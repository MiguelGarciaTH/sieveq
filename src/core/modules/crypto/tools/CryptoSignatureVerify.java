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
public class CryptoSignatureVerify implements Runnable {

    private ArrayBlockingQueue toSign;
    private ArrayBlockingQueue signDone;
    private Signature signer;
    private KeyStorage ks;
    private PrivateKey priv;
    private PublicKey pub;

    public CryptoSignatureVerify(ArrayBlockingQueue toSign, ArrayBlockingQueue signDone, String path, String pubKeyPaht) {
        try {
            this.toSign = toSign;
            this.signDone = signDone;
            this.ks = new KeyStorage(path, CoreProperties.algorithm);
            this.pub = ks.loadPublicKey(pubKeyPaht);
            this.signer = Signature.getInstance("SHA256withRSA");
            signer.initVerify(pub);
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
                signDone.put(signer.verify(data));
            } catch (Exception ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }
    }

}
