# MCWrapper
This is a Minecraft server wrapper

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
Player.instance.sendMessageTo("notch", "{"text":"Welcome to this server!","color":"gold"}");
Player.instance.sendMessageTo("notch", "Welcome to this server!", "gold");
Player.instance.refreshPosition("notch");
Player.instance.runCommandAsPlayer("notch", "say I Like Minecraft");
```

### Server
```java
Server.instance.broadcastMessage("{"text":"The server is saving the world.","color":"red"}");
Server.instance.broadcastMessage("The server is saving the world.", "red");
Server.instance.runCommand("save-all");
```

### Console
```java
Console.instance.writeLine("Write somthing to the console (and log if enabled)");
Console.instance.write("Write something to the console (and log if enabled) without new line");
Console.clear();
Console.beep();
```

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

function test() {
	Server.runCommand("kick "+name+" Please do not use caps lock!"); //kick player cause he used caps

	Console.writeLine("Name '"+name+"'"); // Write to console
	Console.beep(); // Play a beep sound

	Player.sendMessageTo(name, "Welcome to this server!", "gold"); //Send a welcome message to the new Player
	Player.refreshPosition(name);
}
```
