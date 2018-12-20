package nl.ivoka.API;

import org.dom4j.DocumentException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {
    public static Logger instance;

    public static class logger {
        public logger() throws IOException, DocumentException { instance = new Logger(); }
    }

    private Logger() throws IOException, DocumentException {
        Config config = new Config(new File("MCWrapper.xml"));

        if (config.getAttribute("WorkingDirectory", "usecustom").equals("true"))
            this.logDir = new File(config.getValue("WorkingDirectory") + "/" + config.getChildValue("Logging", "FileDirectory"));
        else
            this.logDir = new File(config.getChildValue("Logging", "FileDirectory"));
        this.logFile = new File(config.getChildValue("Logging", "FileName"));
        this.logFilePath = new File(logDir + "/" + logFile);

        this.enableLogging = Boolean.valueOf(config.getAttribute("Logging", "enable"));
        this.enableServerOutputLogging = Boolean.valueOf(config.getChildValue("Logging", "LogServerOutput"));
        this.enableDebugOutputLogging = Boolean.valueOf(config.getChildValue("Logging", "LogDebugOutput"));

        if (enableLogging) {
            if (!logDir.exists())
                logDir.mkdirs();
            if (logFilePath.isFile())
                logFilePath.renameTo(new File(logDir + "/" + LocalDateTime.now().toString() + ".log"));

            logWriter = new FileWriter(logFilePath);
        }
    }

    private File logDir;
    private File logFile;
    private File logFilePath;

    public boolean enableLogging;
    public boolean enableServerOutputLogging;
    public boolean enableDebugOutputLogging;

    private FileWriter logWriter;

    public void writeLog(String message) throws IOException {
        if (enableLogging) {
            logWriter.write(message);
            logWriter.flush();
        }
    }
    public void closeLog() throws IOException { logWriter.flush(); logWriter.close(); }
}
