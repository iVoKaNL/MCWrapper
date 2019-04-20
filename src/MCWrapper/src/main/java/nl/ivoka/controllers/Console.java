package nl.ivoka.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nl.ivoka.MCWrapper;
import nl.ivoka.Main;

import java.net.URL;
import java.util.ResourceBundle;

public class Console implements Initializable {

    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button sendBtn;

    @FXML
    private MenuButton listenersBtn;
    @FXML
    private MenuButton pluginsBtn;

    @FXML
    private TextArea outputArea;
    @FXML
    private TextField inputTxt;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @FXML
    public void onStartBtn() {
        if (Main.getMCWrapper() == null)
            Main.setMCWrapper(new MCWrapper());

        start();
    }

    public void start() {
        startBtn.setDisable(true);
        stopBtn.setDisable(false);
        sendBtn.setDisable(false);

        listenersBtn.setDisable(true); // TODO change to false
        pluginsBtn.setDisable(true); // TODO change to false

        inputTxt.setDisable(false);
    }
    public void stop() {
        startBtn.setDisable(false);
        stopBtn.setDisable(true);
        sendBtn.setDisable(true);

        listenersBtn.setDisable(true);
        pluginsBtn.setDisable(true);

        inputTxt.setDisable(true);
    }

    private void writeError(Exception e) { nl.ivoka.API.Console.instance().writeLine(e, nl.ivoka.API.Console.PREFIX.MAIN, nl.ivoka.API.Console.PREFIX.CONSOLE, nl.ivoka.API.Console.PREFIX.ERROR); }
    private void writeInfo(String msg) { nl.ivoka.API.Console.instance().writeLine(msg, nl.ivoka.API.Console.PREFIX.MAIN, nl.ivoka.API.Console.PREFIX.CONSOLE, nl.ivoka.API.Console.PREFIX.INFO); }
}
