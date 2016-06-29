package gq.baijie.simpleim.prototype.server;

import com.google.protobuf.Any;

import gq.baijie.simpleim.prototype.server.proto.message.Message;

public class Main {

  public static void main(String[] args) {
    final Message.Request request = Message.Request.newBuilder()
        .setId(1)
        .setFunction("test function")
        .build();
    println(request);

    Message.Frame frame = Message.Frame.newBuilder()
        .setRequest(request)
        .build();
    println(frame);
    println(frame.getMessageCase());
    println(frame.getRequest().toByteString());

    Message.Response response = Message.Response.newBuilder()
        .setId(1)
        .setSuccessMessage(Any.pack(request))
        .build();
    println(response);
    println(response.getResultCase());
    println(response.getSuccessMessage().getClass());
    println(response.getSuccessMessage().toByteString());
    println(response.getSuccessMessage());
    println(response.getFailureCause().getClass());
    println(response.getFailureCause().toByteString());
    println(response.getFailureCause());

    frame = Message.Frame.newBuilder()
        .setResponse(response)
        .build();
    println(frame);
    println(frame.getMessageCase());
//    println(frame.getRequest().toByteString());

    response = Message.Response.newBuilder()
        .setId(1)
        .setSuccessMessage(Any.pack(request))
        .setFailureCause(Any.pack(request))
        .build();
    println(response);
    println(response.getResultCase());
    println(response.getSuccessMessage().getClass());
    println(response.getSuccessMessage().toByteString());
    println(response.getSuccessMessage());
    println(response.getFailureCause().getClass());
    println(response.getFailureCause().toByteString());
    println(response.getFailureCause());


  }

  private static void println(Object o) {
    System.out.println(o);
  }

}
