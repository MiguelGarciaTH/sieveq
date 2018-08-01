/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.replica;

public class KMP {
    private String pattern;
    private int[] next;

    // create Knuth-Morris-Pratt NFA from pattern
    public KMP(String pattern) {
//        System.out.println("Pattern="+pattern);
        this.pattern = pattern;
        int M = pattern.length();
        next = new int[M];
        int j = -1;
        for (int i = 0; i < M; i++) {
            if (i == 0)                                      next[i] = -1;
            else if (pattern.charAt(i) != pattern.charAt(j)) next[i] = j;
            else                                             next[i] = next[j];
            while (j >= 0 && pattern.charAt(i) != pattern.charAt(j)) {
                j = next[j];
            }
            j++;
        }

//        for (int i = 0; i < M; i++)
//            System.out.println("next[" + i + "] = " + next[i]);
    }

    // return offset of first occurrence of text in pattern (or N if no match)
    // simulate the NFA to find match
    public int search(String text) {
//        System.out.println("String="+text);
//        System.out.println("Pattern="+pattern);
        int M = pattern.length();
        int N = text.length();
        int i, j;
        for (i = 0, j = 0; i < N && j < M; i++) {
            while (j >= 0 && text.charAt(i) != pattern.charAt(j))
                j = next[j];
            j++;
        }
        if (j == M) return i - M;
        return N;
    }


//    // test client
//    public static void main(String[] args) {
//        String pattern = args[0];
//        String text    = args[1];
//        int M = pattern.length();
//        int N = text.length();
//
//        // substring search
//        KMPplus kmp = new KMPplus(pattern);
//        int offset = kmp.search(text);
//
//        // print results
//        StdOut.println("M = " + M + ", N = " + N);
//        StdOut.println("text:    " + text);
//        StdOut.print("pattern: ");
//        for (int i = 0; i < offset; i++)
//            StdOut.print(" ");
//        StdOut.println(pattern);
//    }

}
