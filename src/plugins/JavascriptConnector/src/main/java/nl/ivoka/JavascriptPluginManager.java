package nl.ivoka;

import nl.ivoka.API.Console;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavascriptPluginManager {
    private Map<String, JavascriptPlugin> plugins;

    public JavascriptPluginManager() { }

    public void reloadPlugins() {
        stopPlugins();
        startPlugins();
    }

    public void startPlugins() {
        File jsPluginDir = new File(Main.getPluginsDir()+"/javascript");
        plugins = new HashMap<>();

        Main.debug("Checking if directories exist, if not create them...");
        if (!jsPluginDir.exists())
            jsPluginDir.mkdirs();

        File[] pluginFiles = jsPluginDir.listFiles(name -> name.getPath().toLowerCase().endsWith(".js"));

        if (pluginFiles==null)
            writeInfo("There were no '.js' files found inside the javascript folder.");
        else if (pluginFiles.length>0) {
            writeInfo("Loading Javascript plugins!");

            for (File file : pluginFiles) {
                if (!plugins.containsKey(file.getName())) {
                    writeInfo("Loading plugin: " + file.getPath());
                    JavascriptPlugin _plugin = new JavascriptPlugin(file.getName(), file);
                    plugins.put(file.getName(), _plugin);
                }
            }

            writeInfo("Finished loading Javascript plugins.");
        } else
            writeInfo("There were no '.js' files found inside the javascript folder.");
        writeInfo(plugins.size()+" Javascript plugins loaded.");
    }
    public boolean startPlugin(String pluginName) {
        if (plugins.containsKey(pluginName))
            return false;

        File jsPluginDir = new File(Main.getPluginsDir()+"/javascript");
        File[] pluginFiles = jsPluginDir.listFiles(name -> name.getName().equals(pluginName));

        if (pluginFiles!=null && pluginFiles.length==1) {
            writeInfo("Loading plugin: " + pluginFiles[0].getPath());
            JavascriptPlugin _plugin = new JavascriptPlugin(pluginFiles[0].getName(), pluginFiles[0]);
            plugins.put(pluginFiles[0].getName(), _plugin);
            writeInfo("Loaded plugin: "+pluginFiles[0].getName());
            return true;
        } else
            return false;
    }

    public void stopPlugins() {
        writeInfo("Stopping all Javascript plugins...");
        for (JavascriptPlugin plugin : plugins.values()) {
            writeInfo("Stopping: " + plugin.getName());
            plugin.stop();
            writeInfo("Stopped: " + plugin.getName());
        }
        plugins.clear();
        writeInfo("Stopped all Javascript plugins...");
    }
    public boolean stopPlugin(String name) {
        if (plugins.containsKey(name)) {
            writeInfo("Stopping: "+name);
            plugins.get(name).stop();
            plugins.remove(name);
            writeInfo("Stopped: "+name);
            return true;
        } else
            return false;
    }

    public int getPluginCount() { return plugins.size(); }
    public List<String> getPluginNames() {
        List<String> names = new ArrayList<>();
        for (JavascriptPlugin plugin : plugins.values())
            names.add(plugin.getName());
        return names;
    }
    public Map<String, JavascriptPlugin> getPlugins() { return plugins; }
    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.JAVASCRIPTCONNECTOR, Console.PREFIX.INFO); }
}
