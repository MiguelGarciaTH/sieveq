/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.message;

import core.management.CoreConfiguration;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author miguel
 */
public class Message {

    public final static int HELLO = 0;
    public final static int HELLO_ACK = 1;
    public final static int SEND_REQUEST = 2;
    public final static int SEND_RESPONSE = 3;
    public final static int ADD_ROUTE = 4;
    public final static int CONNECT = 5;
    public final static int FINIT = 6;
    public final static int ERROR = 7;
    public final static int END_REQUEST = 8;
    public final static int END_ACK = 9;
    public final static int DISCARD = 10;
    public final static int RE_SEND_REQUEST = 11;
    public final static int REQUEST_ACK = 12;
    public final static int ADD_PREREPLICA = 13;
    public final static int RMV_PREREPLICA = 14;
    public final static int CHG_PREREPLICA = 15;
    public final static int CONTROLLER_HELLO = 16;
    public final static int WARMUP_END = 17;
    public final static int CHG_PREREPLICA_REQUEST = 18;
    public final static int WARMUP_END_ACK = 19;
    public final static int ACK = 20;
    public final static int KILL_REPLICA = 21;
    public final static int ADD_REPLICA = 22;
    public final static int CHG_REPLICA_REQUEST = 23;

    //private static final int SHORT_SIZE = 2;
    private static final int INT_SIZE = 4;
    private static final int offset = INT_SIZE;
    //INDEX    
    public static final int CRYPTO_FLAG = 0 * offset;
    public static final int TYPE = 1 * 1;
    public static final int SRC = 2 * offset;
    public static final int SEQNUMB = 3 * offset;
    public static final int PAYLOAD_SIZE = 4 * offset;
    public static final int PAYLOAD = 5 * offset;

    transient public static int HEADER_SIZE = (4 * INT_SIZE) + 1;

    private byte cryptoFlag;
    private int type;
    private int src;
    private int seqNumb;
    private int payloadSize;
    private byte[] payload;

    //private static ByteBuffer hasCrypto = ByteBuffer.allocate(4);
    /**
     * <| CRYPTO FLAG | DATA TYPE | SRC ID | SEQ NUMB | PAYLOAD SIZE | DATA |> |
     * HMAC or SIGNATURE|
     *
     *
     * @param type
     * @param src
     * @param seqNumb
     * @param payload
     */
    public Message(int type, int src, int seqNumb, byte[] payload) {
        this.cryptoFlag = setCryptoFlag(type);
        this.type = type;
        this.src = src;
        this.seqNumb = seqNumb;
        this.payloadSize = payload.length;
        this.payload = payload;

    }

    public Message() {

    }

    public static boolean hasCrypto(byte[] arr) {
        return (arr[0] & 0x1) == 1;

    }

    public int serialize(byte[] serl, ByteBuffer serialized) {
        serialized.rewind();
        serialized.put(setCryptoFlag(type));
        serialized.putInt(type);
        serialized.putInt(src);
        serialized.putInt(seqNumb);
        serialized.putInt(payloadSize);
        serialized.put(payload);
        serialized.rewind();
        serialized.get(serl);
        return HEADER_SIZE + payloadSize;
    }

    public byte[] serialize(ByteBuffer serialized1) {
        serialized1.rewind();
        serialized1.put(setCryptoFlag(type));
        serialized1.putInt(type);
        serialized1.putInt(src);
        serialized1.putInt(seqNumb);
        serialized1.putInt(payloadSize);
        serialized1.put(payload);
        serialized1.rewind();
        byte[] serl = new byte[payloadSize + HEADER_SIZE];
        serialized1.get(serl);
        return serl;
    }

    public Message deserialize(byte[] rawData, ByteBuffer deseliaized) {
        int seq = 0;
        try {
            deseliaized = ByteBuffer.wrap(rawData);
            byte flag = deseliaized.get();
            type = deseliaized.getInt();
            src = deseliaized.getInt();
            seq = deseliaized.getInt();
            int size = deseliaized.getInt();
            payload = new byte[size];
            deseliaized.get(payload);
        } catch (BufferUnderflowException ex) {
            return null;
        }
        return new Message(type, src, seq, payload);
    }

//
    public int getSeqNumber() {
        return this.seqNumb;
    }

    public static int getHeaderSize() {
        return HEADER_SIZE;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public int getSrc() {
        return this.src;
    }

    public byte[] getData() {
        return payload;
    }

    public int getPayloadSize() {
        return this.payloadSize;
    }

    public String type() {
        switch (getType()) {
            case HELLO:
                return "HELLO";
            case HELLO_ACK:
                return "HELLO_ACK";
            case SEND_REQUEST:
                return "SEND_REQUEST";
            case SEND_RESPONSE:
                return "SEND_RESPONSE";
            case ADD_ROUTE:
                return "ADD_ROUTE";
            case CONNECT:
                return "CONNECT";
            case ERROR:
                return "ERR";
            case END_REQUEST:
                return "END_REQUEST";
            case END_ACK:
                return "END_ACK";
            case CHG_PREREPLICA:
                return "CHG PREREPLICA";
            case RMV_PREREPLICA:
                return "REMOVE_REPLICA";
            case ADD_PREREPLICA:
                return "ADD PREREPLICA";
            case CONTROLLER_HELLO:
                return "CONTROLLER HELLO";
            case RE_SEND_REQUEST:
                return "RE-SEND REQUEST";
            case REQUEST_ACK:
                return "REQUEST ACK";
            case WARMUP_END:
                return "WARMUP END";
            case DISCARD:
                return "DISCARD";
            case CHG_PREREPLICA_REQUEST:
                return "CHG REPLICA REQUEST";
            case WARMUP_END_ACK:
                return "WARMUP_END_ACK";
            case ACK:
                return "ACK";
            default:
                return "Unknown=" + getType();
        }
    }

    public boolean equals(Message other) {
        if (this.cryptoFlag != other.cryptoFlag) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.src != other.src) {
            return false;
        }
        if (this.seqNumb != other.seqNumb) {
            return false;
        }
        if (this.payloadSize != other.payloadSize) {
            return false;
        }
        return Arrays.equals(this.payload, other.payload);
    }

    public static byte setCryptoFlag(int type) {
        switch (type) {
            case SEND_REQUEST:
                return (byte) 1;
            case WARMUP_END:
                return (byte) 1;
            case END_REQUEST:
                return (byte) 1;
            default:
                return (byte) 0;

        }
    }

    public String toString() {
        return "seq=" + this.getSeqNumber() + " src=" + this.getSrc() + " type=" + this.type() + " payloadSize=" + this.getPayloadSize();
    }

    public void alarm(ByteBuffer deserialized, byte[] rawData, boolean dump) {
        deserialized.clear();
        deserialized.put(rawData);
        byte flag = deserialized.get();
        int type = deserialized.getInt();
        if (type < 0 || type > 20) {
            System.out.println("Error: type=" + type);
            CoreConfiguration.pause(1);
        }
        int src = deserialized.getInt();
        if (src < 0 || src > 8) {
            System.out.println("Error: src=" + src);
            CoreConfiguration.pause(1);
        }
        int seq = deserialized.getInt();
        int len = deserialized.getInt();
        if (len < 0 || len > 2000) {
            System.out.println("Error: len=" + len);
            CoreConfiguration.pause(1);
        }
        byte[] payload = new byte[len];
        if (dump) {
            deserialized.get(payload);
            System.out.println("payload=" + Arrays.toString(payload));
        }
    }

    public void printHeader(byte[] rawData) {
        byte[] header = new byte[HEADER_SIZE];
        System.arraycopy(rawData, 0, header, 0, HEADER_SIZE);
        System.out.println("HEADER <" + Arrays.toString(header) + ">");
    }
}
