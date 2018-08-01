/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto;

/**
 *
 * @author miguel
 */
public abstract class CryptoScheme {

    protected String description;

    public CryptoScheme() {
    }

    public String getDescription() {
        return this.description;
    }

    abstract public byte[] clientSecureMessage(byte[] data, int data_size);

    abstract public byte[] clientSecureMessage(byte[] data);

    abstract public boolean clientVerifyMessage(byte[] data);

    abstract public byte[] prefilterSecureMessage(byte[] data);

    abstract public boolean prefilterVerifyMessage(byte[] data, int size);

    abstract public boolean prefilterVerifyMessage(byte[] data);

    abstract public byte[] filterSecureMessage(byte[] data);

    abstract public boolean filterVerifyMessage(byte[] data);
    abstract public boolean filterVerifyMessage(byte[] data, int size);

    abstract public byte[] serverSecureMessage(byte[] data);

    abstract public boolean serverVerifyMessage(byte[] data);

}
