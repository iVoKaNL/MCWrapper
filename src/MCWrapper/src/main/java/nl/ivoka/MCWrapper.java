package nl.ivoka;

import nl.ivoka.API.Config;

import java.io.File;

public class MCWrapper {
    private static String name = "MCWrapper";

    private static Config config;

    private static File mcWrapperDir = new File("MCWrapper");
    private static File pluginsDir = new File(mcWrapperDir+"/plugins");
    private static File configsDir = new File(mcWrapperDir+"/configs");

    private static File mcWrapperConfig = new File(mcWrapperDir+"/MCWrapper.xml");



    // region getters
    public static String getName() { return name; }
    public static Config getConfig() { return config; }

    public static File getMCWrapperDir() { return  mcWrapperDir; }
    public static File getConfigsDir() { return configsDir; }
    public static File getPluginsDir() { return pluginsDir; }

    public static File getMCWrapperConfig() { return mcWrapperConfig; }
    // endregion
}
