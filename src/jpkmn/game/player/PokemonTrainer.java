package jpkmn.game.player;

import jpkmn.game.pokemon.Pokemon;

import org.jpokemon.pokemon.storage.PokemonStorageUnit;

public interface PokemonTrainer {
  public int id();

  public TrainerType type();

  public int cash();

  public void cash(int c);

  public String name();

  public void name(String s);

  public PokemonStorageUnit party();

  public boolean add(Pokemon p);

  public void notify(String... message);
}