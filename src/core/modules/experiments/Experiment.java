/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.experiments;

import core.management.CoreConfiguration;
import core.management.CoreProperties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author miguel
 */
public class Experiment implements Runnable {

    private Latency latency;
    private Throughput throughput;
    private final String type;

    public Experiment(String type, int[] senSeq, Long[] sent, BlockingQueue inQueue) {
        this.type = type;
        if (type.equals("client")) {
            if (CoreConfiguration.role.equals("client")) {
                this.latency = new Latency(senSeq, true, sent, null, (LinkedBlockingQueue) inQueue, CoreProperties.latency_experiments_file);
            } else {
                this.latency = new Latency(null, false, null, inQueue, null, CoreProperties.latency_experiments_file);
            }
        } else {
            if (CoreConfiguration.role.equals("client")) {
                this.throughput = new Throughput(false, null, CoreProperties.throughput_experiments_file);
            } else {
                this.throughput = new Throughput(true, inQueue, CoreProperties.throughput_experiments_file);
            }
        }
    }

    @Override
    public void run() {
        if (type.equals("client")) {
            latency.run();
        } else {
            throughput.run();
        }
        
        
    }

}
