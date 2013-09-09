package org.jpokemon.battle.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jpokemon.activity.PlayerManager;
import org.jpokemon.activity.ServiceException;
import org.jpokemon.server.JPokemonService;
import org.jpokemon.server.Message;
import org.jpokemon.trainer.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LobbyService implements JPokemonService {
  private static final Map<String, List<String>> pending = new HashMap<String, List<String>>();

  @Override
  public void serve(JSONObject request, Player player) throws ServiceException {
    try {
      if (request.has("configure")) {
        configure(request, player);
      }
      else if (request.has("respond")) {
        respond(request, player);
      }
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
  }

  private void configure(JSONObject json, Player player) throws JSONException, ServiceException {
    String configure = json.getString("configure");
    Lobby lobby = Lobby.get(player.id());

    if (configure.equals("addteam")) {
      if (lobby.isOpen()) { return; }

      lobby.addTeam();
      PlayerManager.pushJson(player, load(new JSONObject(), player));
    }
    else if (configure.equals("addplayer")) {
      String otherPlayerName = json.getString("name");
      int team = json.getInt("team");

      if (lobby.isOpen()) { return; }

      if (PlayerManager.getPlayer(otherPlayerName) == null) {
        Message message = new Message.Notification("'" + otherPlayerName + "' not found");
        PlayerManager.pushMessage(player, message);
        return;
      }

      lobby.addPlayer(otherPlayerName, team);
      PlayerManager.pushJson(player, load(new JSONObject(), player));
    }
    else if (configure.equals("openstate")) {
      boolean open = json.getBoolean("openstate");

      lobby.setOpen(open);

      if (open) {
        buildPending(lobby);
        pushLobbyToPlayers(lobby, true);
      }
      else {
        clearPending(lobby);
        PlayerManager.pushJson(player, load(new JSONObject(), player));
      }
    }
  }

  private void respond(JSONObject json, Player player) throws JSONException, ServiceException {
    String host = json.getString("host");
    String response = json.getString("respond");
    Lobby lobby = Lobby.get(host);

    if (!lobby.isOpen() || !lobby.getResponses().keySet().contains(player.id())) { return; }

    if (response.equals("accept")) {
      lobby.accept(player.id());
    }
    else if (response.equals("reject")) {
      lobby.reject(player.id());
    }

    pushLobbyToPlayers(lobby, false);
  }

  @Override
  public JSONObject load(JSONObject request, Player player) {
    String host = player.id();

    if (request.has("host")) {
      try {
        host = request.getString("host");
      }
      catch (JSONException e) {
        e.printStackTrace();
      }
    }

    Lobby lobby = Lobby.get(host);
    JSONObject json = generateJson(lobby);

    List<String> pendingList;
    synchronized (pending) {
      pendingList = pending.get(player.id());
    }
    if (pendingList == null) {
      pendingList = new ArrayList<String>();
    }

    try {
      json.put("pending", new JSONArray(pendingList.toString()));
    }
    catch (JSONException e) {
      e.printStackTrace();
    }

    return json;
  }

  private JSONObject generateJson(Lobby lobby) {
    JSONObject json = new JSONObject();

    try {
      json.put("action", "lobby");
      json.put("host", lobby.getHost());
      json.put("open", lobby.isOpen());
      json.put("teams", new JSONArray(lobby.getTeams().toString()));
      json.put("responses", new JSONObject(lobby.getResponses()));
    }
    catch (JSONException e) {
    }

    return json;
  }

  private void buildPending(Lobby lobby) {
    for (List<String> team : lobby.getTeams()) {
      for (String name : team) {
        if (name.equals(lobby.getHost())) {
          continue;
        }

        List<String> pendingList;
        synchronized (pending) {
          if (pending.get(name) == null) {
            pending.put(name, new ArrayList<String>());
          }
          pendingList = pending.get(name);
        }

        synchronized (pendingList) {
          pendingList.add(lobby.getHost());
        }

        Player player = PlayerManager.getPlayer(name);
        PlayerManager.pushJson(player, load(new JSONObject(), player));
      }
    }
  }

  private void clearPending(Lobby lobby) {
    for (List<String> team : lobby.getTeams()) {
      for (String name : team) {
        if (name.equals(lobby.getHost())) {
          continue;
        }

        List<String> pendingList;

        synchronized (pending) {
          pendingList = pending.get(name);
        }

        if (pendingList == null) {
          continue;
        }

        synchronized (pendingList) {
          pendingList.remove(lobby.getHost());
        }

        Player player = PlayerManager.getPlayer(name);
        PlayerManager.pushJson(player, load(new JSONObject(), player));
      }
    }
  }

  private void pushLobbyToPlayers(Lobby lobby, boolean sendNewRequestMessage) {
    Message message = new Message.Notification("New battle request");
    JSONObject lobbyJson = generateJson(lobby);

    for (List<String> team : lobby.getTeams()) {
      for (String name : team) {
        Player player = PlayerManager.getPlayer(name);

        PlayerManager.pushJson(player, lobbyJson);

        if (sendNewRequestMessage) {
          PlayerManager.pushMessage(player, message);
        }
      }
    }
  }
}