/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import java.util.LinkedList;

/**
 *
 * @author miguel
 */
public class BlackList {

    private LinkedList<String> blackList;

    public BlackList() {
        blackList = new LinkedList<String>();

    }

    public boolean contains(String ip) {
        return blackList.contains(ip);
    }

    public void add(String ip) {
        if (!blackList.contains(ip)) {
            blackList.add(ip);
        }
    }

    public void remove(String ip) {
        blackList.remove(ip);
    }

    public void clear() {
        blackList.clear();
    }

}
