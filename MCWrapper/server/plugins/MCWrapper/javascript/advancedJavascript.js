/* Default Config:
<?xml version="1.0" encoding="UTF-8"?>
<root>
  <WelcomeMessage color="blue">Use .welcome [message] and .welcome_color [color] to set the welcome message and color</WelcomeMessage>
  <MaxUpperCasePercent>40</MaxUpperCasePercent>
</root>
*/

var welcomeMessage;
var welcomeMessageColor;
var allowedCapsPercentage;

function ServerStart() {
  RefreshValues();
}

function RefreshValues() {
  //configs are saved as .xml files in the ./plugins/MCWrapper/configs/ folder

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
