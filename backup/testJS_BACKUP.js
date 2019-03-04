var name="testPlugin";

function ServerStart() {
	writeLine("Server started...");
	writeLine("Loaded "+name);
}

function ServerSaving() {
	writeLine("Server saving....");
	writeLine(name+" is saving as well...");
}

function ServerSaved() {
	writeLine("Server saved!");
	writeLine(name+" saved!");
}

function writeLine(msg) {
	Console.writeLine(msg, Console.getPrefix("JAVASCRIPTCONNECTOR"), Console.getPrefix("PLUGIN"));
}