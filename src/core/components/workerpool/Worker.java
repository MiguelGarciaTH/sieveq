/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author miguel
 */
public abstract class Worker implements Runnable {

    protected ArrayBlockingQueue in, out;
    protected int tid;

    Worker(int tid, ArrayBlockingQueue in, ArrayBlockingQueue out) {
        this.tid = tid;
        this.in = in;
        this.out = out;

    }

}
