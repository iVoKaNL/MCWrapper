module MCWrapper {

    // imports
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires dom4j;

    // make nl.ivoka accessable to javafx
    opens nl.ivoka to javafx.fxml;
    opens nl.ivoka.controllers to javafx.fxml;

    // export settings
    exports nl.ivoka;
}