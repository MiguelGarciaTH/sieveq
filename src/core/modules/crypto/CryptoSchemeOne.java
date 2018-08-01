/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.crypto;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * This crypto scheme is an end-to-end authentication+integrity scheme. Client HMACs a message and the server verifies it. // * roles affected: client and server
 *
 * @author miguel
 */
public class CryptoSchemeOne extends CryptoScheme {

    private byte[] macArray;
    private byte[] dataArray;

    private ArrayBlockingQueue in, out;
    private int macSize = 0;
    private ByteBuffer complete;
    private CryptoMAC cryptoMac;

    public CryptoSchemeOne() {
        description = "off";
        if (CoreConfiguration.role.equals("client") || CoreConfiguration.role.equals("server")) {
            description = "on";
            this.in = new ArrayBlockingQueue(16);
            this.out = new ArrayBlockingQueue(16);
            this.cryptoMac = new CryptoMAC(in, out, CoreProperties.shared_key_path);
            this.macSize = cryptoMac.macSize();
            this.macArray = new byte[macSize];
        }
        new Thread(cryptoMac).start();
    }

    private void splitData(byte[] data) {
        complete.clear();
        complete.put(data);
        complete.position(0);
        dataArray = new byte[Math.abs(data.length - macSize)];
        complete.get(dataArray);
        complete.get(macArray);
    }

    @Override
    public byte[] clientSecureMessage(byte[] data) {
        try {
            //byte[] mac2 = calculateMAC(data);
            in.add(data);
            byte[] total = new byte[data.length + this.macSize];
            complete.position(0);
            complete.put(data);
            complete.put((byte) out.take());
            complete.position(0);
            complete.get(total);
            return total;
        } catch (InterruptedException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "clientSecureMessage", ex.getMessage());
            return null;
        }

    }

    @Override
    public boolean serverVerifyMessage(byte[] data) {
        try {
            splitData(data);
            in.add(dataArray);
            byte[] mac2 = (byte[]) out.take();
            return Arrays.equals(mac2, macArray);
        } catch (InterruptedException ex) {
            CoreConfiguration.printException(this.getClass().getCanonicalName(), "serverVerifyMessage", ex.getMessage());
            return false;
        }
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
