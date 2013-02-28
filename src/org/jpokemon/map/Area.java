package org.jpokemon.map;

import java.util.ArrayList;
import java.util.List;

import org.jpokemon.pokemon.Pokemon;

public class Area {
  public Area(int id) {
    _id = id;

    _info = AreaInfo.get(id);
    _pokemon = WildPokemon.get(id);
  }

  public String name() {
    return _info.getName();
  }

  public Pokemon pokemon() {
    int totalFlex = 0;

    for (WildPokemon p : _pokemon)
      totalFlex += p.getFlex();

    totalFlex = (int) (totalFlex * Math.random());

    for (WildPokemon p : _pokemon) {
      if (totalFlex < p.getFlex()) {
        int level = p.getLevelmin() + (int) (Math.random() * (p.getLevelmax() - p.getLevelmin() + 1));
        return new Pokemon(p.getNumber(), level);
      }
      else
        totalFlex -= p.getFlex();
    }

    return null;
  }

  public List<Border> borders() {
    return _borders;
  }

  public void addBorder(Border b) {
    if (b == null || _borders.contains(b))
      return;

    _borders.add(b);
  }

  public void removeBorder(Border b) {
    if (b == null || !_borders.contains(b))
      return;

    _borders.remove(b);
  }

  public boolean equals(Object o) {
    if (o instanceof Area)
      return ((Area) o)._id == _id;

    return false;
  }

  private int _id;
  private AreaInfo _info;
  private List<WildPokemon> _pokemon;
  private List<Border> _borders = new ArrayList<Border>();
}