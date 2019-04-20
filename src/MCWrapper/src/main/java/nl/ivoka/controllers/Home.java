package nl.ivoka.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import nl.ivoka.API.Console;
import nl.ivoka.API.MCWrapperXML;
import nl.ivoka.MCWrapper;
import nl.ivoka.Main;
import nl.ivoka.PageRegister;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Home implements Initializable {

    @FXML
    private FlowPane playersRootPane;
    @FXML
    private ScrollPane mainRootPane;
    @FXML
    private FlowPane detailsRootPane;

    @FXML
    private Tab consoleTab;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Main.getStage().setTitle(MCWrapper.getName()+" - Home");

        try {
            if (new File(MCWrapper.getName()+".xml").exists()) {
                MCWrapperXML _mcWrapperXML = new MCWrapperXML(new File(MCWrapper.getName()+".xml"), true);
                if (_mcWrapperXML.isCustomWorkingDirectoryUsed())
                    Main.setMCWrapperXML(new MCWrapperXML(new File(_mcWrapperXML.getCustomWorkingDirectory()+"/"+MCWrapper.getMCWrapperConfig())));
                else
                    Main.setMCWrapperXML(new MCWrapperXML(MCWrapper.getMCWrapperConfig()));
            } else
                Main.setMCWrapperXML(new MCWrapperXML(MCWrapper.getMCWrapperConfig()));
        } catch (DocumentException | IOException e) { writeError(e); }

        try {
            Pane userPane = FXMLLoader.load(getClass().getResource(PageRegister.get("players").getLocation()));
            playersRootPane.getChildren().add(userPane);

            Pane consolePane = FXMLLoader.load(getClass().getResource(PageRegister.get("console").getLocation()));
            consoleTab.setContent(consolePane);

        } catch (IOException e) { writeError(e); }
    }

    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.MAIN, Console.PREFIX.ERROR); }
    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.MAIN, Console.PREFIX.INFO); }
}
