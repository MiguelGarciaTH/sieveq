/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

/**
 *
 * @author miguel
 */
public class Lock {

    private boolean lock;

    public Lock() {
        this.lock = true;

    }

    public void unlock() {
        this.lock = false;
    }

    public void lock() {
        this.lock = true;

    }

    public boolean isLocked() {
        return this.lock;
    }
}
