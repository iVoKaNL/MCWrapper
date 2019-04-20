package nl.ivoka.API.console;

import nl.ivoka.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {
    private static Logger instance = null;

    public static synchronized Logger instance() {
        if (instance == null)
            instance = new Logger();
        return instance;
    }
    private Logger() { reload(); }

    public void reload() {
        try {
            logClosed=false;
            logDir = Main.getMCWrapperXML().getLogFileDirectory();
            logFilePath = new File(logDir+"/"+Main.getMCWrapperXML().getLogFileName());

            if (Main.getMCWrapperXML().isLoggingEnabled()) {
                if (!logDir.exists()) {
                    if (!logDir.mkdirs())
                        Console.instance().writeLine("Error: something went wrong when making directories.", Console.PREFIX.ERROR, Console.PREFIX.LOGGER);
                }
                if (logFilePath.isFile()) {
                    if (!logFilePath.renameTo(new File(logDir + "/" + LocalDateTime.now().toString().replace('/', '-').replace(':','-') + ".log")))
                        Console.instance().writeLine("Error: something went wrong when closing Logger and renaming log file.", Console.PREFIX.ERROR, Console.PREFIX.LOGGER);
                }

                logWriter = new FileWriter(logFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();

            closeLog();
        }
    }

    private File logFilePath;
    private File logDir;
    private FileWriter logWriter;

    private boolean logClosed;

    public void writeLog(String message) {
        try {
            if (Main.getMCWrapperXML().isLoggingEnabled() && !logClosed) {
                logWriter.write(message);
                logWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();

            closeLog();
        }
    }
    public void closeLog() {
        try {
            logClosed=true;

            if (logWriter != null) {
                logWriter.close();

                if (logFilePath.isFile())
                    if (!logFilePath.renameTo(new File(logDir + "/" + LocalDateTime.now().toString().replace('/', '-').replace(':','-') + ".log")))
                        Console.instance().writeLine("Error: something went wrong when closing Logger and renaming log file.", Console.PREFIX.LOGGER, Console.PREFIX.ERROR);
            }
        } catch (IOException e) {}
    }

    // TODO is...enabled -> MCWrapperXML.java
}
