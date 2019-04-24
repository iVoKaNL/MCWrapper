module MCWrapper {

    // imports
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires jansi;
    requires dom4j;

    // make nl.ivoka accessable to javafx
    opens nl.ivoka to javafx.fxml;
    opens nl.ivoka.controllers to javafx.fxml;
    opens nl.ivoka.API.xml to dom4j;

    // export settings
    exports nl.ivoka;
}