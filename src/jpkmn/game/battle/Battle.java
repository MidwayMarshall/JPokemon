package jpkmn.game.battle;

import java.util.ArrayList;
import java.util.List;

import jpkmn.game.Player;
import jpkmn.game.pokemon.Pokemon;
import jpkmn.game.pokemon.move.Move;
import jpkmn.game.pokemon.move.MoveStyle;
import jpkmn.game.pokemon.storage.AbstractParty;

public class Battle {
  public Battle() {
    _ready = false;
    _slots = new ArrayList<Slot>();
    _round = new Round(this);
  }

  public void add(AbstractParty p) {
    _slots.add(new Slot(p));
  }

  public void removeLoser(Slot s) {
    lostBattle(s.leader().owner());

    // REWARD PARTICIPANTS HERE

    remove(s);
  }

  public void remove(Slot s) {
    _slots.remove(s);
    if (_slots.size() == 1) {
      // Battle is over
    }
  }

  public List<Slot> getSlots() {
    return _slots;
  }

  public void start() {
    _ready = true;
  }

  public void fight(Slot slot) {
    if (!_ready || !_slots.contains(slot)) return;

    if (!slot.chooseMove()) return;
    if (!slot.chooseAttackTarget(getEnemySlotsForSlot(slot))) return;

    _round.add(slot.attack());

    if (_round.size() == _slots.size()) executeRound();
  }

  public void item(Slot slot) {
    if (!_ready || !_slots.contains(slot)) return;

    if (!slot.chooseItem()) return;
    if (!slot.chooseItemTarget(getEnemySlotsForSlot(slot))) return;

    _round.add(slot.item());

    if (_round.size() == _slots.size()) executeRound();
  }

  public void swap(Slot slot) {
    if (!_ready || !_slots.contains(slot)) return;

    if (!slot.chooseSwapPosition()) return;

    _round.add(slot.swap());

    if (_round.size() == _slots.size()) executeRound();
  }

  public void run(Slot slot) {
    if (!_ready || !_slots.contains(slot)) return;

    _round.add(slot.run(this));

    if (_round.size() == _slots.size()) executeRound();
  }

  private void executeRound() {
    Round current = _round;
    _round = new Round(this);
    current.play();
    executeConditionEffects();
  }

  private void executeConditionEffects() {
    for (Slot slot : _slots) {
      notifyAll(slot.leader().condition.applyEffects());
    }
  }

  public static int computeDamage(Move move, Pokemon victim) {
    Pokemon user = move.pkmn;

    double damage = 1.0, L = user.level(), A = 1.0, P = move.power(), D = 0, STAB = move
        .STAB(), E = move.effectiveness(victim), R = Math.random() * .15 + .85;

    if (move.style() == MoveStyle.SPECIAL) {
      A = user.stats.stk.cur();
      D = victim.stats.sdf.cur();
    }
    else if (move.style() == MoveStyle.PHYSICAL) {
      A = user.stats.atk.cur();
      D = victim.stats.def.cur();
    }
    else if (move.style() == MoveStyle.OHKO) {
      A = 10000000;
      D = 1;
    }
    else if (move.style() == MoveStyle.DELAY) {
      if (user.stats.atk.cur() > user.stats.stk.cur())
        A = user.stats.atk.cur();
      else
        A = user.stats.stk.cur();
      if (victim.stats.def.cur() > victim.stats.sdf.cur())
        D = victim.stats.def.cur();
      else
        D = victim.stats.sdf.cur();
    }

    damage = (((2.0 * L / 5.0 + 2.0) * A * P / D) / 50.0 + 2.0) * STAB * E * R;

    if (damage < 1 && E != 0) damage = 1;

    return (int) damage;
  }

  /**
   * Calculates the confused damage a Pokemon does to itself. This method
   * exists here to unify all damage calculations.
   * 
   * @param p Pokemon to calculate for
   * @return Damage done
   */
  public static int confusedDamage(Pokemon p) {
    // For more info, see computeDamage
    double L = p.level(), A = p.stats.atk.cur(), P = 40, D = p.stats.def.cur(), STAB = 1, E = 1, R = 1.00;
    return (int) ((((2.0 * L / 5.0 + 2.0) * A * P / D) / 50.0 + 2.0) * STAB * E * R);
  }

  void notifyAll(String... s) {
    for (Slot slot : _slots) {
      slot.leader().notify(s);
    }
  }

  private void lostBattle(Player p) {
    p.setCash(p.cash() / 2);
  }

  // Later used to implement teams
  private List<Slot> getEnemySlotsForSlot(Slot slot) {
    List<Slot> enemySlots = new ArrayList<Slot>();

    for (Slot s : _slots)
      if (s != slot) enemySlots.add(s);

    return enemySlots;
  }

  private boolean _ready;
  private List<Slot> _slots;
  private Round _round;
}