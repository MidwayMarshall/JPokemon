game.control('pokemonsprite', {
  refs : [
  ],
  api : {
    constructor : function() {
    },

    update : function(json) {
      this.view.css('background-position', '0 -' + (80 * (json.pokemonNumber - 1)));
    }
  }
});