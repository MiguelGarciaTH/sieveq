/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.controller;

import java.io.IOException;

/**
 *
 * @author miguel
 */
public class tester {

    public static void main(String[] args) {

        String cmd = "kill -4 `pgrep -f vlc`";
        try {
            System.out.println(cmd);
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {
            System.out.println("EX> " + ex.getMessage());
        }
    }

}
