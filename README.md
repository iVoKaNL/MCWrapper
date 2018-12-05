# MCWrapper
This is a Minecraft server wrapper

## Javascript plugin example
```javascript
function OnCommand(name, arg) {} //OnCommand(name, args) is invoked when a player says something beginning with an '!'

function PlayerJoin(name) {} //PlayerJoin(name) is invoked when a player with name name joins the server
function PlayerLeave(name) {} //PlayerLeave(name) is invoked when a player with name name leaves the server
function ChatReceived(name, message) {} //ChatReceived(name, message) is invoked when a Player says something
function PlayerPosition(name, position) {
	//PlayerPosition is invoked when a player is teleported to specific coordinates
	//position is a ready to use string for command like tp or setblock (e. g. '10.3356 125.1234 46.6368')
}

function ServerStart() {} //ServerStart() is invoked when the server starts
function ServerStop() {} //ServerStop() is invoked when the server stops

Server.runCommand("kick "+name+" Please do not use caps lock!"); //kick player cause he used caps

Console.writeLine("Name '"+name+"'"); // Write to console
Console.beep(); // Play a beep sound

Player.sendMessageTo(name, "Welcome to this server!", "gold"); //Send a welcome message to the new Player
Player.refreshPosition(name);
```
