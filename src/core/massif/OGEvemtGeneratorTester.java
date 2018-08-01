/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core.massif;

/**
 *
 * @author miguel
 */
public class OGEvemtGeneratorTester {
    
    
        public static void main(String[] args){
            OGEvemtGenerator generator = new OGEvemtGenerator();
            //generator.loadEventFile("./config/test-log.log");
              generator.loadEventFile("./config/maxi-test-log.log");
            
            
            generator.getStatistics();
            int i=0;
            for (int j = 0; j < 1000; j++) {
                generator.getRandomEvent();
//                System.out.println("STR=>"+generator.getRandomEvent());
            }
        
    }
    
}
