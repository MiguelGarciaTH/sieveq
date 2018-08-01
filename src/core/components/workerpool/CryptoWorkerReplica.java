/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.workerpool;

import bftsmart.tom.core.messages.TOMMessage;
import core.management.ByteArrayWrap;
import core.management.Message;
import core.management.CoreConfiguration;
import core.management.CoreProperties;
import core.modules.crypto.CryptoScheme;
import core.modules.crypto.CryptoSchemeFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author miguel
 */
public class CryptoWorkerReplica extends Worker {

    private final CryptoScheme crypto;
    private final CryptoSchemeFactory fact = new CryptoSchemeFactory();
    protected ByteBuffer deserliazedBuf = ByteBuffer.allocate(4500);
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
    protected ByteBuffer serialized1 = ByteBuffer.allocate(Message.HEADER_SIZE + 4500).order(ByteOrder.BIG_ENDIAN);
//    private ApplicationFirewall appfw;

    CryptoWorkerReplica(int tid, ArrayBlockingQueue in, ArrayBlockingQueue out) {
        super(tid, in, out);
//        this.appfw = new ApplicationFirewall();
        this.crypto = fact.getNewCryptoScheme(CoreProperties.crypto_scheme);
    }

    @Override
    public void run() {
        while (true) {
            while (true) {
                TOMMessage message;
                byte[] command;
                try {
                    message = (TOMMessage) in.take();
                    
                    command = message.getContent();
                } catch (InterruptedException ex) {
                    CoreConfiguration.printException(this.getClass().getCanonicalName(), "run", ex.getMessage());
                    System.out.println("Exeption");
                    break;
                }
                if (Message.hasCrypto(command)) {
                    ByteArrayWrap wrap = new ByteArrayWrap().deserialize(deserliazedBuf, command);
                    if (wrap != null) {
//                        if (wrap.getSize() < 250 && wrap.getSize() > 0) {
                            if (!crypto.filterVerifyMessage(wrap.getArr(), wrap.getSize())) {
                                CoreConfiguration.print("Verification error TID=" + tid);
                                message.setContent(new Message(Message.ERROR, CoreConfiguration.ID, CoreConfiguration.ID, command).serialize(serialized1));
                            } else {//if (!appfw.filterKMP(new String(command)))
                                message.setContent(wrap.getArr());
                                out.add(message);
//                            }else{
//                                System.out.println("firewall error");
                            }
                        }
//                    }
                } else if (command.length > 1) {
                    Message m = new Message().deserialize(command, deserialized);
                    if (m != null) {
                        if (m.getSrc() == 6) {
                            out.add(message);
                        } else {
                            ByteArrayWrap wrap = new ByteArrayWrap().deserialize(deserliazedBuf, command);
                            message.setContent(wrap.getArr());
                            out.add(message);
                        }
                    }
                } else {
                    System.out.println("ATTACKER");
                }
            }
        }
    }

}
