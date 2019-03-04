# MCWrapper
This is a Minecraft server wrapper

## Javascript plugin example
#### WHEN YOU RELOAD PLUGINS, ServerStart wont be called (because server is already started). So if your javascript plugin does something importand in that function note that it's possible you'll get a error!
### Basic
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

function test() {
	Server.runCommand("kick "+name+" Please do not use caps lock!"); //kick player cause he used caps

	Console.writeLine("Name '"+name+"'"); // Write to console
	Console.beep(); // Play a beep sound

	Player.sendMessageTo(name, "Welcome to this server!", "gold"); //Send a welcome message to the new Player
	Player.refreshPosition(name);
}
```

### Advanced
```javascript
var welcomeMessage;
var welcomeMessageColor;
var allowedCapsPercentage;

function ServerStart() {
	//configs are saved as .xml files in the plugins/MCWrapper/configs/ folder

	//Welcome Message:
	welcomeMessage = Config.getValue("WelcomeMessage") || "Welcome to this server"; //set value of 'welcomeMessage' to the value we get from the config
	welcomeColor = Config.getAttribute("WelcomeMessage", "color") || "red"; //set value of 'welcomeColor' to the value we get from the config
	allowedCapsPercent = Config.getValue("MaxUpperCasePercent") || "40"; //set value of 'allowedCapsPercent' to the value we get from the config
}

function ChatReceived(name, message) //ChatReceived(name, message) is invoked when a Player says something
{
	var args = message.split(' ');

	if(args[0] == ".welcome" && args.length > 1) //if command is 'welcome'
	{
		//get welcome message
		var welcome = args[1];
		if(args.length > 2)
			for(var i = 2; i < args.length; i++)
				welcome += " "+args[i];
		Config.setValue("WelcomeMessage", welcome); //set new welcome message in config
		Config.saveConfig(); //save the config
		RefreshValues(); //refresh the variables
		Player.sendMessageTo(name, "Welcome Message set!", "dark_blue"); //send sucess message to sender
	}
	else if(args[0] == ".welcome_color" && args.length == 2) //if command is 'welcome_color'
	{
		Config.setAttribute("WelcomeMessage", "color", args[1]); //set new welcome message color in config
		Config.saveConfig(); //save the config
		RefreshValues(); //refresh the variables
		Player.sendMessageTo(name, "Welcome Message Color set to this color!", args[1]); //send sucess message to sender
	}
	else if(args[0] == ".welcome_refresh") //if command is 'welcome_refresh'
  {
		RefreshValues(); //refresh the variables
    Player.sendMessageTo(name, "Refreshed", "purple");
  }

	var upper = 0; //temporary variable with upper case letter count
	for(var i = 0; i < message.length; i++) //loop through all letters in message
	    if (message.substring(i, i + 1).toUpperCase() == message.substring(i, i + 1) && (message.substring(i, i + 1).toUpperCase() != message.substring(i, i + 1).toLowerCase()))
                //  ^^  if letter is uppercase letter -  second part after && elimates things like numbers and special characters that have no "case"
			upper++; //add 1 to uppercase
	if(message.length > 5 && upper/message.length*100 > allowedCapsPercent) //if uppercase percentage is more than allowed value
		Server.runCommand("kick "+name+" A maximum of "+allowedCapsPercent+"% Upper Case Letters is allowed you had "+upper/message.length*100+"%"); //kick player
}

function PlayerJoin(name) //PlayerJoin(name) is invoked when a player with name name joins the server
{
	Player.sendMessageTo(name, welcomeMessage, welcomeColor); //Send the welcome message to the new Player
}
```

## API
### Config
```java
config = new Config(new File("testjeuh.xml"));

Map<String, Config.XMLValues> values = new LinkedHashMap<>();
values.put("JavaFile", new Config().new XMLValues("server.jar", "required", "yes"));
values.put("Argument", new Config().new XMLValues("-Xmx1G -Xms1G -jar", "required", "yes"));
values.put("UsePlugins", new Config().new XMLValues("true", "required", "yes"));
config.setValues(values);

config.setValue("key", "value");
config.setChildValue("parentKey", "childKey", "value");
config.getValue("key"); // returns "value"
config.getChildValue("parentKey", "childKey"); // optional arguments are parentIndex and childIndex
config.setAttributes("key", "Map<String, String> attributes"); // also setChildAttributes (but with the parentKey)
config.createElement("key"); // createChildElement
config.elementExists("key"); // returns boolean
config.elementExistsAndNotEmpty("key"); // return boolean

config.saveConfig();

// This is not finished (there are a lot more methods that need to be added to this little Wiki, but you can look at the Config.java)
```

### Player
```java
Player.instance().sendMessageTo("notch", "{"text":"Welcome to this server!","color":"gold"}");
Player.instance().sendMessageTo("notch", "Welcome to this server!", "gold");
Player.instance().refreshPosition("notch");
Player.instance().runCommandAsPlayer("notch", "say I Like Minecraft");
```

### Server
```java
Server.instance().broadcastMessage("{"text":"The server is saving the world.","color":"red"}");
Server.instance().broadcastMessage("The server is saving the world.", "red");
Server.instance().runCommand("save-all");
```

### Console
```java
Console.instance().writeLine("Write somthing to the console (and log if enabled)", Console.PREFIX.MAIN, Console.PREFIX.INFO);
Console.instance().write("Write something to the console (and log if enabled) without new line", Console.PREFIX.MAIN, Console.PREFIX.INFO);
Console.clear();
Console.beep();
```

## Config files
### MCWrapper.xml (in root (same directory as MCWrapper.jar))
```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <ServerFile>minecraft.jar</ServerFile>
    <JavaExecutable>java</JavaExecutable>
    <JavaArguments>-Xms1G -Xmx1G</JavaArguments>
    <ServerJarArguments>nogui</ServerJarArguments>
    <UsePlugins>true</UsePlugins>
    <WorkingDirectory usecustom="true">server</WorkingDirectory> <!-- default false -->
    <Logging enable="true"> <!-- default false -->
        <LogServerOutput>true</LogServerOutput>
        <LogDebugOutput>false</LogDebugOutput>
        <FileDirectory>plugins/MCWrapper/logs</FileDirectory>
        <FileName>latest.log</FileName>
    </Logging>
    <Debug>false</Debug>
</root>
```

### EventValidators.xml (plugins/MCWrapper/configs)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <ServerStart>
        <CheckFor>[Server thread/INFO]: Starting minecraft server</CheckFor>
    </ServerStart>
    <ServerStop>
        <CheckFor>[Server Shutdown Thread/INFO]: Stopping server</CheckFor>
    </ServerStop>
    <ServerDone>
        <CheckFor>[Server thread/INFO]: Done</CheckFor>
    </ServerDone>
    
    <ServerSaving>
        <CheckFor>[Server thread/INFO]: Saving the game</CheckFor>
    </ServerSaving>
    <ServerSaved>
        <CheckFor>[Server thread/INFO]: </CheckFor>
        <CheckFor> Saved the game</CheckFor>
    </ServerSaved>
    <ServerSaveOFF>
        <CheckFor>[Server thread/INFO]: </CheckFor>
        <CheckFor> Automatic saving is now disabled</CheckFor>
    </ServerSaveOFF>
    <ServerSaveON>
        <CheckFor>[Server thread/INFO]: </CheckFor>
        <CheckFor> Automatic saving is now enabled</CheckFor>
    </ServerSaveON>
    
    <PlayerJoin>
        <CheckFor>[Server thread/INFO]: </CheckFor>
        <CheckFor> joined the game</CheckFor>
    </PlayerJoin>
    <PlayerLeave>
        <CheckFor>[Server thread/INFO]: </CheckFor>
        <CheckFor> left the game</CheckFor>
    </PlayerLeave>
    <PlayerChat>
        <CheckFor>[Server thread/INFO]: </CheckFor>
        <CheckFor>&lt;</CheckFor>
        <CheckFor>&gt;</CheckFor>
    </PlayerChat>
    <PlayerPosition>
        <CheckFor>[Server thread/INFO]: </CheckFor>
        <CheckFor>Teleported</CheckFor>
    </PlayerPosition>
    <PlayerUUID>
        <CheckFor>[User Authenticator #</CheckFor>
        <CheckFor>/INFO]: UUID of player </CheckFor>
    </PlayerUUID>
    
    <example>
        <one>example needs to be an event name</one>
        <two>But the child elements of an event can be named whatever you want</two>
        <ChildElement>This can be named whatever you want, all child elements will be read</ChildElement>
        <four>Everything needs to be closed properly for the program to not throw any errors</four>
    </example>
</root>
```
