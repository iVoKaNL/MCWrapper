package nl.ivoka;

import nl.ivoka.EventArgs.ServerEvents.ServerOutputEventArgs;
import nl.ivoka.EventArgs.PlayerEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ServerManager {
    public MinecraftConnector connector;
    PlayerEvent events;

    private Process mc;
    private ProcessBuilder pb;

    private BufferedWriter writer;
    private BufferedReader reader;
    private Thread outputThread;

    private List _listeners = new ArrayList();

    public ServerManager(String jarFile, String dir) {
        /*
        String executable = Main.config.getValue("JavaExecutable");
        String arguments = Main.config.getValue("JavaArguments");
        String dir = Main.config.getValue("ServerDirectory");
        */

        String executable = "java";
        String arguments = "-jar";

        pb = new ProcessBuilder(executable, arguments, jarFile, "nogui");
        pb.redirectErrorStream(true);
        pb.directory(new File(dir));

        events = new PlayerEvent();
    }

    public void start() throws IOException, InterruptedException {
        System.out.println("Starting java...");
        mc = pb.start();
        writer = new BufferedWriter(new OutputStreamWriter(mc.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(mc.getInputStream()));

        outputThread = new Thread(() -> outputThread());
        outputThread.start();

        connector = new MinecraftConnector(this);

        //mc.waitFor();
    }

    public void stop() throws IOException, InterruptedException {
        if (mc.isAlive())
            writeLine("stop");

        mc.waitFor();

        System.out.println("Server stopped. Press any key to exit!");
        Main.readLine();
        System.exit(0);
    }

    public void writeLine(String message) throws IOException {
        if (mc.isAlive()) {
            writer.write(message);
            writer.newLine();
            writer.flush();
        }
    }

    private void outputThread() {
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                events.broadcast(new ServerOutputEventArgs(line));
                System.out.println(line);
            }
        } catch (IOException ioException) {

        }
    }

}
