/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class WorkerPool {

    private WorkerFactory fact;
    private LinkedList<Thread> workers;
    private String type;

    private ArrayBlockingQueue in, out;
    private ThreadBlockQueue queue;

    public WorkerPool(ArrayBlockingQueue in, ArrayBlockingQueue out, String type, int numb_workers) {
        fact = new WorkerFactory();
        this.in = in;
        this.out = out;
        this.type = type;
        workers = new LinkedList<>();
        for (int i = 0; i < numb_workers; i++) {
            workers.add(new Thread(fact.getWorker(type, i, this.in, this.out)));
            workers.get(i).start();
        }
    }

    public WorkerPool(ArrayBlockingQueue in, ArrayBlockingQueue out, ThreadBlockQueue queue, String type, int numb_workers) {
        fact = new WorkerFactory();
        this.in = in;
        this.out = out;
        this.queue=queue;
        this.type = type;
        workers = new LinkedList<>();
        for (int i = 0; i < numb_workers; i++) {
            workers.add(new Thread(fact.getWorker(type, i, this.in, this.out, this.queue)));
            workers.get(i).start();
        }
    }

    public void stopWorkers() {
        for (Thread worker : workers) {
            worker.interrupt();
        }
    }

//    public void ReloadWorkerPool() {
//        for (Thread worker : workers) {
//            worker.interrupt();
//        }
//        workers = new LinkedList<>();
//        for (int i = 0; i < CoreProperties.num_workers; i++) {
//            workers.add(new Thread(fact.getWorker(type, i, in, out)));
//            workers.get(i).start();
//            System.out.println("New worker started tid=" + i);
//        }
//    }
}
