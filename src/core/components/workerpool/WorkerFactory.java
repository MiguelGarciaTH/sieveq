/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class WorkerFactory {

    public WorkerFactory() {

    }

    public Worker getWorker(String type, int tid, ArrayBlockingQueue in, ArrayBlockingQueue out) {
        switch (type) {
            case "crypto":
                return new CryptoWorkerClient(tid, in, out, null);

            case "none":
                return new NullWorker(tid, in, out);
            case "cryptoreplica":
                return new CryptoWorkerReplica(tid, in, out);
            case "cryptoprereplica":
                return new CryptoWorkerPrereplica(tid, in, out, null);
            case "teste":
                return new WorkerTest(tid, in, out);

        }
        return null;
    }

    public Worker getWorker(String type, int tid, ArrayBlockingQueue in, ArrayBlockingQueue out, ThreadBlockQueue blockQueue) {
        switch (type) {
            case "crypto":
                return new CryptoWorkerClient(tid, in, out, blockQueue);
            case "crypto2":
                return new CryptoWorkerClient2(tid, in, out, blockQueue);
            case "none":
                return new NullWorker(tid, in, out);
            case "cryptoreplica":
                return new CryptoWorkerReplica(tid, in, out);
            case "cryptoprereplica":
                return new CryptoWorkerPrereplica(tid, in, out, blockQueue);
            case "teste":
                return new WorkerTest(tid, in, out);

        }
        return null;
    }
}
