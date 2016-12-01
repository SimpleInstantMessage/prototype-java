package gq.baijie.simpleim.prototype.business.server;

import gq.baijie.simpleim.prototype.business.common.Message;

public interface MessageSwitchSession extends Server.Connect.Session {

  void setOnReceiveRequestListener(OnReceiveRequestListener listener);

  void sendMessage(Message message);

  interface OnReceiveRequestListener {
    void onReceiveMessage(Message message);
  }

}
