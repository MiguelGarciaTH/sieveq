/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.components.controller;

import bftsmart.communication.client.ReplyListener;
import bftsmart.tom.ServiceProxy;
import bftsmart.tom.core.messages.TOMMessage;
import core.management.BlackList;
import core.message.Message;
import core.management.CoreConfiguration;
import core.modules.voter.SimpleVoter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author miguel
 */
public class ControllerSMaRt implements ReplyListener, Runnable {

    private ServiceProxy proxy;
    private ControllerOperator operator;
    private SimpleVoter voter;
    private boolean trinco = false;
    private int id;
    private String id_str;
    private int nodeID;
    private BlackList blacklist;
    protected ByteBuffer deserialized = ByteBuffer.allocate(Message.HEADER_SIZE + 1500).order(ByteOrder.BIG_ENDIAN);
    protected String[] ids;
    private boolean trinco1;

    public ControllerSMaRt(int ID, ControllerOperator operator) {
        this.id = ID;
        this.proxy = new ServiceProxy(ID);
        this.proxy.setReplyListener(this);
        this.operator = operator;
        this.voter = new SimpleVoter(4, 2);
        this.blacklist = new BlackList();

    }

    @Override
    public void replyReceived(TOMMessage reply) {
        Message resp = new Message().deserialize(reply.getContent(), deserialized);
        if (voter.vote(resp.getSrc(), resp.getSeqNumber(), resp.getData())) {
            switch (resp.getType()) {
                case Message.HELLO_ACK:
                    CoreConfiguration.print("connected to replicas");
                    break;
                case Message.KILL_REPLICA:
                    System.out.println("KILL REPLICA");
                    if (!trinco1) {
                        id_str = new String(resp.getData());
                        ids = id_str.split(":");
                        if (!blacklist.contains(ids[1])) {
                            nodeID = Integer.parseInt(ids[1]);
                            blacklist.add(ids[0]);
                            operator.createReplica(nodeID);
                            CoreConfiguration.print("add replica id=" + nodeID);
//                            operator.destroyReplica(Integer.parseInt(ids[0]));
//                            CoreConfiguration.print("destroy replica id=" + ids[0]);
                        }
                        trinco1 = true;
                        break;
                    }
                case Message.ADD_PREREPLICA:
                    if (!trinco) {
                        id_str = new String(resp.getData());
                        ids = id_str.split(":");
                        if (!blacklist.contains(ids[1])) {
                            nodeID = Integer.parseInt(ids[1]);
                            blacklist.add(ids[0]);
                            operator.createPreReplica(nodeID);
                            CoreConfiguration.print("add prereplica id=" + nodeID);
                            operator.destroyPreReplica(Integer.parseInt(ids[0]));
                            CoreConfiguration.print("destroy prereplica id=" + ids[0]);
                        }
                        trinco = true;
                    }
                    break;
//                case Message.RMV_PREREPLICA:
//                    id_str = new String(resp.getData());
//                    if (!blacklist.contains(id_str)) {
//                        prereplicaId = Integer.parseInt(new String(resp.getData()));
//                        operator.destroyPreReplica(prereplicaId);
//                        blacklist.add(id_str);
//                        CoreConfiguration.print("remove prereplica id=" + prereplicaId);
//                    }
//                    break;
//                default:
//                    CoreConfiguration.print("unknown type=" + resp.getType());
            }
        }
    }

    @Override
    public void run() {
        int[] processes = proxy.getViewManager().getCurrentViewProcesses();
        Message m = new Message(Message.CONTROLLER_HELLO, id, 0, new byte[]{1});
        proxy.invokeAsynchronous(m.serialize(deserialized), this, processes);
        while (true) {
            System.out.print(". ");
            CoreConfiguration.pause(5);
        }
    }
}
