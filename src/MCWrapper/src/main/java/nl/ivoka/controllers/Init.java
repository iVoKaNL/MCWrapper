package nl.ivoka.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import nl.ivoka.API.xml.Config;
import nl.ivoka.API.console.Console;
import nl.ivoka.MCWrapper;
import nl.ivoka.Main;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Init implements Initializable {
    @FXML
    private TextField serverNameTxt;
    @FXML
    private TextField workingDirectoryTxt;
    @FXML
    private TextField serverFileTxt;
    @FXML
    private TextField javaExecTxt;
    @FXML
    private TextField minRamTxt;
    @FXML
    private TextField maxRamTxt;
    @FXML
    private TextField javaArgTxt;
    @FXML
    private TextField serverArgTxt;
    @FXML
    private TextField mcWrapperCoreIP;
    @FXML
    private TextField mcWrapperCorePort;

    @FXML
    private Slider minRamSld;
    @FXML
    private Slider maxRamSld;

    @FXML
    private RadioButton useCustomPluginsRdb;
    @FXML
    private RadioButton logOutputRdb;
    @FXML
    private RadioButton logServerOutputRdb;
    @FXML
    private RadioButton logMCWrapperOutputRdb;
    @FXML
    private RadioButton debugRdb;
    @FXML
    private RadioButton mcWrapperCoreRdb;

    @Override
    public void initialize(URL url, ResourceBundle rb) { Main.getStage().setTitle("MCWrapper - Initialize"); }

    @FXML
    private void onMinRamChange() { minRamTxt.setText(String.valueOf((int) Math.round(minRamSld.getValue()))); }
    @FXML
    private void onMaxRamChange() { maxRamTxt.setText(String.valueOf((int)Math.round(maxRamSld.getValue()))); }

    @FXML
    private void onMinDragDone() { minRamTxt.setText(String.valueOf((int) Math.round(minRamSld.getValue()))); }
    @FXML
    private void onMaxDragDone() { maxRamTxt.setText(String.valueOf((int)Math.round(maxRamSld.getValue()))); }

    @FXML
    private void onCloseBtn() { Main.stopMCWrapper(); }
    @FXML
    private void onCreateBtn() {
        try {
            Config config;
            if (workingDirectoryTxt.getText().length()>0)
                config = new Config("MCWrapper.xml");
            else
                config = new Config(MCWrapper.getMCWrapperConfig());

            config.setValue("ServerName", serverNameTxt.getText());
            config.setValue("ServerFile", serverFileTxt.getText());
            config.setValue("JavaExecutable", javaExecTxt.getText());

            config.setValue("Xms", minRamTxt.getText());
            config.setValue("Xmx", maxRamTxt.getText());
            config.setValue("JavaArguments", javaArgTxt.getText());

            config.setValue("ServerJarArguments", serverArgTxt.getText());
            config.setValue("UsePlugins", String.valueOf(useCustomPluginsRdb.isSelected()));

            config.setValue("WorkingDirectory", workingDirectoryTxt.getText());
            if (workingDirectoryTxt.getText().length()<1)
                config.setAttribute("WorkingDirectory", "usecustom", "false");
            else
                config.setAttribute("WorkingDirectory", "usecustom", "true");

            config.setChildValue("Logging", "LogServerOutput", String.valueOf(logServerOutputRdb.isSelected()));
            config.setChildValue("Logging", "LogMCWrapperOutput", String.valueOf(logMCWrapperOutputRdb.isSelected()));
            config.setChildValue("Logging", "FileDirectory", "MCWrapper/logs");
            config.setChildValue("Logging", "FileName", "latest.log");
            config.setAttribute("Logging", "enable", String.valueOf(logOutputRdb.isSelected()));

            config.setValue("Debug", String.valueOf(debugRdb.isSelected()));

            config.setChildValue("MCWrapper-core", "IPAddress", mcWrapperCoreIP.getText());
            config.setChildValue("MCWrapper-core", "Port", mcWrapperCorePort.getText());
            config.setAttribute("MCWrapper-core", "enable", String.valueOf(mcWrapperCoreRdb.isSelected()));

            config.saveConfig();

            Main.getMain().setScene("home");
        } catch (DocumentException | IOException e) { writeError(e); }
    }

    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.INIT, Console.PREFIX.ERROR); }
    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.INIT, Console.PREFIX.INFO); }
}
