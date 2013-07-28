package org.jpokemon.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.jpokemon.server.JPokemonServer;
import org.jpokemon.server.JPokemonWebSocket;
import org.jpokemon.trainer.Player;
import org.jpokemon.trainer.PokemonTrainer;
import org.json.JSONException;
import org.json.JSONObject;
import org.zachtaylor.jnodalxml.XmlParser;

public class PlayerManager {
  public static void dispatchRequest(JPokemonWebSocket socket, JSONObject request) throws JSONException, ServiceException {
    Player player = connections.get(socket);

    if (player == null) {
      String action = request.getString("action");

      if (action.equals("login")) {
        player = PlayerManager.login(request.getString("name"));
      }
      else if (action.equals("create")) {
        player = PlayerManager.create(request.getString("name"), request.getString("rival"));
      }

      connections.put(socket, player);
    }
    else {
      getActivity(player).handleRequest(player, request);
    }
  }

  public static Activity getActivity(Player player) {
    Stack<Activity> stack = activities.get(player);
    return stack.peek();
  }

  public static void addActivity(Player player, Activity a) {
    // TODO
  }

  public static void popActivity(PokemonTrainer trainer) {
    // TODO
  }

  public static void save(Player player) {
    String path = JPokemonServer.savepath + player.id() + ".jpkmn";

    try {
      Writer writer = new BufferedWriter(new PrintWriter(new File(path)));
      writer.write(player.toXml().toString());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Player login(String name) throws ServiceException {
    if (players.keySet().contains(name)) {
      throw new ServiceException("File already loaded");
    }

    File file = new File(JPokemonServer.savepath, name + ".jpkmn");

    if (!file.exists()) {
      throw new ServiceException("Save file not found");
    }

    Player player = newPlayer(name);

    try {
      player.loadXML(XmlParser.parse(file).get(0));
    } catch (FileNotFoundException e) {
    }

    return player;
  }

  private static Player create(String name, String rivalName) {
    Player player = newPlayer(name = getUniquePlayerName(name));
    player.setName(name);
    player.record().setRivalName(rivalName);
    return player;
  }

  private static Player newPlayer(String id) {
    Player player = new Player(id);
    players.put(id, player);

    return player;
  }

  private static String getUniquePlayerName(String attempt) {
    if (!players.containsKey(attempt) && !new File(JPokemonServer.savepath, attempt + ".jpkmn").exists())
      return attempt;

    int n = 0;
    for (; players.containsKey(attempt + n) || new File(JPokemonServer.savepath, attempt + n + ".jpkmn").exists(); n++)
      ;

    return attempt + n;
  }

  private static Map<String, Player> players = new HashMap<String, Player>();
  private static Map<JPokemonWebSocket, Player> connections = new HashMap<JPokemonWebSocket, Player>();
  private static Map<Player, Stack<Activity>> activities = new HashMap<Player, Stack<Activity>>();
}