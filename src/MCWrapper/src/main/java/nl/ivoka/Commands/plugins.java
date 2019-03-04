package nl.ivoka.Commands;

import nl.ivoka.API.Commands.Command;
import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.Main;

@Command
public class plugins implements CustomCommand {
    private String[] args;

    public plugins(CommandEvent e) {
        if (e!=null) {
            this.args = e.args;

            if (args.length == 0) {
                help();
            } else {
                if (e.command.equals("help")) help();
                else if (args[0].equals("list")) list();
                else if (args[0].equals("reload")) reload();
                else if (args[0].equals("stop")) stop();
                else if (args[0].equals("start")) start();
                else help();
            }
        }
    }

    public void help() {
        writeInfo("Usage 'help':");
        writeInfo("  !plugins help - Show this help menu");
        writeInfo("  !plugins list - Show a list of all plugins");
        writeInfo("  !plugins reload [plugin] - Reloads all plugins, when [plugin] argument is given reload specific plugin");
        writeInfo("  !plugins stop [plugin] - Stops all plugins, when [plugin] argument is given stop specific plugin");
        writeInfo("  !plugins start [plugin] - Start all plugins, when [plugin] argument is given start specific plugin");
    }

    private void list() {
        String message = "Plugins("+ Main.getPluginManager().getPluginSize()+"): ";

        for (String pluginName : Main.getPluginManager().getPluginStateList())
            message+=pluginName+", ";

        message = message.substring(0, message.length()-2);

        writeInfo(message);
    }

    private void reload() {
        if (args.length == 1)
            Main.getPluginManager().reloadPlugins();
        else
            Main.getPluginManager().reloadPlugin(args[1]);
    }

    private void stop() {
        if (args.length == 1)
            Main.getPluginManager().stopPlugins();
        else
            Main.getPluginManager().stopPlugin(args[1]);
    }

    private void start() {
        if (args.length == 1)
            Main.getPluginManager().startPlugins();
        else
            Main.getPluginManager().startPlugin(args[1]);
    }
}
