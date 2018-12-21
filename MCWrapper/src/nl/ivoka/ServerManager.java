package nl.ivoka;

import nl.ivoka.API.Console;
import nl.ivoka.API.Logger;
import nl.ivoka.EventArgs.ServerEvents.ServerOutputEventArgs;
import nl.ivoka.EventArgs.PlayerEvent;
import org.dom4j.DocumentException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    public MinecraftConnector connector;
    public PlayerEvent events;

    private Process mc;
    private ProcessBuilder pb;

    private BufferedWriter writer;
    private BufferedReader reader;

    public boolean calledStop = false;
    private Thread outputThread;

    public ServerManager(String jarFile) throws IOException { new ServerManager(jarFile, null); }
    public ServerManager(String jarFile, File directory) throws IOException {
        List<String> startingArguments = new ArrayList<>();
        startingArguments.add(Main.config.getValue("JavaExecutable"));
        for (String arg : Main.config.getValues("JavaArguments")) {
            for (String _add : arg.split("\\s+")) {
                startingArguments.add(_add);
            }
        }
        startingArguments.add("-jar");
        startingArguments.add(jarFile);
        startingArguments.add(Main.config.getValue("ServerJarArguments"));

        pb = new ProcessBuilder(startingArguments);
        if (directory != null)
            pb.directory(directory);
        pb.redirectErrorStream(true);

        Console.instance.writeLine("Server starting with: "+pb.command());

        events = new PlayerEvent();
    }

    public void start() throws IOException, DocumentException {
        Console.instance.writeLine("Starting java...");

        mc = pb.start();
        writer = new BufferedWriter(new OutputStreamWriter(mc.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(mc.getInputStream()));

        outputThread = new Thread(() -> outputThread());
        outputThread.start();

        connector = new MinecraftConnector(this);
    }

    public void stop() throws IOException, InterruptedException {
        calledStop = true;

        if (mc.isAlive())
            writeLine("stop");

        mc.waitFor();

        Console.instance.writeLine("Server stopped. Press enter to exit! (may have to hit enter multiple times!)");
        Logger.instance.closeLog();
        Main.readLine();
        System.exit(0);
    }

    public void writeLine(String message) throws IOException {
        if (mc.isAlive()) {
            writer.write(message);
            writer.newLine();
            writer.flush();

            Logger.instance.writeLog("> "+message+"\n");
        }
    }

    private void outputThread() {
        try {
            if (Logger.instance.enableServerOutputLogging) {
                for (String line = reader.readLine(); line != null && mc.isAlive(); line = reader.readLine()) {
                    events.broadcast(new ServerOutputEventArgs(line));

                    System.out.println(line);
                    Logger.instance.writeLog(line + "\n");
                }
            } else {
                for (String line = reader.readLine(); line != null && mc.isAlive(); line = reader.readLine()) {
                    events.broadcast(new ServerOutputEventArgs(line));

                    System.out.println(line);
                }
            }
        } catch (IOException ioException) {

        }
    }

}
