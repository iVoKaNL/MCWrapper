package nl.ivoka.API;

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
    private Logger() {
        File logFile;

        try {
            if (Main.getConfig().getAttribute("WorkingDirectory", "usecustom").equals("true"))
                logDir = new File(Main.getConfig().getValue("WorkingDirectory") + "/" + Main.getConfig().getChildValue("Logging", "FileDirectory"));
            else
                logDir = new File(Main.getConfig().getChildValue("Logging", "FileDirectory"));
            logFile = new File(Main.getConfig().getChildValue("Logging", "FileName"));
            logFilePath = new File(logDir + "/" + logFile);

            enableLogging = Boolean.valueOf(Main.getConfig().getAttribute("Logging", "enable"));
            enableServerOutputLogging = Boolean.valueOf(Main.getConfig().getChildValue("Logging", "LogServerOutput"));
            enableDebugOutputLogging = Boolean.valueOf(Main.getConfig().getChildValue("Logging", "LogDebugOutput"));

            if (enableLogging) {
                if (!logDir.exists()) {
                    if (!logDir.mkdirs())
                        Console.instance().writeLine("Error: something went wrong when making directories.", Console.PREFIX.ERROR, Console.PREFIX.LOGGER);
                }
                if (logFilePath.isFile()) {
                    if (!logFilePath.renameTo(new File(logDir + "/" + LocalDateTime.now().toString() + ".log")))
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

    private boolean enableLogging;
    private boolean enableServerOutputLogging;
    private boolean enableDebugOutputLogging;

    private FileWriter logWriter;

    public void writeLog(String message) {
        try {
            if (enableLogging) {
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
            enableLogging               =false;
            enableDebugOutputLogging    =false;
            enableServerOutputLogging   =false;

            if (logWriter != null) {
                logWriter.close();

                if (logFilePath.isFile())
                    if (!logFilePath.renameTo(new File(logDir + "/" + LocalDateTime.now().toString() + ".log")))
                        Console.instance().writeLine("Error: something went wrong when closing Logger and renaming log file.", Console.PREFIX.LOGGER, Console.PREFIX.ERROR);
            }
        } catch (IOException e) {}
    }

    public boolean isEnableServerOutputLogging() { return enableServerOutputLogging; }
    public boolean isEnableDebugOutputLogging() { return enableDebugOutputLogging; }
}
