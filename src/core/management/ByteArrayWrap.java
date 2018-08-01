/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import java.nio.ByteBuffer;

/**
 *
 * @author miguel
 */
public class ByteArrayWrap {

    private byte[] arr;
    private int size;

    public ByteArrayWrap(byte[] arr, int size) {
        if (size <= 0) {
            this.arr = null;
            this.size = 0;
        } else {
            this.arr = arr;
            this.size = size;
        }
    }

    public ByteArrayWrap() {

    }

    public byte[] getArr() {
        return arr;
    }

    public int getSize() {
        return size;
    }

    public void serialize(ByteBuffer serliazedBuf, byte[] data, int size) {
        serliazedBuf.clear();
        serliazedBuf.rewind();
        serliazedBuf.put(data);
        serliazedBuf.position(data.length - 4);
        serliazedBuf.putInt(size);
        serliazedBuf.rewind();
        serliazedBuf.get(data);
    }

    public ByteArrayWrap deserialize(ByteBuffer deserliazedBuf, byte[] data) {
        deserliazedBuf.clear();
        deserliazedBuf.rewind();
        deserliazedBuf.put(data);
        int sizeRead = deserliazedBuf.getInt(data.length - 4);
        return new ByteArrayWrap(data, sizeRead);
    }

}
