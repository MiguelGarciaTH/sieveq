/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.voter;

import core.management.CoreConfiguration;

/**
 *
 * @author miguel
 */
public class VoterFactory {

    public static Voter getVoter(String type, int replicas, int quorom) {

        switch (type.toLowerCase()) {
            case "simple":
                return new SimpleVoter(replicas, quorom);
//            case "fast":
//                return new BloomFilterVoter(quorom);
            default:
                CoreConfiguration.print("Possible types: simple (default) | fast (bloomfilter) | normal ");
                return new SimpleVoter(replicas, quorom);
        }

    }

}
