package nl.ivoka;

import nl.ivoka.API.Console;
import nl.ivoka.API.Player;
import nl.ivoka.API.Server;
import nl.ivoka.API.Config;
import org.dom4j.DocumentException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class JavascriptPlugin {
    String name;
    ScriptEngine runtime;
    Config config;

    public JavascriptPlugin(String name, File source) throws DocumentException, IOException {
        this.name = name;
        this.config = new Config(Main.configsDir + "/" + name + ".xml");

        ScriptEngineManager factory = new ScriptEngineManager();
        runtime = factory.getEngineByName("JavaScript");

        runtime.put("Console", Console.instance);
        runtime.put("Player", Player.instance);
        runtime.put("Server", Server.instance);
        runtime.put("Config", config);

        run(source);
    }

    public void run(String source) {
        try {
            runtime.eval(source);
        } catch (ScriptException e) {
            Console.instance.writeLine("Error in Javascript Plugin "+name);
            Console.instance.writeLine(" \\ Error: "+e.getMessage());
        }
    }

    public void run(File source) {
        try {
            runtime.eval(new FileReader(source.getAbsolutePath()));
        } catch (FileNotFoundException e) {
            Console.instance.writeLine("Error in Javascript Plugin "+name);
            Console.instance.writeLine(" \\ Error: "+e.getMessage());
        } catch (ScriptException e) {
            Console.instance.writeLine("Error in Javascript Plugin "+name);
            Console.instance.writeLine(" \\ Error: "+e.getMessage());
        }
    }

    public void stop() {
        config = null;
        runtime = null;
    }
}

// https://metoojava.wordpress.com/2010/06/20/execute-javascript-from-java/
// https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/index.html