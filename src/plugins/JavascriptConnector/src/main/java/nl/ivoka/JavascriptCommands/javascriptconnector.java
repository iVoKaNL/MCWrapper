package nl.ivoka.JavascriptCommands;

import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.API.Console;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.JavascriptConnector;

public class javascriptconnector implements CustomCommand {
    private JavascriptConnector javascriptConnector;
    private String[] args;

    public javascriptconnector(CommandEvent e, JavascriptConnector javascriptConnector) {
        if (e!=null&&javascriptConnector!=null) {
            this.javascriptConnector = javascriptConnector;
            this.args = e.args;

            if (args.length == 0) {
                help();
            } else {
                if (args[0].equals("list")) list();
                else if (args[0].equals("stop")) stop();
                else if (args[0].equals("start")) start();
                else if (args[0].equals("reload")) reload();
                else help();
            }
        }
    }

    public void help() {
        writeInfo("Usage 'javascriptconnector' alias 'jsc':");
        writeInfo("  !jsc help - Show this help menu");
        writeInfo("  !jsc list - Shows a list of all javascript plugins");
        writeInfo("  !jsc stop [javascriptplugin1, javascriptplugin2, ...] - Remove all javascript plugins, or all specified javascript plugins");
        writeInfo("  !jsc start [javascriptplugin1, javascriptplugin2, ...] - Add all javascript plugins, or all specified javascript plugins");
        writeInfo("  !jsc reload [javascriptplugin1, javascriptplugin2, ...] - Reload all javascript plugins, or all specified javascript plugins");
    }

    private void list() {
        StringBuilder message = new StringBuilder("Running javascript plugins("+javascriptConnector.getJavascriptPluginManager().getPluginCount()+"): ");

        for (String name : javascriptConnector.getJavascriptPluginManager().getPluginNames()) {
            message.append(name);
            message.append(", ");
        }

        Console.instance().writeLine(message.substring(0, message.length()-2), Console.PREFIX.INFO);

    }

    private void stop() {
        if (args.length == 1)
            javascriptConnector.getJavascriptPluginManager().stopPlugins();
        else {
            for (int i = 1; i < args.length; i++) {
                String plugin = args[i];
                if (!plugin.toLowerCase().endsWith(".js"))
                    plugin+=".js";

                if (!javascriptConnector.getJavascriptPluginManager().stopPlugin(plugin))
                    writeInfo("No plugin found with name '" + plugin + "'");
            }
        }
    }

    private void start() {
        if (args.length == 1)
            javascriptConnector.getJavascriptPluginManager().startPlugins();
        else {
            for (int i=1; i<args.length; i++) {
                String plugin = args[i];
                if (!plugin.toLowerCase().endsWith(".js"))
                    plugin+=".js";

                if (!javascriptConnector.getJavascriptPluginManager().startPlugin(plugin))
                    writeInfo("An error occurred while trying to start plugin with name '" + plugin + "'. Plugin is already running or does not exists.");
            }
        }
    }

    private void reload() {
        if (args.length==1)
            writeInfo("Reloading all javascript plugins...");
        else
            writeInfo("Reloading "+(args.length-1)+" javascript plugins...");

        stop();
        start();

        writeInfo("Reloaded javascript plugins.");
    }
}