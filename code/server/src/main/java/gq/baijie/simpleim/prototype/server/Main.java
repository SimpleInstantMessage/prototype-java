package gq.baijie.simpleim.prototype.server;

import gq.baijie.simpleim.prototype.server.proto.message.MessageFrameOuterClass;

public class Main {

  public static void main(String[] args) {
    System.out.println(MessageFrameOuterClass.MessageFrame.newBuilder()
                           .setSessionId(1)
                           .setServiceId("test service")
                           .build());
  }

}
