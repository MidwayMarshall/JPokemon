package jpkmn.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jpkmn.game.base.AIInfo;
import jpkmn.game.base.ConnectionInfo;
import jpkmn.game.base.SpawnInfo;
import jpkmn.game.player.MockPlayer;
import jpkmn.game.pokemon.Pokemon;

/**
 * Represents a game area where the player can "be." Areas can contain
 * buildings, and may have water applied to them. Water usage is always
 * optional for the player.
 * 
 * @author Zach
 */
public class Area {
  public final int id;

  public Area(int areaNumber) {
    id = areaNumber;

    _spawner = SpawnInfo.getSpawner(areaNumber);
    _trainers = AIInfo.getAIForArea(areaNumber);
    _neighbors = ConnectionInfo.getConnectionMap(areaNumber);

    // Replace with areainfo
    _name = mapNumberToName();
    _center = mapNumberToCenter();
    _events = new ArrayList<Event>();
    _water = _spawner == null ? false : _spawner.spawn("oldrod") != null;
  }

  /**
   * Gets the name of the Area
   * 
   * @return Name of the Area
   */
  public String name() {
    return _name;
  }

  /**
   * Gets a new instance of the specified MockPlayer, if that MockPlayer is in
   * this area.
   * 
   * @param trainerID ID of the trainer
   * @return A new instance of the specified Trainer
   */
  public MockPlayer getTrainer(int trainerID) {
    AIInfo targetInfo = null;

    for (AIInfo curInfo : _trainers) {
      if (curInfo.getNumber() == trainerID) {
        targetInfo = curInfo;
        break;
      }
    }

    if (targetInfo == null) return null;

    return new MockPlayer(targetInfo.getType(), targetInfo.getName(),
        targetInfo.getCash(), targetInfo.getNumber());
  }

  /**
   * Gets the info for all Trainers in this area.
   * 
   * @return All Trainers' info
   */
  public List<AIInfo> trainers() {
    return _trainers;
  }

  /**
   * Gets the AreaConnection for the specified direction, if such an
   * AreaConnection exists
   * 
   * @param d Direction to travel in
   * @return The connection
   */
  public AreaConnection neighbor(Direction d) {
    if (_neighbors == null) return null;

    return _neighbors.get(d);
  }

  /**
   * Tells whether this Area has a Pokemon Center
   * 
   * @return Whether this Area has a Pokemon Center
   */
  public boolean center() {
    return _center;
  }

  /**
   * Tells whether this Area has fishable water
   * 
   * @return Whether this Area has fishable water
   */
  public boolean water() {
    return _water;
  }

  /**
   * Attempts to create a new instance of a Pokemon, which is spawnable in this
   * area, using the given tag. Specifying tag=null will spawn a wild Pokemon
   * from the tall grass.
   * 
   * @param tag Item used, i.e. "oldrod"
   * @return New instance of a wild Pokemon
   */
  public Pokemon spawn(String tag) {
    if (_spawner == null) return null;

    return _spawner.spawn(tag);
  }

  /**
   * Maps the number of this Area to a name
   * 
   * @return Name
   */
  private String mapNumberToName() {
    switch (id) {
    case 1:
      return "Pallet Town";
    case 2:
      return "Route-1";
    case 3:
      return "Viridian City";
    case 4:
      return "Route-2";
    case 5:
      return "Route-22";
    case 6:
      return "Viridian Forest";
    case 7:
      return "Pewter City";
    case 8:
      return "Route-3";
    case 9:
      return "Mt. Moon 1F";
    case 10:
      return "Mt. Moon B1F";
    case 11:
      return "Mt. Moon B2F";
    case 12:
      return "Route-4";
    case 13:
      return "Cerulean City";
    case 14:
      return "Route-24";
    case 15:
      return "Route-25";
    case 16:
      return "Route-9";
    case 17:
      return "Route-5";
    case 18:
      return "Saffron City";
    case 19:
      return "Route-8";
    case 20:
      return "Lavender Town";
    case 21:
      return "Route-10";
    case 22:
      return "Route-6";
    case 23:
      return "Vermilion City";
    case 24:
      return "Route-7";
    case 25:
      return "Celadon City";
    case 26:
      return "Route-11";
    case 27:
      return "Diglett's Cave";
    case 28:
      return "Route-12";
    case 29:
      return "Route-13";
    case 30:
      return "Route-14";
    case 31:
      return "Route-15";
    case 32:
      return "Fuchsia City";
    case 33:
      return "Route-18";
    case 34:
      return "Route-17 (Bike Path)";
    case 35:
      return "Route-16";
    case 36:
      return "Route-19";
    case 37:
      return "Route-20 (east)";
    case 38:
      return "Seafoam Islands 1F";
    case 39:
      return "Seafoam Islands B1F";
    case 40:
      return "Seafoam Islands B2F";
    case 41:
      return "Seafoam Islands B3F";
    case 42:
      return "Seafoam Islands B4F";
    case 43:
      return "Route-20 (west)";
    case 44:
      return "Cinnabar Island";
    case 45:
      return "Route-21";

    default:
      return "FakeName";
    }
  }

  /**
   * Maps the number of this area to a Pokemon Center status.
   * 
   * @return If this Area has a Pokemon Center
   */
  private boolean mapNumberToCenter() {
    switch (id) {
    case 1:
    case 3:
    case 7:
    case 13:
    case 18:
    case 20:
    case 23:
    case 25:
    case 32:
    case 44:
      return true;
    default:
      return false;
    }
  }

  private String _name;
  private boolean _water;
  private boolean _center;
  private List<Event> _events;
  private List<AIInfo> _trainers;
  private PokemonSpawner _spawner;
  private Map<Direction, AreaConnection> _neighbors;
}