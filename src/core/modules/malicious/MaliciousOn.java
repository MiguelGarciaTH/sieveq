/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.modules.malicious;

import core.management.CoreProperties;
import core.message.Message;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 *
 * @author miguel
 */
public class MaliciousOn extends Malicious {

    private double probability;
    private int num_bytes;
    private boolean random_bytes;
    private Random random;

    public MaliciousOn() {
        random_bytes = CoreProperties.random_bytes;
        num_bytes = CoreProperties.num_bytes;
        //probability = CoreProperties.probability;
        probability=0.5;
        random = new Random();
    }

    public byte[] corrupt(byte[] message) {
        double k = random.nextDouble();
        if (k <= probability) {
            ByteBuffer temp = ByteBuffer.wrap(message);
            temp.put(getIndex(CoreProperties.target), (byte) 0x1);
            temp.rewind();
            temp.get(message);
        }
        return message;
    }

    private int getIndex(String target) {
        switch (target) {
            case "crypto_flag":
                return Message.CRYPTO_FLAG;
            case "payload":
                return Message.PAYLOAD;
        }
        return -1;
    }
}
