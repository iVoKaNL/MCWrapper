package nl.ivoka;

import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class JavascriptPluginManager {
    JavascriptPlugin[] plugins;

    public JavascriptPluginManager() throws DocumentException, IOException, NullPointerException {
        String dir = Main.pluginsDir + "/javascript";

        plugins = new JavascriptPlugin[0];

        File[] pluginFiles = (new File(dir)).listFiles((directory, name) -> name.endsWith(".js"));

        if (pluginFiles == null) {

        } else if (pluginFiles.length > 0) {
            List<JavascriptPlugin> _plugins = new ArrayList<>();

            System.out.println("Loading Javascript Plugins!");
            for (File file : pluginFiles) {
                System.out.println("Loading Plugin: "+file);
                String name = file.getName();
                JavascriptPlugin plugin = new JavascriptPlugin(name, file);
                _plugins.add(plugin);
            }
            plugins = _plugins.toArray(new JavascriptPlugin[0]);
            System.out.println("Finished Loading Javascript Plugins!");
        } else
            System.out.println("No Javascript Plugins found!");
        System.out.println(plugins.length+" Javascript Plugins loaded");
    }
}
