package nl.ivoka.controllers;

import javafx.fxml.Initializable;
import nl.ivoka.API.Console;

import java.net.URL;
import java.util.ResourceBundle;

public class Players implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.MAIN, Console.PREFIX.USERS, Console.PREFIX.ERROR); }
    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.MAIN, Console.PREFIX.USERS, Console.PREFIX.INFO); }
}
