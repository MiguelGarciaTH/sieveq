/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 *
 * @author miguel
 */
public class RouteTable implements Serializable {
    //<src, dsts>

    private TreeMap<Integer, LinkedList<Integer>> route;

    public RouteTable() {
        this.route = new TreeMap<Integer, LinkedList<Integer>>();
    }

    public void printString(){
        for (Integer g : route.keySet()) {
            System.out.println("ID = " + g );
            for (Integer r : route.get(g)) {
                System.out.println("\tELEM = " + r);
            }
            
        }
        
    }
    public void addRoute(Integer src, Integer dst) {
        if (route.containsKey(src)) {
            if (!(route.get(src).contains(dst))) {
                route.get(src).add(dst);
                route.get(dst).add(src);
            }
        } else {
            LinkedList<Integer> lst = new LinkedList<Integer>();
            lst.add(dst);
            route.put(src, lst);
        }
    }

    public void addSourceRoute(Integer src) {
        if (!route.containsKey(src)) {
            route.put(src, null);
        }
    }

    public void addDestinyRoute(Integer src, Integer dst) {
        if (route.containsKey(src)) {
            route.get(src).addAll(Arrays.asList(dst));
        } else {
            LinkedList<Integer> lst = new LinkedList<Integer>();
            lst.addAll(Arrays.asList(dst));
            route.put(src, lst);
        }
    }

    public void prettyPrint() {
        for (Integer k : route.keySet()) {
            System.out.print("Key=[" + k);
            LinkedList<Integer> list = route.get(k);
            for (Integer integer : list) {
                System.out.print(" " + integer);
            }
            System.out.println("]");

        }
    }

    public void addRoute(Integer src, Integer... dst) {
        if (route.containsKey(src)) {
            route.get(src).addAll(Arrays.asList(dst));
        } else {
            LinkedList<Integer> lst = new LinkedList<Integer>();
            lst.addAll(Arrays.asList(dst));
            route.put(src, lst);
        }
    }

    public LinkedList<Integer> getDestination(Integer src) {
        if (route.containsKey(src)) {
            return route.get(src);
        }
        return null;
    }

    public int[] getDestinationArray(Integer src) {
        int[] dst = new int[route.get(src).size()];
        int k = 0;
        for (Integer i : route.get(src)) {
            dst[k++] = i;
        }
        return dst;

    }

    public void removeDestination(String src, String dst) {
        if (route.containsKey(src)) {
            route.get(src).remove(dst);
        }
    }

    public void removeSource(String src) {
        if (route.containsKey(src)) {
            route.remove(src);
        }
    }
}
