package jpkmn.map;

/**
 * Areas that can house buildings, and may have water.
 * 
 * @author Zach
 */
public class City extends Area {
  public City(int cityNumber) {
    super(cityNumber);

    if (cityNumber == 0) {
      name("Pallet Town");
      buildings(Building.HOME);
      buildings(Building.EVENTHOUSE); // Map
      rivalBattle(1);

      Water w = new Water();
      w.add(129, 1, 5, 5, "oldrod"); // Magikarp
      w.add(60, 1, 10, 10, "goodrod"); // Poliwag
      w.add(118, 1, 10, 10, "goodrod"); // Goldeen
      w.add(60, 1, 15, 15, "superrod"); // Poliwag
      w.add(72, 1, 15, 15, "superrod"); // Tentacool
      w.add(120, 1, 10, 20, "superrod"); // Staryu
      water(w);
    }
    else if (cityNumber == 1) {
      name("Viridian City");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 8

      Water w = new Water();
      w.add(129, 1, 5, 5, "oldrod"); // Magikarp
      w.add(60, 1, 10, 10, "goodrod"); // Poliwag
      w.add(118, 1, 10, 10, "goodrod"); // Goldeen
      w.add(60, 1, 5, 15, "superrod"); // Poliwag
      w.add(72, 1, 15, 15, "superrod"); // Tentacool=
      water(w);
    }
    else if (cityNumber == 2) {
      name("Pewter City");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 1
      buildings(Building.EVENTHOUSE); // Science Museum
    }
    else if (cityNumber == 3) {
      name("Cerulean City");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 2
      buildings(Building.EVENTHOUSE); // Trade Poliwhirl for Jynx

      Water w = new Water();
      w.add(129, 1, 5, 5, "oldrod"); // Magikarp
      w.add(60, 1, 10, 10, "goodrod"); // Poliwag
      w.add(118, 1, 10, 10, "goodrod"); // Goldeen
      w.add(54, 1, 15, 15, "superrod"); // Psyduck
      w.add(98, 1, 15, 15, "superrod"); // Krabby
      w.add(118, 2, 15, 15, "superrod"); // Goldeen
      water(w);
    }
    else if (cityNumber == 4) {
      name("Vermilion City");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 3
      buildings(Building.EVENTHOUSE); // Trade Spearow for Farfetch'd
      buildings(Building.EVENTHOUSE); // Bike Voucher
      buildings(Building.EVENTHOUSE); // Old Rod

      Water w = new Water();
      w.add(129, 1, 5, 5, "oldrod"); // Magikarp
      w.add(60, 1, 10, 10, "goodrod"); // Poliwag
      w.add(118, 1, 10, 10, "goodrod"); // Goldeen
      w.add(72, 3, 10, 20, "superrod"); // Tentacool
      w.add(90, 3, 15, 15, "superrod"); // Shelder
      w.add(98, 3, 15, 15, "superrod"); // Krabby
      w.add(116, 1, 5, 5, "superrod"); // Horsea
      water(w);
    }
    else if (cityNumber == 5) {
      name("Lavender Town");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.EVENTHOUSE); // Pokemon Tower
    }
    else if (cityNumber == 6) {
      name("Celadon City");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 4
      buildings(Building.EVENTHOUSE); // Casino
      buildings(Building.EVENTHOUSE); // Department Store
      buildings(Building.EVENTHOUSE); // Mansion to get Eevee
    }
    else if (cityNumber == 7) {
      name("Celadon City");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 5
      buildings(Building.EVENTHOUSE); // Get psychic
      buildings(Building.EVENTHOUSE); // Fighting dojo
      buildings(Building.EVENTHOUSE); // Trade pokedoll for Mimic
    }
    else if (cityNumber == 8) {
      name("Fuchsia City");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 6
      buildings(Building.EVENTHOUSE); // good rod
      buildings(Building.EVENTHOUSE); // Safari Zone - Strength
    }
    else if (cityNumber == 9) {
      name("Cinnabar Island");
      buildings(Building.CENTER);
      buildings(Building.MART);
      buildings(Building.GYM); // TODO Gym 7
      buildings(Building.EVENTHOUSE); // get metronome
      buildings(Building.EVENTHOUSE); // lots of trades
    }
  }
}