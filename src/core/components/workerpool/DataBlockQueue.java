/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class DataBlockQueue extends LinkedBlockingQueue<byte[]> {

    public DataBlockQueue(int nrBlocks, int blockSize) {
        super();
//        System.out.println("Block size=>"+blockSize);
        setup(nrBlocks, blockSize);

    }

    private void setup(int nrBLocks, int blockSize) {
        for (int i = 0; i < nrBLocks; i++) {
            super.offer(new byte[blockSize]);
        }
    }

    @Override
    public byte[] take() {
        try {
            return super.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(DataBlockQueue.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean offer(byte[] block) {
        return super.offer(block);
    }

}
