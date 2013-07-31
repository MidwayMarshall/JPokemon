package org.jpokemon.server;

import java.io.IOException;

import org.eclipse.jetty.websocket.WebSocket;
import org.jpokemon.activity.PlayerManager;
import org.jpokemon.activity.ServiceException;
import org.json.JSONException;
import org.json.JSONObject;

public class JPokemonWebSocket implements WebSocket.OnTextMessage {
  private Connection connection;

  @Override
  public void onClose(int arg0, String arg1) {
    PlayerManager.close(this);
  }

  @Override
  public void onOpen(Connection c) {
    connection = c;
  }

  @Override
  public void onMessage(String arg0) {
    try {
      PlayerManager.dispatchRequest(this, new JSONObject(arg0));
    } catch (JSONException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
  }

  public void sendJson(JSONObject json) {
    try {
      connection.sendMessage(json.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendMessage(Message message) {
    sendJson(message.toJson());
  }
}