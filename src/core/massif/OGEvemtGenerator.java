/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.massif;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author miguel
 */
public class OGEvemtGenerator {

    private HashMap<Integer, String> events;
    private int index = 0;
    private Random random;

    private double avg;
    private double std;
    private LinkedList<Integer> stdList;
    private int max, min;

    public OGEvemtGenerator() {
        this.events = new HashMap<>();
        this.random = new Random();
        this.avg = 0;
        this.std = 0;
        this.max = 0;
        this.min = Integer.MAX_VALUE;
        this.stdList = new LinkedList<Integer>();
    }

    public void getStatistics() {
        System.out.println("Total=" + index);
        System.out.println("Min=" + min);
        System.out.println("Max=" + max);
        avg = avg / index;
        System.out.println("Avg=" + avg);
        for (Integer integer : stdList) {
            std += (integer - avg) * (integer - avg);
        }
        std = Math.sqrt(std / index);
        System.out.println("Std=" + std);
        stdList.clear();
        std = 0;
        avg = 0;
        max = 0;
        min = Integer.MAX_VALUE;
        index = 0;

    }

    public String getRandomEvent() {
        int idx = random.nextInt(events.size());
        String event = events.get(idx);
        index++;
        avg += event.length();
        if (event.length() < min) {
            min = event.length();
        }
        if (event.length() > max) {
            max = event.length();
        }
        stdList.add(event.length());
        return event;
    }

    public boolean loadEventFile(String file) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                //sb.append(System.lineSeparator());
                line = br.readLine();
                line = eventValidator(line);
                if (!line.equals("")) {
//                    System.out.println(">>" + line);
                    events.put(index++, line);
                    avg += line.length();
                    if (line.length() < min) {
                        min = line.length();
                    }
                    if (line.length() > max) {
                        max = line.length();
                    }
                    stdList.add(line.length());

                }

            }

            //String everything = sb.toString();
        } catch (Exception ex) {
            return false;
        } finally {
            System.out.println("Load " + file + " completed...");
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {

                }
            }
        }

        return true;
    }

    private String eventValidator(String event) {
        if (event.contains("<13>")) {
            String[] cleanEvent = event.split("<13>");
            return cleanEvent[2];
        }

        return "";
    }

}
