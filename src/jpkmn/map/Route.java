package jpkmn.map;

import java.util.ArrayList;
import java.util.List;

import jpkmn.game.pokemon.Pokemon;

/**
 * Maintains a list of Pokemon which appear, and level range they appear in.
 * Routes are also capable of generating random Pokemon based on that data.
 * 
 * @author Zach
 */
public class Route extends Area {
  public Route(String name) {
    super(name);
    _low = 0;
    _high = 0;
    _species = new ArrayList<Integer>();
  }

  /**
   * Adds a species of Pokemon to the list that appear in this Route.
   * 
   * @param num The Pokemon number
   * @param flex An integer which represents the popularity of the Pokemon.
   */
  public void addSpecies(int num, int flex) {
    for (int i = 0; i < flex; i++)
      _species.add(num);
  }

  /**
   * Gets a list of all the available species numbers for this Route.
   * 
   * @return A list of integers
   */
  public List<Integer> getSpecies() {
    List<Integer> ret = new ArrayList<Integer>();

    for (Integer i : _species) {
      if (!ret.contains(i)) ret.add(i);
    }

    return ret;
  }

  /**
   * Sets the level range for this route.
   * 
   * @param low Lowest level encounter.
   * @param high Highest level encounter.
   */
  public void setRange(int low, int high) {
    _low = low;
    _high = high;
  }

  /**
   * Generates a random Pokemon that appears in this Route.
   * 
   * @return A new Pokemon
   */
  public Pokemon getRandomPokemon() {
    int number = _species.get((int) (Math.random() * _species.size()));
    int level = _low + (int) (Math.random() * (_high - _low + 1));

    return new Pokemon(number, level);
  }

  private int _low, _high;
  private List<Integer> _species;
}