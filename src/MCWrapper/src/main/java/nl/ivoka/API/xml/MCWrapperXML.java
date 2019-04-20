package nl.ivoka.API.xml;

import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class MCWrapperXML {
    private File mcWrapperXML;

    private String serverName="MCWrapper";
    private String serverFile="spigot-1.13.2.jar";
    private String javaExecutable="java";
    private int xms=1024;
    private int xmx=1024;
    private String javaArguments=null;
    private String serverJarArguments="nogui";
    private boolean usePlugins=true;
    private boolean useCustomWorkingDirectory=false;
    private File customWorkingDirectory=null;
    private boolean enableLogging=true;
    private boolean logServerOutput=true;
    private boolean logMCWrapperOutput=true;
    private File logFileDirectory=new File("MCWrapper/logs");
    private File logFileName=new File("latest.log");
    private boolean debug=false;
    private boolean useMCWrapperCore=true;
    private InetAddress mcWrapperCoreIPAddress=InetAddress.getLocalHost();
    private int mcWrapperCorePort=59898;

    public MCWrapperXML() throws DocumentException, IOException { load(new File("temp.xml")); }
    public MCWrapperXML(File mcWrapperXML) throws DocumentException, IOException { load(mcWrapperXML); }
    public MCWrapperXML(File mcWrapperXML, boolean customDirCheck) throws DocumentException, IOException { if (customDirCheck) loadDir(mcWrapperXML); else load(mcWrapperXML); }

    private void loadDir(File mcWrapperXML) throws DocumentException, IOException {
        Config config = new Config(mcWrapperXML);

        useCustomWorkingDirectory = Boolean.getBoolean(config.getAttribute("WorkingDirectory", "usecustom"));
        customWorkingDirectory = new File(config.getValue("WorkingDirectory"));
    }

    public void load(File mcWrapperXML) throws DocumentException, IOException{ this.mcWrapperXML=mcWrapperXML; reload(); }
    public void reload() throws DocumentException, IOException {
        Config config = new Config(mcWrapperXML);

        serverName = config.getValue("ServerName");
        serverFile = config.getValue("ServerFile");
        javaExecutable = config.getValue("JavaExecutable");
        xms = Integer.valueOf(config.getValue("Xms"));
        xmx = Integer.valueOf(config.getValue("Xmx"));
        javaArguments = config.getValue("JavaArguments");
        serverJarArguments = config.getValue("ServerJarArguments");
        usePlugins = Boolean.getBoolean(config.getValue("UsePlugins"));
        useCustomWorkingDirectory = Boolean.getBoolean(config.getAttribute("WorkingDirectory", "usecustom"));
        customWorkingDirectory = new File(config.getValue("WorkingDirectory"));
        enableLogging = Boolean.getBoolean(config.getAttribute("Logging", "enable"));
        logServerOutput = Boolean.getBoolean(config.getChildValue("Logging", "LogServerOutput"));
        logMCWrapperOutput = Boolean.getBoolean(config.getChildValue("Logging", "LogMCWrapperOutput"));
        if (useCustomWorkingDirectory)
            logFileDirectory = new File(customWorkingDirectory+"/"+config.getChildValue("Logging", "FileDirectory"));
        else
            logFileDirectory = new File(config.getChildValue("Logging", "FileDirectory"));
        logFileName = new File(config.getChildValue("Logging", "FileName"));
        debug = Boolean.getBoolean(config.getValue("Debug"));
        useMCWrapperCore = Boolean.getBoolean(config.getAttribute("MCWrapper-core", "enable"));
        mcWrapperCoreIPAddress = InetAddress.getByName(config.getChildValue("MCWrapper-core", "IPAddress"));
        mcWrapperCorePort = Integer.valueOf(config.getChildValue("MCWrapper-core", "Port"));
    }

    // region Getters
    public String getServerName() { return serverName; }
    public String getServerFile() { return serverFile; }
    public String getJavaExecutable() { return javaExecutable; }
    public int getXms() { return xms; }
    public int getXmx() { return xmx; }
    public String getJavaArguments() { return javaArguments; }
    public String getServerJarArguments() { return serverJarArguments; }
    public boolean isPluginsUsed() { return usePlugins; }
    public boolean isCustomWorkingDirectoryUsed() { return useCustomWorkingDirectory; }
    public File getCustomWorkingDirectory() { return customWorkingDirectory; }
    public boolean isLoggingEnabled() { return enableLogging; }
    public boolean isServerOutputLogged() { return logServerOutput; }
    public boolean isMCWrapperOutputLogged() { return logMCWrapperOutput; }
    public File getLogFileDirectory() { return logFileDirectory; }
    public File getLogFileName() { return logFileName; }
    public boolean isDebugEnabled() { return debug; }
    public boolean isMCWrapperCoreEnabled() { return useMCWrapperCore; }
    public InetAddress getMCWrapperCoreIPAddress() { return mcWrapperCoreIPAddress; }
    public int getMCWrapperCorePort() { return mcWrapperCorePort; }
    // endregion
}
