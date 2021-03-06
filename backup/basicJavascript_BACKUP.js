function PlayerJoin(name) //PlayerJoin(name) is invoked when a player with name name joins the server
{
	Console.writeLine("Name '"+name+"'");
	Player.sendMessageTo(name, "Welcome to this server!", "gold"); //Send a welcome message to the new Player
}

function PlayerLeave(name) //PlayerLeave(name) is invoked when a player with name name leaves the server
{
	Console.writeLine("Name '"+name+"'");
	Console.beep(); //play a beep sound
}

function ChatReceived(name, message) //ChatReceived(name, message) is invoked when a Player says something
{
	if(message.toUpperCase() == message && message.length > 3) //check if message is caps only and longer than 3 characters long
		Server.runCommand("kick "+name+" Please do not use caps lock!"); //kick player cause he used caps

	Console.writeLine("Message: "+message+"\nIs .light: "+(message.toLowerCase().trim() == ".light"));
	if(message.toLowerCase() == ".light") {
		Player.runCommandAsPlayer(name, "setblock ~ ~ ~ minecraft:torch");
		Player.refreshPosition(name);
	}
	else if(message.split(' ')[0] == '.tpme')
		Server.runCommand("tp "+name+" "+message.split(' ')[1]);
}

function PlayerPosition(name, position) //PlayerPosition is invoked when a player is teleported to specific coordinates
{ //position is a ready to use string for command like tp or setblock (e. g. '10.3356 125.1234 46.6368')
	Server.runCommand("setblock "+position+" minecraft:torch"); //set block where the player stands to a torch so the player has light
}

function ServerStart() //ServerStart() is invoked when the server starts
{
	Console.writeLine("Example Plugin active!\nCommands: .tpme <player> and .light\nThis Plugin has an anti-caps lock function!"); //display message to server log
}

function ServerStop() //ServerStop() is invoked when the server stops
{
	Console.beep();
}
