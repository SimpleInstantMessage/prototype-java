package gq.baijie.simpleim.prototype.server.service;

import gq.baijie.simpleim.prototype.business.api.Message;

public interface MessageSwitchServerHandle {

  void setOnReceiveRequestListener(OnReceiveRequestListener listener);

  void sendMessage(Message message);

  interface OnReceiveRequestListener {
    void onReceiveMessage(Message message);
  }

}
