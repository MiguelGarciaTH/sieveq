/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

//import core.components.attacker.Attacker;
import core.components.client.Client;
import core.components.controller.Controller;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.components.prereplica.PreReplica;
import core.components.replica.Replica;
import core.components.server.Server;

/**
 *
 * @author miguel
 */
public class CoreMIS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //components
        Client client = null;
        Controller controller = null;
//        Attacker attacker = null;
        Server server = null;
        PreReplica prereplica = null;
        //modules
        CoreProperties prop = null;
        CryptoScheme crypto = null;

        CoreConfiguration config = null;

        int dst;
        if (args.length <= 0) {
            System.out.println("Invalid number of arguments \n"
                    + "\tusage mode: {1|2|3} \n"
                    + "\tcommand: mode {attacker|client|server|replica} \n"
                    + "\targuments: mode option {id} \n"
                    + "\tproperties file \n"
                    + "\t[client's destiantion: {id dst}]\n");
        } else {
            String command = args[1];
            int ID = Integer.parseInt(args[2]);
            int mode = Integer.parseInt(args[0]);
            prop = CoreProperties.getProperties(args[3], command);
            config = CoreConfiguration.getConfiguration(ID, command);
            
            crypto = CryptoSchemeFactory.getCryptoScheme(CoreProperties.crypto_scheme);
            CoreConfiguration.setCryptoScheme(CoreProperties.crypto_scheme, crypto.getDescription());
            CoreConfiguration.setChannel(CoreProperties.channel);
            System.out.println(">> Properties file=" + args[3]);
            System.out.println(config);
            switch (command) {
                case "client":
                    dst = Integer.parseInt(args[4]);
                    client = new Client(mode, ID, dst);
                    client.start();
                    break;
                case "server":
                    server = new Server(mode, ID, prop);
                    server.start();
                    break;
                case "replica":
                    new Replica(ID);
                    break;
                case "prereplica":
                    prereplica = new PreReplica(mode, ID);
                    prereplica.start();
                    break;
                case "controller":
                    controller = new Controller(ID, mode, prop.operator);
                    controller.start();
                    break;
                case "attacker":
                    System.out.println("Args 0:" + args[0]); 
                    System.out.println("Args 1:" + args[1]); 
                    System.out.println("Args 2:" + args[2]); 
                    System.out.println("Args 3:" + args[3]); 
                    System.out.println("Args 4:" + args[4]); 
                    System.out.println("UNCOMMENT!!");
//                    attacker = new Attacker(mode, Integer.parseInt(args[4]));
//                    attacker.start();
                    break;

            }
        }

    }
}
