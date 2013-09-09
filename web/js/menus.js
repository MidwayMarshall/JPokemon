(function($) {
  me.menu = me.menu || {};

  me.menu.Window = me.ui.Panel.extend({
    init : function(x, y) {
      this.parent({
        x : x,
        y : y,
        xlayout : 'fill',
        ylayout : 'fit',
        color : 'black',
        border : 'white',
        opacity : .7
      });

      this.frame = new me.ui.Panel({ xlayout : 'fit', ylayout : 'center' });
      this.icon = new me.ui.Button();
      this.frame.add(this.icon);
      this.title = new me.ui.Label({ fontSize : 12, fontName : 'verdana' });
      this.frame.add(this.title);
      this.add(this.frame);

      this.pane = new me.ui.Panel({ padding : 0, border : 'white', xlayout : 'fill', ylayout : 'fit' });
      this.add(this.pane);
    },
  });

  me.menu.StatefulWindow = me.menu.Window.extend({
    init : function(x, y) {
      this.parent(x, y);

      this.icon.onClick = this.chooseState.bind(this);
      this.states = {};
    },

    addState : function(name, icon, view) {
      this.states[name] = new me.ui.Toggle({
        xlayout : 'fit', ylayout : 'center', toggleColor : false, untoggleColor : false, padding : 0,
        onToggleChange : (function(name) {
          return function() { this.setState(name); };
        })(name).bind(this)
      });

      this.states[name].add(new me.ui.Icon({ image : icon }));
      this.states[name].add(new me.ui.Label({ text : name }));
      this.states[name].icon = icon;
      this.states[name].view = view;
    },

    chooseState : function() {
      this.title.setText('');
      this.icon.setImage('circle_gray');
      this.pane.clear();

      for (var state in this.states) {
        this.pane.add(this.states[state]);
      }
    },

    setState : function(name) {
      this.title.setText(name);
      this.icon.setImage(this.states[name].icon);
      this.pane.clear();
      this.pane.add(this.states[name].view);
    }
  });

  me.menu.FriendsLauncher = me.ui.Toggle.extend({
    init : function() {
      this.parent({
        x : 10,
        y : 512 - 20,
        border : 'white',
        toggleStyle : 'toggle',
        padding : 0,
        opacity : .7
      });
      this.add(new me.ui.Label({ text : 'Friends' }));

      this.friendsWindow = new me.menu.FriendsWindow();
      game.subscribe('friends', this.friendsWindow);
    },

    onToggleChange : function(visible) {
      if (visible) {
        this.friendsWindow.show();
        this.friendsWindow.refresh();
      }
      else {
        this.friendsWindow.hide();
      }
    }
  });

  me.menu.FriendsWindow = me.menu.StatefulWindow.extend({
    init : function() {
      this.parent(10, 50);

      this.frame.add(new me.ui.Button({ image : 'plus_gray', onClick : this.showInputWindow.bind(this) }));
      this.frame.add(new me.ui.Button({ image : 'refresh_gray', onClick : this.sendLoadRequest }));

      this.inputWindow = new me.ui.Panel({ x : 200, y : 200, ylayout : 'fit', border : 'white', color : 'black', opacity : .7 });
      this.inputWindowLabel = new me.ui.Label({ text : 'Add Friend' });
      this.inputWindow.add(this.inputWindowLabel);
      this.inputWindowInputBox = new me.ui.InputBox({ width : 100, onEnter : this.requestFriend.bind(this) });
      this.inputWindow.add(this.inputWindowInputBox);

      this.friends = new me.ui.Scrollable({ padding : 0, height : 100, width: 130 });
      this.blocked = new me.ui.Scrollable({ padding : 0, height : 100, width: 130 });
      this.pending = new me.ui.Scrollable({ padding : 0, height : 100, width: 130 });
      
      this.addState('Friends List', 'circle_green', this.friends);
      this.addState('Blocked List', 'circle_red', this.blocked);
      this.addState('Pending List', 'circle_blue', this.pending);

      this.setState('Friends List');
    },

    hide : function() {
      this.parent();
      this.inputWindow.hide();
    },

    showInputWindow : function() {
      this.inputWindow.show();
    },

    friendsNamePanel : function(name) {
      var namePanel = new me.ui.Panel({ opacity : 0, padding : 0, xlayout:'fill' });
      namePanel.add(new me.ui.Label({ text : name }));
      return namePanel;
    },

    blockedNamePanel : function(name) {
      var namePanel = new me.ui.Panel({ opacity : 0, padding : 0, xlayout:'fill' });
      namePanel.add(new me.ui.Label({ text : name }));
      return namePanel;
    },

    pendingNamePanel : function(name) {
      var namePanel = new me.ui.Panel({ opacity : 0, padding : 0, xlayout:'fit', ylayout : 'center' });

      var acceptButton = new me.ui.Button({
        image : 'plus_green',
        onClick : (function(name) {
          return function() {
            this.acceptFriend(name);
          };
        })(name).bind(this)
      });
      namePanel.add(acceptButton);

      namePanel.add(new me.ui.Label({ text : name }));

      return namePanel;
    },

    refresh : function() {
      game.send({
        load : 'friends'
      });
    },

    requestFriend : function() {
      var name = this.inputWindowInputBox.getText();

      if (name) {
        game.send({
          service : 'friends',
          configure : 'add',
          name : name
        });
        this.inputWindowInputBox.setText('');
      }

      this.inputWindow.hide();
    },

    acceptFriend : function(name) {
      game.send({
        service : 'friends',
        configure : 'accept',
        name : name
      });
    },

    dispatch : function(json) {
      this.friends.clear();
      for (var i = 0; i < json.friends.length; i++) {
        this.friends.add(this.friendsNamePanel(json.friends[i]));
      }

      this.blocked.clear();
      for (var i = 0; i < json.blocked.length; i++) {
        this.blocked.add(this.blockedNamePanel(json.blocked[i]));
      }

      this.pending.clear();
      for (var i = 0; i < json.pending.length; i++) {
        this.pending.add(this.pendingNamePanel(json.pending[i]));
      }
    }
  });

  me.menu.BattleLobbyLauncher = me.ui.Toggle.extend({
    init : function() {
      this.parent({
        x : 75,
        y : 512 - 20,
        border : 'white',
        toggleStyle : 'toggle',
        padding : 0,
        opacity : .7
      });
      this.add(new me.ui.Label({ text : 'Battle' }));

      this.lobbyWindow = new me.menu.BattleLobbyWindow();
      game.subscribe('lobby', this.lobbyWindow);
    },

    onToggleChange : function(focus) {
      this.parent();

      if (focus) {
        this.lobbyWindow.show();
        this.lobbyWindow.refresh();
      }
      else {
        this.lobbyWindow.hide();
      }
    }
  });

  me.menu.BattleLobbyWindow = me.menu.StatefulWindow.extend({
    init : function() {
      this.parent(125, 50);

      this.frame.add(new me.ui.Button({ image : 'refresh_gray', onClick : this.refresh.bind(this) }));

      this.inputWindow = new me.ui.Panel({ x : 200, y : 200, ylayout : 'fit', border : 'white', color : 'black', opacity : .7 });
      this.inputWindow.label = new me.ui.Label({ text : 'Enter name' });
      this.inputWindow.add(this.inputWindow.label);
      this.inputWindow.inputBox = new me.ui.InputBox({ width : 200, onEnter : this.addPlayer.bind(this) });
      this.inputWindow.add(this.inputWindow.inputBox);
      this.inputWindow.radio = new me.ui.RadioGroup({ xlayout : 'fit' });
      this.inputWindow.add(this.inputWindow.radio);

      this.lobbyView = new me.ui.Panel({ ylayout : 'fit', height : 100, width: 130, padding : 0 });

      this.lobbyView.controls = new me.ui.Panel({ xlayout : 'fit', ylayout : 'center', width : 130});
      this.lobbyView.add(this.lobbyView.controls);

      this.lobbyView.controls.addTeamButton = new me.ui.Button({ image : 'plus_green', text : 'Team', onClick : this.addTeam.bind(this) });
      this.lobbyView.controls.addPlayerButton = new me.ui.Toggle({ toggleStyle : 'toggle', padding : 0, xlayout : 'fit', ylayout : 'center', untoggleColor : false, onToggleChange : this.toggleInputWindow.bind(this) });
      this.lobbyView.controls.addPlayerButton.add(new me.ui.Icon({ image : 'plus_green', padding : 0 }));
      this.lobbyView.controls.addPlayerButton.add(new me.ui.Label({ text : 'Player' }));
      this.lobbyView.controls.closeButton = new me.ui.Button({ image : 'gears_gray', text : 'Configure', onClick : this.closeLobby.bind(this) });
      this.lobbyView.controls.openButton = new me.ui.Button({ image : 'send', text : 'Send', onClick : this.openLobby.bind(this) });
      this.lobbyView.controls.startButton = new me.ui.Button({ image : 'check_green', text : 'Start', onClick : this.startBattle.bind(this) });
      this.lobbyView.controls.acceptBattleButton = new me.ui.Button({ image : 'check_green', text : 'Accept', onClick : this.acceptBattle.bind(this) });
      this.lobbyView.controls.rejectBattleButton = new me.ui.Button({ image : 'x_red', text : 'Reject', onClick : this.rejectBattle.bind(this) });

      this.lobbyView.teamsContainer = new me.ui.Panel({xlayout : 'fit', ylayout : 'fill' });
      this.lobbyView.add(this.lobbyView.teamsContainer);

      this.pendingView = new me.ui.Scrollable({ padding : 0, height : 100, width: 130 });

      this.lobbies = {};
      this.currentHostView = null;

      this.addState('My Lobby', 'circle_green', this.lobbyView);
      this.addState('Pending List', 'circle_blue', this.pendingView);

      this.setState('My Lobby');
    },

    hide : function() {
      this.parent();
      this.inputWindow.hide();
    },

    toggleInputWindow : function(active) {
      if (active) {
        this.inputWindow.show();
      }
      else {
        this.inputWindow.hide();
      }
    },

    setState : function(name) {
      this.parent(name);

      if (name === 'My Lobby') {
        this.showLobbyDetail(game.playerName);
      }
    },

    addPlayer : function() {
      var name = this.inputWindow.inputBox.getText().trim();
      var team = this.inputWindow.radio.indexOf(this.inputWindow.radio.getSelectedItem());

      game.send({
        service : 'lobby',
        configure : 'addplayer',
        name : name,
        team : team,
      });

      this.inputWindow.inputBox.setText('');
    },

    removePlayer : function(name) {
      game.send({
        service : 'lobby',
        configure : 'removeplayer',
        name : name
      });
    },

    addTeam : function() {
      game.send({
        service : 'lobby',
        configure : 'addteam'
      });
    },

    closeLobby : function() {
      game.send({
        service : 'lobby',
        configure : 'openstate',
        openstate : false
      });
    },

    openLobby : function() {
      game.send({
        service : 'lobby',
        configure : 'openstate',
        openstate : true
      });
    },

    acceptBattle : function() {
      game.send({
        service : 'lobby',
        host : this.currentHostView,
        respond : 'accept'
      });
    },

    rejectBattle : function() {
      game.send({
        service : 'lobby',
        host : this.currentHostView,
        respond : 'reject'
      });
    },

    startBattle : function() {
      console.log("todo");
    },

    showLobbyDetail : function(name) {
      this.currentHostView = name;
      var json = this.lobbies[name];

      if (!json) {
        game.send({
          load : 'lobby',
          host : name
        });

        return;
      }
      
      this.lobbyView.teamsContainer.clear();
      this.inputWindow.radio.clear();
      this.lobbyView.controls.clear();

      if (json.host === game.playerName) {
        if (json.open) {
          this.lobbyView.controls.add(this.lobbyView.controls.closeButton);
          this.lobbyView.controls.add(this.lobbyView.controls.startButton);
        }
        else {
          this.lobbyView.controls.add(this.lobbyView.controls.addTeamButton);
          this.lobbyView.controls.add(this.lobbyView.controls.addPlayerButton);
          this.lobbyView.controls.add(this.lobbyView.controls.openButton);
        }
      }
      else {
        this.lobbyView.controls.add(this.lobbyView.controls.acceptBattleButton);
        this.lobbyView.controls.add(this.lobbyView.controls.rejectBattleButton);

        this.pane.clear();
        this.pane.add(this.lobbyView);
      }

      for (var team = 0; team < json.teams.length; team++) {
        var teamPanel = new me.ui.Panel({ xlayout : 'fill', ylayout : 'fit', padding : 0, width : 25, height : 100 });
        teamPanel.add(new me.ui.Label({ text : 'Team ' + team }));
        teamPanel.add(new me.ui.Panel({ border : 'white', opacity : .7 }));
        this.lobbyView.teamsContainer.add(teamPanel);
        this.inputWindow.radio.addLabel({ text : 'Team '+ team });

        for (var name = 0; name < json.teams[team].length; name++) {
          var image;
          var onClick;

          if (json.open) {
            if (json.responses[json.teams[team][name]] === 'yes') {
              image = 'check_green';
            }
            else if (json.responses[json.teams[team][name]] === 'no') {
              image = 'x_red';
            }
            else {
              image = 'clock';
            }
          }
          else {
            image = 'minus_red';
            onClick = (function(name) {
              return function() {
                this.removePlayer(name);
              }
            })(json.teams[team][name]).bind(this);
          }

          var panel = new me.ui.Button({ 
            image : image,
            text : json.teams[team][name],
            onClick : onClick
          });

          teamPanel.add(panel);
        }
      }
    },

    refresh : function() {
      game.send({
        load : 'lobby',
        host : this.currentHostView
      });
    },

    dispatch : function(json) {
      this.lobbies[json.host] = json;

      if (json.host === this.currentHostView) {
        this.showLobbyDetail(json.host);
      }

      if (json.pending) {
        this.pendingView.clear();
        for (var i = 0; i < json.pending.length; i++) {
          var pendingName = json.pending[i];

          var namePanel = new me.ui.Button({
            text : pendingName,
            onClick : (function(pendingName) {
              return function() {
                this.showLobbyDetail(pendingName);
              }
            })(pendingName).bind(this)
          });

          this.pendingView.add(namePanel);
        }
      }
    }
  });

  me.menu.MessagesArea = me.ui.Scrollable.extend({
    init : function() {
      this.parent({
        x : me.video.getWidth() - 100,
        y : 100,
        padding : 15,
        height : me.video.getHeight(),
        ylayout : 'fit',
        opacity : 0,
        color : 'black'
      });

      game.subscribe('notification', this);
    },

    dispatch : function(json) {
      var panel = new me.ui.Panel({
        xlayout : 'fit',
        ylayout : 'center',
        padding : 0,
        opacity : .7,
        color : 'black'
      });

      var button = new me.ui.Button({
        image : 'x_gray',
        opacity : .7,
        onClick : (function() {
          this.remove(panel);
        }).bind(this)
      });
      panel.add(button);

      var timer = new me.ui.Timer({
        timer : 15,
        onComplete : (function() {
          this.remove(panel);
        }).bind(this)
      });
      panel.add(timer);

      var text = new me.ui.Button({
        text : json.text
      });
      panel.add(text);

      this.add(panel);
    },

    update : function() {
      if (this.needsUpdate) {
        this.parent();

        this._forEachChild(function(child) {
          child.config.x = 100 - 15 - child.rect.width;
        });

        return true;
      }

      return this.parent();
    },

    doLayout : function() {
      this.parent();
    }
  });
})(window);