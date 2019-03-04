package nl.ivoka.Server;

import nl.ivoka.API.Console;
import nl.ivoka.API.Logger;
import nl.ivoka.Events.Handlers.EventHandler;
import nl.ivoka.Events.ServerEvents.ServerOutputEvent;
import nl.ivoka.Main;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerManager {
    private MinecraftConnector connector;

    private Process mc;
    private ProcessBuilder processBuilder;

    private BufferedReader reader;
    private BufferedWriter writer;

    private boolean calledStop = false;
    private Thread outputThread;

    public ServerManager(String jarFile) { this(jarFile, null); }
    public ServerManager(String jarFile, File directory) {
        List<String> startingArguments = new ArrayList<>();
        debug("Adding server start arguments.");
        startingArguments.add(Main.getConfig().getValue("JavaExecutable"));

        for (String argument : Main.getConfig().getValues("JavaArguments"))
            Collections.addAll(startingArguments, argument.split("\\s+"));
        startingArguments.add("-jar");
        startingArguments.add(jarFile);
        Collections.addAll(startingArguments, Main.getConfig().getValue("ServerJarArguments").split("\\s+"));

        debug("Creating processbuilder and checking if custom directory is used.");
        processBuilder = new ProcessBuilder(startingArguments);
        if (directory != null) {
            debug("Setting custom directory.");
            processBuilder.directory(directory);
        } else
            debug("Using default directory.");
        processBuilder.redirectErrorStream(true);

        writeInfo("Server starting with: "+processBuilder.command());
    }

    public void start() {
        try {
            writeInfo("Starting java...");

            mc = processBuilder.start();
            reader = new BufferedReader(new InputStreamReader(mc.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(mc.getOutputStream()));

            debug("Redirected input- and output streams.");

            outputThread = new Thread(this::outputThread);
            outputThread.start();

            debug("Started output thread.");

            connector = new MinecraftConnector();
            debug("Initialized minecraft connector.");
        } catch (IOException e) {
            writeError(e);
        }
    }

    public void stop() {
        try {
            debug("Stopping server...");
            calledStop = true;

            EventHandler.instance().removeListener(Main.getName()+"-serverStopListener");

            if (mc.isAlive())
                writeLine("stop");

            System.setIn(new ByteArrayInputStream("\n".getBytes()));

            mc.waitFor();

            Console.instance().writeLine("Server stopped. Press enter to exit! (if server was stopped without stop command (command line) then press enter twice, bug is being looked at)", Console.PREFIX.INFO);
            Logger.instance().closeLog();
            Main.readLine();
            System.exit(0);
        } catch (InterruptedException e) {
            writeError(e);
        }
    }

    public void writeLine(String message) {
        try {
            if (mc.isAlive()) {
                writer.write(message);
                writer.newLine();
                writer.flush();

                Logger.instance().writeLog("> "+message+"\n");
            }
        } catch (IOException e) {
            writeError(e);
        }
    }

    private void outputThread() {
        try {
            if (Logger.instance().isEnableServerOutputLogging()) {
                for (String line=reader.readLine(); line!=null && mc.isAlive(); line=reader.readLine()) {
                    connector.onDataReceived(new ServerOutputEvent(line));

                    System.out.println(line);
                    Logger.instance().writeLog(line+"\n");
                }
            } else {
                for (String line=reader.readLine(); line!=null && mc.isAlive(); line=reader.readLine()) {
                    connector.onDataReceived(new ServerOutputEvent(line));

                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            writeError(e);
        }
    }

    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.SERVERMANAGER, Console.PREFIX.INFO); }
    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.SERVERMANAGER, Console.PREFIX.ERROR); }

    public boolean getCalledStop() { return calledStop; }

    public static void debug(String msg) { debug(msg, false); }
    public static void debug(String msg, boolean override) {
        if (Main.getDebug()||override)
            Console.instance().writeLine(msg, Console.PREFIX.PLUGINMANAGER, Console.PREFIX.DEBUG);
    }
}
