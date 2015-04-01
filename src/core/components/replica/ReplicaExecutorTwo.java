///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package core.components.replica;
//
//import bftsmart.communication.ServerCommunicationSystem;
//import bftsmart.tom.MessageContext;
//import bftsmart.tom.ServiceReplica;
//import bftsmart.tom.core.messages.TOMMessage;
//import core.message.Message;
//import core.misc.CoreConfiguration;
//import java.util.Arrays;
//
///**
// *
// * @author miguel
// */
//public class ReplicaExecutorTwo extends ReplicaExecutor {
//
//    private boolean primary = false;
//    private ServerCommunicationSystem comm;
//
//    public ReplicaExecutorTwo(int id) {
//        super(id);
//        this.primary = (id == 0);
//        this.replica = new ServiceReplica(id, this, this, true);
//        this.replica.setReplyController(this);
//        this.comm = replica.getReplicaContext().getServerCommunicationSystem();
//    }
//
//    @Override
//    public void manageReply(TOMMessage request, MessageContext msgCtx) {
//        if (primary) {
//            Message rcv = Message.deserialize(request.reply.getContent());
//            int[] dst = null;
//            switch (rcv.getType()) {
//                case Message.HELLO_ACK:
//                    comm.send(new int[]{rcv.getSrc()}, request.reply);
//                    if (connected.size() > 1) {
//                        int[] dst2 = getDst();
//                        CoreConfiguration.print("CONNECTED sendingTo=" + Arrays.toString(dst2));
//                        Message msg = new Message(Message.CONNECT, sharedID, rcv.getSeqNumber(), intTobytes(dst2));
//                        TOMMessage tomMsg = new TOMMessage(0, request.getSession(), request.getSequence(), msg.serialize(), request.getViewID(), request.getReqType());
//                        comm.send(dst2, tomMsg);
//                    }
//                    break;
//                case Message.SEND_REQUEST:
//                    dst = route.getDestinationArray(rcv.getSrc());
//                    comm.send(dst, request.reply);
//                    break;
//                case Message.END_REQUEST:
//                    dst = route.getDestinationArray(rcv.getSrc());
//                    comm.send(dst, request.reply);
//                    CoreConfiguration.print("End request sent");
//                    break;
//                case Message.END_ACK:
//                    dst = route.getDestinationArray(rcv.getSrc());
//                    comm.send(dst, request.reply);
//                    break;
//                case Message.ADD_ROUTE:
//                    dst = route.getDestinationArray(rcv.getSrc());
//                    CoreConfiguration.print("new route=" + rcv.getSrc() + " > " + Arrays.toString(dst));
//                    break;
//                default:
//                    CoreConfiguration.print("Nothing to send");
//            }
//        }
//    }
//}
