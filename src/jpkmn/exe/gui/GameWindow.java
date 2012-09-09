package jpkmn.exe.gui;

import javax.swing.JFrame;

import jpkmn.exe.gui.battle.BattleView;
import jpkmn.exe.gui.pokemonupgrade.PokemonUpgradeView;
import jpkmn.exe.gui.world.WorldView;
import jpkmn.img.ImageFinder;

public class GameWindow extends JFrame {
  public GameWindow(int playerID) {
    _playerID = playerID;
    _inbox = new MessageView();
    _battle = new BattleView();
    _main = new WorldView(this);
    _upgrade = new PokemonUpgradeView(this);

    setResizable(false);
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setIconImage(ImageFinder.find("main-icon").getImage());

    setVisible(true);
  }

  public int playerID() {
    return _playerID;
  }

  public MessageView inbox() {
    return _inbox;
  }

  public void showMain() {
    show(_main);
  }

  public void showBattle(int battleID, int slotID) {
    _battle.setup(battleID, slotID);

    show(_battle);
  }

  public void showUpgrade(int partyIndex) {
    _upgrade.setup(partyIndex);

    show(_upgrade);
  }

  public void refresh() {
    _active.refresh();

    setVisible(false);
    setVisible(true);
  }

  private void show(JPokemonView view) {
    if (_active != null) remove(_active);

    _active = view;
    add(_active);

    setSize(_active.dimension());

    refresh();
  }

  private int _playerID;
  private WorldView _main;
  private BattleView _battle;
  private MessageView _inbox;
  private JPokemonView _active;
  private PokemonUpgradeView _upgrade;
  private static final long serialVersionUID = 1L;
}