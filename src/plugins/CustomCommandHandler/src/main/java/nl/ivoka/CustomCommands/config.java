package nl.ivoka.CustomCommands;

import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.API.Config;
import nl.ivoka.CustomCommandHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.Main;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;

public class config implements CustomCommand {
    private CustomCommandHandler c;
    private String[] args;
    private static String selectedConfigFile = "MCWrapper.xml";
    private static Config selectedConfig = Main.getConfig();

    public config(CommandEvent e, CustomCommandHandler c) {
        if (e!=null&&c!=null) {
            this.c = c;
            this.args = e.args;

            if (args.length == 0) {
                help();
            } else {
                if (args[0].equals("list")) list();
                else if (args[0].equals("select")) select();
                else if (args[0].equals("show")) show();
                else if (args[0].equals("save")) save();
                else if (args[0].equals("getvalue")) getvalue();
                else if (args[0].equals("setvalue")) setvalue();
                else if (args[0].equals("getchildvalue")) getchildvalue();
                else if (args[0].equals("setchildvalue")) setchildvalue();
                else help();
            }
        }
    }

    public void help() {
        writeInfo("Usage 'config':");
        writeInfo("  !config help - Show this help menu");
        writeInfo("  !config list - Shows a list of all config files");
        writeInfo("  !config select [config.xml] - Selects a config file to work with, if this is never used or [config.xml] is not given use the standard config file (MCWrapper.xml)");
        writeInfo("  !config show - Shows the config file in command line");
        writeInfo("  !config save - Saves the config");

        writeInfo("  !config getvalue <key> [index] - Get the value of 'key'");
        writeInfo("  !config setvalue <key> <value> [index] - Set the value of 'key' to 'value', if value is 'null' the 'key' element will be removed");

        writeInfo("  !config getchildvalue <parentKey> <childKey> [parentIndex] [childIndex] - Get the value of 'childKey' inside the 'parentKey'");
        writeInfo("  !config setchildvalue <parentKey> <childKey> <value> [parentIndex] [childIndex] - Set the value of 'childKey' inside the 'parentKey' to 'value'");
        // TODO Add more commands
    }

    private void list() {
        File[] configFiles = Main.getConfigsDir().listFiles(name -> name.getPath().toLowerCase().endsWith(".xml"));

        if (configFiles==null)
            writeError("No folder or files found inside config folder.");
        else {
            StringBuilder msg = new StringBuilder("Config files (" + configFiles.length + "): ");
            for (File configFile : configFiles) {
                msg.append(configFile.getName());
                msg.append(", ");
            }
            msg = new StringBuilder(msg.substring(0, msg.length() - 2));

            writeInfo(msg.toString());
        }
    }

    private void select() {
        if (args.length==1) {
            selectedConfigFile = "MCWrapper.xml";
            selectedConfig = Main.getConfig();
            writeInfo("Default config selected (MCWrapper.xml)");
        } else {
            String configFile = args[1];
            if (!configFile.toLowerCase().endsWith(".xml"))
                configFile+=".xml";

            try {
                if (new File(Main.getConfigsDir() + "/" + configFile).exists()) {
                    selectedConfigFile = configFile;
                    selectedConfig = new Config(Main.getConfigsDir() + "/" + selectedConfigFile);
                    writeInfo(configFile + " selected.");
                } else
                    writeInfo("That config file does not exist.");
            } catch (DocumentException | IOException e) { writeError(e); }
        }
    }

    private void show() {
        writeInfo("Config ("+
                selectedConfigFile+
                "): \n\n"+
                selectedConfig.toString()+
                "\n");
    }

    private void save() {
        try {
            selectedConfig.saveConfig();
            writeInfo("Config saved to "+selectedConfigFile);
        } catch (IOException e) { writeError(e); }
    }

    private void getvalue() {
        if (args.length < 2)
            writeInfo("Invalid use of '!config getvalue' command, use '!config' or '!config help' to get help for this command.");
        else {
            try {
                if (args.length == 2)
                    writeInfo(args[1] + "[0]=" + selectedConfig.getValue(args[1]));
                else if (args.length == 3)
                    writeInfo(args[1] + "[" + args[2] + "]=" + selectedConfig.getValue(args[1], Integer.valueOf(args[2])));
                else
                    writeInfo("Invalid use of '!config getvalue' command, use '!config' or '!config help' to get help for this command.");
            } catch (NumberFormatException e) { writeError(e); }
        }
    }

    private void setvalue() {
        if (args.length < 3)
            writeInfo("Invalid use of '!config setvalue' command, use '!config' or '!config help' to get help for this command.");
        else {
            try {
                if (args.length == 3) {
                    if (args[2].equals("null")) {
                        selectedConfig.removeElement(args[1]);
                        writeInfo("Value of " + args[1] + "[0] removed");
                    } else {
                        selectedConfig.setValue(args[1], args[2]);
                        writeInfo("Value of " + args[1] + "[0] set to " + args[2]);
                    }
                } else if (args.length == 4) {
                    if (args[2].equals("null")) {
                        selectedConfig.removeElement(args[1], Integer.valueOf(args[3]));
                        writeInfo("Value of " + args[1] + "[" + args[3] + "] removed");
                    } else {
                        selectedConfig.setValue(args[1], args[2], Integer.valueOf(args[3]));
                        writeInfo("Value of " + args[1] + "[" + args[3] + "] set to " + args[2]);
                    }
                } else
                    writeInfo("Invalid use of '!config setvalue' command, use '!config' or '!config help' to get help for this command.");
            } catch (NumberFormatException e) { writeError(e); }
        }
    }

    private void getchildvalue() {
        if (args.length < 3)
            writeInfo("Invalid use of '!config getchildvalue' command, use '!config' or '!config help' to get help for this command.");
        else {
            try {
                if (args.length == 3)
                    writeInfo(args[1] + "[0]->" + args[2] + "[0]=" + selectedConfig.getChildValue(args[1], args[2]));
                else if (args.length == 5)
                    writeInfo(args[1] + "[" + args[3] + "]->" + args[2] + "[" + args[4] + "]=" + selectedConfig.getChildValue(args[1], args[2], Integer.valueOf(args[3]), Integer.valueOf(args[4])));
                else
                    writeInfo("Invalid use of '!config getchildvalue' command, use '!config' or '!config help' to get help for this command.");
            } catch (NumberFormatException e) { writeError(e); }
        }
    }

    private void setchildvalue() {
        if (args.length < 4)
            writeInfo("Invalid use of '!config setchildvalue' command, use '!config' or '!config help' to get help for this command.");
        else {
            try {
                if (args.length == 4) {
                    if (args[3].equals("null")) {
                        selectedConfig.removeChildElement(args[1], args[2]);
                        writeInfo("Value of " + args[1] + "[0]->" + args[2] + "[0] removed");
                    } else {
                        selectedConfig.setChildValue(args[1], args[2], args[3]);
                        writeInfo("Value of " + args[1] + "[0]->" + args[2] + "[0] set to " + args[3]);
                    }
                } else if (args.length == 6) {
                    if (args[3].equals("null")) {
                        selectedConfig.removeChildElement(args[1], args[2], Integer.valueOf(args[4]), Integer.valueOf(args[5]));
                        writeInfo("Value of " + args[1] + "[" + args[4] + "]->" + args[2] + "[" + args[5] + "] removed");
                    } else {
                        selectedConfig.setChildValue(args[1], args[2], args[3], Integer.valueOf(args[4]), Integer.valueOf(args[5]));
                        writeInfo("Value of " + args[1] + "[" + args[4] + "]->" + args[2] + "[" + args[5] + "] set to " + args[3]);
                    }
                } else
                    writeInfo("Invalid use of '!config setchildvalue' command, use '!config' or '!config help' to get help for this command.");
            } catch (NumberFormatException e) { writeError(e); }
        }
    }
}
