/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.attacker;

/**
 *
 * @author miguel
 */
public class Counter {

    private int counter;

    public Counter() {
        this.counter = 0;
    }

    public int getCounter() {
        return this.counter;

    }

    public void incrementeCounter() {
        this.counter++;
    }

    public void incrementeCounter(int add) {
        this.counter += add;
    }

    public void decrementCounter() {
        this.counter--;
    }
    public String toString(){
        return ""+counter;
    }

}
