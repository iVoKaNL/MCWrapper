package nl.ivoka.Plugins;

import nl.ivoka.MinecraftConnector;
import nl.ivoka.ServerManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class PluginManager {

    public List<IMCWrapperPlugin> plugins;
    URLClassHacker urlClassHacker;
    ServerManager serverManager;

    public PluginManager(File dir, ServerManager serverManager) {
        plugins = new ArrayList<>();
        urlClassHacker = new URLClassHacker();
        this.serverManager = serverManager;

        File[] pluginFiles = dir.listFiles((directory, name) -> name.endsWith(".jar"));

        if (pluginFiles.length > 0) {
            System.out.println("Loading plugins!");
            for (File file : pluginFiles) {
                System.out.println("Loading Plugin: "+file);
                loadPlugin(file);
            }
            System.out.println("Finished Loading Plugins!");
        } else
            System.out.println("No Plugins found!");

        System.out.println(plugins.size()+" Plugins loaded");
    }

    public void unLoadPlugin(String name) {
        plugins.forEach((x) -> {
            if (name == x.Name)
                plugins.remove(x);
        });
    }

    public void loadPlugin(File file) {
        try {
            urlClassHacker.addFile(file);

            Class<IMCWrapperPlugin> c1 = (Class<IMCWrapperPlugin>)ClassLoader.getSystemClassLoader().loadClass("nl.ivoka.JavascriptConnector");
            Constructor<IMCWrapperPlugin> constructor = c1.getConstructor(MinecraftConnector.class);
            IMCWrapperPlugin instance = constructor.newInstance(serverManager.connector);

            plugins.add(instance);
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
