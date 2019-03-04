package nl.ivoka.Plugin;

import nl.ivoka.API.Console;
import nl.ivoka.Main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class PluginManager {
    public enum PLUGINSTATE {
        STARTING,
        RUNNING,
        RELOADING,
        STOPPING,
        STOPPED
    }

    private Map<IMCWrapperPlugin, PLUGINSTATE> plugins;
    private Map<IMCWrapperPlugin, String> pluginStringMap;
    private Map<String, IMCWrapperPlugin> stringPluginMap;

    private List<URL> urls;
    private List<List<URL>> oldURLLists;

    private URLClassLoader urlClassLoader;
    private ServiceLoader<IMCWrapperPlugin> serviceLoader;
    private Iterator<IMCWrapperPlugin> imcWrapperPluginIterator;

    private File pluginDir;

    // TODO add reloadPluginFolder, public addJar, addUrls, addPlugins
    public PluginManager(File dir) {
        pluginDir = new File(dir.getPath()+"/plugins");

        debug("Checking if plugins directory exists, if not create it...");
        if (!pluginDir.exists())
            pluginDir.mkdirs();

        plugins = new LinkedHashMap<>();
        pluginStringMap = new LinkedHashMap<>();
        stringPluginMap = new LinkedHashMap<>();

        urls = new ArrayList<>();
        oldURLLists = new ArrayList<>();

        File[] pluginFiles = pluginDir.listFiles(x -> x.getPath().toLowerCase().endsWith(".jar"));

        if (pluginFiles == null)
            pluginFiles=new File[0];

        if (pluginFiles.length>0) {
            writeInfo("Loading plugins!");
            for (File file : pluginFiles) {
                writeInfo("Adding jar: "+file.getPath());
                addJar(file);
            }

            writeInfo("Adding urls...");
            addUrls();

            writeInfo("Adding plugins...");
            addPlugins();

            writeInfo("Starting plugins...");
            startPlugins();

            writeInfo("Finished loading plugins.");
        } else
            writeInfo("No plugins found!");

        writeInfo(plugins.size()+" plugins loaded.");
    }

    private void addJar(File file) { try { urls.add(file.toURI().toURL()); } catch (MalformedURLException e) { writeError(e); } }
    private void addUrls() { urlClassLoader = new URLClassLoader(urls.toArray(new URL[0])); oldURLLists.add(urls); }
    private void addPlugins() {
        serviceLoader = ServiceLoader.load(IMCWrapperPlugin.class, urlClassLoader);
        imcWrapperPluginIterator = serviceLoader.iterator();

        while(imcWrapperPluginIterator.hasNext()) {
            IMCWrapperPlugin plugin = imcWrapperPluginIterator.next();
            plugin.initialize(this);

            plugins.put(plugin, PLUGINSTATE.STOPPED);
            pluginStringMap.put(plugin, plugin.getName());
            stringPluginMap.put(plugin.getName(), plugin);
        }
    }

    public void startPlugins() {
        plugins.forEach((plugin, state) -> {
            if (state.equals(PLUGINSTATE.STOPPED))
                startPlugin(plugin);
        });
        writeInfo("Started all plugins.");
    }
    public void startPlugin(String name) {
        if (stringPluginMap.containsKey(name))
            startPlugin(stringPluginMap.get(name));
        else
            writeInfo("That plugin is not in the plugin list.");
    }
    public void startPlugin(IMCWrapperPlugin plugin) {
        writeInfo("Starting '"+pluginStringMap.get(plugin)+"'...");
        if (!plugins.replace(plugin, PLUGINSTATE.STOPPED, PLUGINSTATE.STARTING))
            writeError(new Exception("An error occurred while starting plugin..."));
        plugin.start();
        writeInfo("Started '"+pluginStringMap.get(plugin)+"'.");

        /*
        System.out.println();
        System.out.println();

        System.out.println("plugins: "+plugins.toString());
        System.out.println("stringP: "+stringPluginMap.toString());
        System.out.println("pluginS: "+pluginStringMap.toString());

        System.out.println();
        System.out.println();
        */
    }

    public void stopPlugins() {
        plugins.forEach((plugin, state) -> {
            if (state.equals(PLUGINSTATE.RUNNING))
                stopPlugin(plugin);
        });
        writeInfo("Stopped all plugins.");
    }
    public void stopPlugin(String name) {
        if (stringPluginMap.containsKey(name))
            stopPlugin(stringPluginMap.get(name));
        else
            writeInfo("That plugin is not in the plugin list.");
    }
    public void stopPlugin(IMCWrapperPlugin plugin) {
        writeInfo("Stopping '"+pluginStringMap.get(plugin)+"'...");
        if (!plugins.replace(plugin, PLUGINSTATE.RUNNING, PLUGINSTATE.STOPPING))
            writeError(new Exception("An error occurred while stopping plugin..."));
        plugin.stop();
        writeInfo("Stopped '"+pluginStringMap.get(plugin)+"'.");
    }

    public void reloadPlugins() {
        plugins.forEach((plugin, state) -> {
            if (state.equals(PLUGINSTATE.RUNNING))
                reloadPlugin(plugin);
        });
        writeInfo("Reloaded all running plugins.");
    }
    public void reloadPlugin(String name) {
        if (stringPluginMap.containsKey(name))
            reloadPlugin(stringPluginMap.get(name));
        else
            writeInfo("That plugin is not in the plugin list.");
    }
    public void reloadPlugin(IMCWrapperPlugin plugin) {
        writeInfo("Reloading '"+pluginStringMap.get(plugin)+"'...");
        if (!plugins.replace(plugin, PLUGINSTATE.RUNNING, PLUGINSTATE.RELOADING))
            writeError(new Exception("An error occurred while reloading plugin..."));
        plugin.reload();
        if (!plugins.get(plugin).equals(PLUGINSTATE.RUNNING))
            writeError(new Exception("An error occurred while reloading plugin, plugin was not started..."));
        else
            writeInfo("Reloaded '"+pluginStringMap.get(plugin)+"'.");
    }

    public void changePluginState(String name, PLUGINSTATE newState) { changePluginState(stringPluginMap.get(name), newState); }
    public boolean changePluginState(String name, PLUGINSTATE oldState, PLUGINSTATE newState) { return changePluginState(stringPluginMap.get(name), oldState, newState); }
    public void changePluginState(IMCWrapperPlugin plugin, PLUGINSTATE newState) { plugins.replace(plugin, newState); }
    public boolean changePluginState(IMCWrapperPlugin plugin, PLUGINSTATE oldSate, PLUGINSTATE newState) { return plugins.replace(plugin, oldSate, newState); }
    public PLUGINSTATE getPluginState(String name) { return getPluginState(stringPluginMap.get(name)); }
    public PLUGINSTATE getPluginState(IMCWrapperPlugin plugin) { return plugins.get(plugin); }

    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.PLUGINMANAGER, Console.PREFIX.INFO); }
    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.PLUGINMANAGER, Console.PREFIX.ERROR); }

    public int getPluginSize() { return plugins.size(); }
    public int getRunningPluginSize() { int i=0; for (PLUGINSTATE state : plugins.values()) if (state.equals(PLUGINSTATE.RUNNING)) i++; return i; }
    public int getPluginSize(PLUGINSTATE pluginstate) { int i=0; for (PLUGINSTATE state : plugins.values()) if (state.equals(pluginstate)) i++; return i; }

    public List<String> getPluginStateList() {
        List<String> pluginStateList = new ArrayList<>();

        for (Map.Entry<IMCWrapperPlugin, PLUGINSTATE> entry : plugins.entrySet())
            pluginStateList.add(pluginStringMap.get(entry.getKey())+"("+entry.getValue()+")");

        return pluginStateList;
    }

    public static void debug(String msg) { debug(msg, false); }
    public static void debug(String msg, boolean override) {
        if (Main.getDebug()||override)
            Console.instance().writeLine(msg, Console.PREFIX.PLUGINMANAGER, Console.PREFIX.DEBUG);
    }
}
