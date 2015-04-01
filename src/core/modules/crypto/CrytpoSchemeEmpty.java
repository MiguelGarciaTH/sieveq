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
public class CrytpoSchemeEmpty extends CryptoScheme {

    public CrytpoSchemeEmpty() {
        description = "off";
    }

    @Override
    public byte[] clientSecureMessage(byte[] data) {
        return data;
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
    public boolean serverVerifyMessage(byte[] data) {
        return true;
    }

    @Override
    public byte[] clientSecureMessage(byte[] data, int data_size) {
        return data;
    }

    @Override
    public boolean prefilterVerifyMessage(byte[] data, int size) {
        return true;
    }

    @Override
    public boolean filterVerifyMessage(byte[] data, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
