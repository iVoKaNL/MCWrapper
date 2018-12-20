package nl.ivoka;

import nl.ivoka.API.Console;
import nl.ivoka.EventArgs.PlayerEvents.PlayerChatEventArgs;
import nl.ivoka.EventArgs.PlayerEvents.PlayerJoinedEventArgs;
import nl.ivoka.EventArgs.PlayerEvents.PlayerLeftEventArgs;
import nl.ivoka.EventArgs.PlayerEvents.PlayerPositionEventArgs;
import nl.ivoka.EventArgs.ServerEvents.ServerStatusEventArgs;
import nl.ivoka.Plugins.IMCWrapperPlugin;
import org.dom4j.DocumentException;

import java.io.IOException;

public class JavascriptConnector implements IMCWrapperPlugin {
    public String Name="JavascriptConnector";
    private JavascriptPluginManager manager;

    public JavascriptConnector(MinecraftConnector connector) throws DocumentException, IOException {
        manager = new JavascriptPluginManager();

        if (connector.events == null) {
            Console.instance.writeLine(Name+": error while loading plugin. Events = null");
            return;
        } else {
            connector.events.addListener((x) -> {
                try {
                    if (x instanceof PlayerChatEventArgs)
                        PlayerChat((PlayerChatEventArgs) x);
                    else if (x instanceof PlayerJoinedEventArgs)
                        PlayerJoin((PlayerJoinedEventArgs) x);
                    else if (x instanceof PlayerLeftEventArgs)
                        PlayerLeft((PlayerLeftEventArgs) x);
                    else if (x instanceof PlayerPositionEventArgs)
                        PlayerPosition((PlayerPositionEventArgs) x);
                    else if (x instanceof ServerStatusEventArgs) {
                        ServerStatusEventArgs e = (ServerStatusEventArgs) x;
                        if (e.event == ServerStatusEventArgs.Event.START)
                            ServerStart(e);
                        else if (e.event == ServerStatusEventArgs.Event.STOP)
                            ServerStop(e);
                    } else
                        return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void PlayerJoin(PlayerJoinedEventArgs e) throws IOException { TriggerEvent("PlayerJoin", e.name); }
    private void PlayerLeft(PlayerLeftEventArgs e) throws IOException { TriggerEvent("PlayerLeave", e.name); }
    private void PlayerPosition(PlayerPositionEventArgs e) throws IOException { TriggerEvent("PlayerPosition", e.name, e.position); }
    private void PlayerChat(PlayerChatEventArgs e) throws IOException { TriggerEvent("ChatReceived", e.name, e.chat); }

    private void ServerStart(ServerStatusEventArgs e) throws IOException { TriggerEvent("ServerStart"); }
    private void ServerStop(ServerStatusEventArgs e) throws IOException { TriggerEvent("ServerStop"); }

    private void TriggerEvent(String name, Object... args) throws IOException {
        for (JavascriptPlugin plugin : manager.plugins) {

            String _tmp = "if(typeof "+
                    name+
                    " != 'undefined') "+
                    name+
                    "(";

            for (int i = 0; i < args.length; i++) {
                plugin.runtime.put("arg" + i, args[i]);

                _tmp += "arg"+
                        i;
                if (i != args.length-1)
                    _tmp += ", ";
            }
            _tmp += ");";

            plugin.run(_tmp);
        }
    }
}
