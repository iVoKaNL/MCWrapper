package nl.ivoka;

import nl.ivoka.API.Config;
import nl.ivoka.API.Console;
import nl.ivoka.API.Player;
import nl.ivoka.API.Server;
import org.dom4j.DocumentException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class JavascriptPlugin {
    private String name;
    private ScriptEngine runtime;
    private Config config;

    JavascriptPlugin(String name, File source) {
        try {
            this.name = name;
            this.config = new Config(Main.getConfigsDir() + "/" + name + ".xml");

            ScriptEngineManager factory = new ScriptEngineManager();
            runtime = factory.getEngineByName("JavaScript");

            runtime.put("Console", Console.instance());
            runtime.put("Player", Player.instance());
            runtime.put("Server", Server.instance());
            runtime.put("Config", config);

            run(source);
        } catch (DocumentException | IOException e) {
            writeError("Error in Javascript plugin: "+name);
            writeError(e);
        }
    }

    private void run(File source) {
        try {
            runtime.eval(new FileReader(source.getAbsolutePath()));
        } catch (FileNotFoundException | ScriptException e) {
            writeError("Error in Javascript plugin: "+name);
            writeError(e);
        }
    }
    public void run(String source) {
        try {
            runtime.eval(source);
        } catch (ScriptException e) {
            writeError("Error in Javascript plugin: "+name);
            writeError(e);
        }
    }

    public void runtimePut(String key, Object value) { runtime.put(key, value); }

    public void stop() {
        run("if(typeof Stop != 'undefined') Stop();");

        config = null;
        runtime = null;
    }

    private void writeError(String msg) { Console.instance().writeLine(msg, Console.PREFIX.JAVASCRIPTCONNECTOR, Console.PREFIX.ERROR); }
    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.JAVASCRIPTCONNECTOR, Console.PREFIX.ERROR); }
}

// https://metoojava.wordpress.com/2010/06/20/execute-javascript-from-java/
// https://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/index.html