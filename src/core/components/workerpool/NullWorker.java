/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core.components.workerpool;

import core.management.CoreConfiguration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author miguel
 */
class NullWorker extends Worker {

    
    
    public NullWorker(int tid, ArrayBlockingQueue in, ArrayBlockingQueue out) {
        super(tid, in, out);
        
    }

    @Override
    public void run() {
        while(true){
            try {
                byte[] data = (byte[]) in.take();
                out.put(data);
            } catch (InterruptedException ex) {
                CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
            }
        }
    }
    
}
