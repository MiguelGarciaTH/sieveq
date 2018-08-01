/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.prereplica;

import java.util.HashMap;

/**
 *
 * @author miguel
 */
public class IPList {

    private HashMap<Integer, String> ips;

    public IPList() {
        ips = new HashMap<>();

    }

    public void addIP(int id, String ip) {
        ips.put(id, ip);
    }

    public String getIP(int id) {
        return ips.get(id);
    }

}
