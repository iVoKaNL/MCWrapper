package nl.ivoka;

import nl.ivoka.API.Console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class JavascriptPluginManager {
    private List<JavascriptPlugin> plugins;

    JavascriptPluginManager() { }

    public void reloadPlugins() {
        stopPlugins();
        loadPlugins();
    }

    public void loadPlugins() {
        File jsPluginDir = new File(Main.getPluginsDir()+"/javascript");
        plugins = new ArrayList<>();

        Main.debug("Checking if directories exist, if not create them...");
        if (!jsPluginDir.exists())
            jsPluginDir.mkdirs();

        File[] pluginFiles = jsPluginDir.listFiles(name -> name.getPath().toLowerCase().endsWith(".js"));

        if (pluginFiles==null)
            writeInfo("There were no '.js' files found inside the javascript folder.");
        else if (pluginFiles.length>0) {
            writeInfo("Loading Javascript plugins!");

            for (File file : pluginFiles) {
                writeInfo("Loading plugin: "+file.getPath());
                JavascriptPlugin _plugin = new JavascriptPlugin(file.getName(), file);
                plugins.add(_plugin);
            }

            writeInfo("Finished loading Javascript plugins.");
        } else
            writeInfo("There were no '.js' files found inside the javascript folder.");
        writeInfo(plugins.size()+" Javascript plugins loaded.");
    }

    public void stopPlugins() {
        for (JavascriptPlugin plugin : plugins)
            plugin.stop();
        plugins.clear();
    }

    public int getPluginCount() { return plugins.size(); }
    public List<JavascriptPlugin> getPlugins() { return plugins; }
    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.JAVASCRIPTCONNECTOR, Console.PREFIX.INFO); }
}
