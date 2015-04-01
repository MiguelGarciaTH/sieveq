/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.server;

import core.message.Message;
import core.management.CoreProperties;
import core.misc.Lock;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
class OrderQueue implements Runnable {

    private int BACTH_SIZE;
    private ArrayBlockingQueue unordered, ordered;
    private Message[] batch;
    private Quicksort sort;

    private RemindTask task;
    private Timer timer;
    private Lock lock;
    long init;

    public OrderQueue(ArrayBlockingQueue thirdQueue, ArrayBlockingQueue orderedQueue, int batch_size) {
        unordered = thirdQueue;
        ordered = orderedQueue;
        BACTH_SIZE = batch_size;
        batch = new Message[BACTH_SIZE];
        sort = new Quicksort();
        this.lock = new Lock();
        System.out.println("Time out=" + CoreProperties.timeout);
    }

    @Override
    public void run() {
        int messages = 0;
        lock.lock();
        task = new RemindTask(lock);
        timer = new Timer();
        timer.schedule(task, 0, 1000); //delay in milliseconds
        while (true) {
            init = System.nanoTime();
            while (lock.isLocked()) {//&& lock.isLocked() messages < BACTH_SIZE && 
                if (!unordered.isEmpty()) {
                    batch[messages++] = (Message) unordered.remove();
                }
            }
            System.out.println("Is locked? " + lock.isLocked());
            timer.cancel();
            System.out.println("batch=" + messages + " elasped=" + ((System.nanoTime() - init) / 1000000000.0) + "secs" + " time out =" + CoreProperties.timeout);
            sort.sort(batch, messages);
            for (int i = 0; i < messages; i++) {
                ordered.add(batch[i]);
            }
            messages = 0;
            lock.lock();
            task = new RemindTask(lock);
            timer = new Timer();
            timer.schedule(task, 0, 1000); //delay in milliseconds
        }
    }

    class RemindTask extends TimerTask {

        Lock lock;

        RemindTask(Lock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    class Quicksort {

        private Message[] message;

        public void sort(Message[] messageToSort, int maxindex) {
            if (maxindex > 1) {
                this.message = messageToSort;
                quicksort(0, maxindex - 1);
            }
        }

        private void quicksort(int low, int high) {
            int i = low, j = high;
            int pivot = message[low + (high - low) / 2].getSeqNumber();
            while (i <= j) {
                while (message[i].getSeqNumber() < pivot) {
                    i++;
                }
                while (message[j].getSeqNumber() > pivot) {
                    j--;
                }
                if (i <= j) {
                    swap(i, j);
                    i++;
                    j--;
                }
            }
            if (low < j) {
                quicksort(low, j);
            }
            if (i < high) {
                quicksort(i, high);
            }
        }

        private void swap(int i, int j) {
            Message temp = message[i];
            message[i] = message[j];
            message[j] = temp;
        }
    }

}
