/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.management;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Encoder;

/**
 *
 * @author miguel
 */
public class CoreConfiguration {

    static public volatile CoreConfiguration config = null;
    static public int ID;
    static public String role;
    static public String crypto_scheme;
    static public String crypto_description;
    static private BASE64Encoder encoder;
    static public boolean channel;
    static private String template;

    public static CoreConfiguration getConfiguration(int ID, String role) {
        encoder = new BASE64Encoder();
        if (config == null) {
            return new CoreConfiguration(ID, role);
        } else {
            return config;
        }
    }

    public static void setChannel(boolean type) {
        channel = type;
    }

    public static void pause(int secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CoreConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void updateId(int id) {
        ID = id;
    }

    private CoreConfiguration(int ID, String role) {
        this.ID = ID;
        this.role = role;
        this.template = "[" + role + " id=" + ID + "]: ";

    }

    public String toString() {
        String print = "---------------------------------------------------------------\n";
        print = print.concat(">> core-mis configuration:\n");
        print = print.concat(">> Role=" + role + " ID=" + ID + " \n");
        print = print.concat(">> Crytpo scheme=" + crypto_scheme + " : " + crypto_description + " \n");
        print = print.concat(">> Channel reliable=" + channel + "\n");
        print = print.concat("---------------------------------------------------------------\n");
        return print;

    }

    public static void print(String text) {
        System.out.println(template + text);
    }

    public static void printException(String className, String method, String exception) {
        System.out.println(template + " " + className + "'s exception on " + method + ":" + exception);
    }

    public static void setCryptoScheme(String scheme, String description) {
        CoreConfiguration.crypto_scheme = scheme;
        CoreConfiguration.crypto_description = description;
    }

    public static void debug(String title, byte[] var) {
        System.out.println(title + "= " + encoder.encode(var));
    }

    public static void debug(String title, int[] var) {
        System.out.println(title + "= " + Arrays.toString(var));
    }

    public static void length(String title, Object[] arr) {
        System.out.println(title + " length = " + arr.length);
    }

    public static void length(String title, byte[] arr) {
        System.out.println(title + " length = " + arr.length);
    }
}
