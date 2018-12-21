package nl.ivoka.Plugins;

import nl.ivoka.API.Console;
import nl.ivoka.MinecraftConnector;
import nl.ivoka.ServerManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;

public class PluginManager {

    public enum PluginState {
        RUNNING,
        STOPPED
    }

    public Map<IMCWrapperPlugin, PluginState> plugins;
    URLClassHacker urlClassHacker;
    ServerManager serverManager;
    File dir;

    public PluginManager(File dir, ServerManager serverManager) {
        this.serverManager = serverManager;
        this.dir = dir;
        plugins = new LinkedHashMap<>();
        urlClassHacker = new URLClassHacker();

        File[] pluginFiles = dir.listFiles((directory, name) -> name.endsWith(".jar"));

        if (pluginFiles.length > 0) {
            Console.instance.writeLine("Loading plugins!");
            for (File file : pluginFiles) {
                Console.instance.writeLine("Loading Plugin: "+file);
                loadPlugin(file);
            }
            Console.instance.writeLine("Finished Loading Plugins!");
        } else
            Console.instance.writeLine("No Plugins found!");

        Console.instance.writeLine(plugins.size()+" Plugins loaded");
    }

    public void startPlugin(String name) {
        plugins.forEach((IMCWrapperPlugin x, PluginState y) -> {
            if (name.equals(x.Name())) {
                if (y.equals(PluginState.STOPPED)) {
                    Console.instance.writeLine("Starting: "+name);
                    x.start();
                    plugins.replace(x, PluginState.RUNNING);
                    Console.instance.writeLine("Started: "+name);
                } else
                    Console.instance.writeLine("Couldn't start "+x.Name()+" because this plugin is already running");
            }
        });
    }
    public void startPlugins() {
        plugins.forEach((IMCWrapperPlugin x, PluginState y) -> {
            if (y.equals(PluginState.STOPPED)) {
                Console.instance.writeLine("Starting: "+x.Name());
                x.start();
                plugins.replace(x, PluginState.RUNNING);
                Console.instance.writeLine("Started: "+x.Name());
            }
            Console.instance.writeLine("Started all plugins that were not running.");
        });
    }

    public void reloadPlugin(String name) {
        plugins.forEach((IMCWrapperPlugin x, PluginState y) -> {
            if (name.equals(x.Name()) && y.equals(PluginState.RUNNING)) {
                Console.instance.writeLine("Reloading: "+name);
                x.reload();
                Console.instance.writeLine("Reloaded: "+name);
            }
        });
    }
    public void reloadPlugins() {
        Console.instance.writeLine("Reloading "+plugins.size()+" plugin(s)");
        plugins.forEach((IMCWrapperPlugin x, PluginState y) -> {
            if (y.equals(PluginState.RUNNING)) {
                Console.instance.writeLine("Reloading: " + x.Name());
                x.reload();
                Console.instance.writeLine("Reloaded: " + x.Name());
            }
        });
        Console.instance.writeLine("Successfully reloaded "+plugins.size()+" plugins");
    }

    public void stopPlugin(String name) {
        plugins.forEach((IMCWrapperPlugin x, PluginState y) -> {
            if (name.equals(x.Name()) && y.equals(PluginState.RUNNING)) {
                Console.instance.writeLine("Stopping: "+name);
                x.stop();
                plugins.replace(x, PluginState.STOPPED);
                Console.instance.writeLine("Stopped: "+name);
            }
        });
    }
    public void stopPlugins() {
        Console.instance.writeLine("Stopping "+plugins.size()+" plugin(s)");
        plugins.forEach((IMCWrapperPlugin x, PluginState y) -> {
            if (y.equals(PluginState.RUNNING)) {
                Console.instance.writeLine("Stopping: " + x.Name());
                x.stop();
                plugins.replace(x, PluginState.STOPPED);
                Console.instance.writeLine("Stopped: " + x.Name());
            }
        });
        Console.instance.writeLine("Successfully stopped "+plugins.size()+" plugins");
    }

    public void unloadPlugin(String name) {
        plugins.forEach((IMCWrapperPlugin x, PluginState y) -> {
            if (name.equals(x.Name()) && y.equals(PluginState.RUNNING)) {
                Console.instance.writeLine("Stopping: "+name);
                x.stop();
                plugins.replace(x, PluginState.STOPPED);
                Console.instance.writeLine("Stopped: "+name);

                plugins.remove(x);
            } else
                plugins.remove(x);
        });
    }

    public void loadPlugin(File file) {
        try {
            urlClassHacker.addFile(file);

            Class<IMCWrapperPlugin> c1 = (Class<IMCWrapperPlugin>)ClassLoader.getSystemClassLoader().loadClass("nl.ivoka."+file.getName().split("\\.")[0]);
            Constructor<IMCWrapperPlugin> constructor = c1.getConstructor(MinecraftConnector.class);
            IMCWrapperPlugin instance = constructor.newInstance(serverManager.connector);

            plugins.put(instance, PluginState.RUNNING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class URLClassHacker {
    private final Class<?>[] parameters = new Class[]{URL.class};

    public void addFile(String s) throws IOException {
        File file = new File(s);
        addFile(file);
    }

    public void addFile(File f) throws IOException {
        addURL(f.toURI().toURL());
    }

    public void addURL(URL u) {
        URLClassLoader sysLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class<?> sysClass = URLClassLoader.class;
        try {
            Method method = sysClass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[]{ u });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
