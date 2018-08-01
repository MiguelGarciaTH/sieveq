/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author miguel
 */
public class ApplicationFirewall {

    private final HashMap<Integer, String> patterns;
    //private Pattern pattern;

    private SecureRandom random;
    private int patternlenght;
    private int numberofpatterns;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//    static final String AB = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

    private KMP kmp;
    private BM bm;
    private BM2 bm2;
    public ApplicationFirewall() {
        this.patterns = new HashMap<>();
        this.random = new SecureRandom();
//        this.numberofpatterns = CoreProperties.numberofpatterns;
//        this.patternlenght = CoreProperties.patternlenght;
        int size = 20;
        StringBuilder sb = new StringBuilder(size);
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            sb.append(AB.charAt(r.nextInt(AB.length())));
        }
        String ptrn = sb.toString();
        kmp = new KMP(ptrn);
        bm = new BM(ptrn);
        bm2 = new BM2(ptrn);
        //generatePatterns();
        //loadfile(CoreProperties.rules);
    }

    public boolean filterBM(String entry) {
        return bm.search(entry) != entry.length();
    }

    public boolean filterKMP(String entry) {
        return kmp.search(entry) != entry.length();
    }
    public boolean filterBM2(String entry){
        return bm2.filterBM2(AB);
    }

    public boolean filter(String entry) {
        boolean find = false;
        for (Integer key : patterns.keySet()) {
            String s = patterns.get(key);
            if (entry.contains(s)) {
                find = true;
            }
        }
        return find;
    }

    private void generatePatterns() {
        Random r = new Random(100);
        StringBuilder sb = new StringBuilder(patternlenght);
        for (int j = 0; j < numberofpatterns; j++) {
            for (int i = 0; i < patternlenght; i++) {
                sb.append(AB.charAt(r.nextInt(AB.length())));
            }
            patterns.put(sb.hashCode(), sb.toString());
//            System.out.println(j + ": pattern=" + sb.tCoreConfiguration.print("Number of patterns=" + numberofpatterns);oString());
        }
    }

    private void loadfile(String filename) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            patterns.put(line.hashCode(), line);
//            }

        } catch (Exception ex) {
            Logger.getLogger(ApplicationFirewall.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (Exception ex) {
                Logger.getLogger(ApplicationFirewall.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
