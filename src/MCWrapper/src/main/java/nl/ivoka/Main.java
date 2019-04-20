package nl.ivoka;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.ivoka.API.console.Console;
import nl.ivoka.API.xml.MCWrapperXML;

import java.io.File;
import java.io.IOException;

/**
 * Main class (this is the starting class)
 */
public class Main extends Application {

    private static Stage stage;
    private static Main main;
    private static MCWrapper mcWrapper;
    private static MCWrapperXML mcWrapperXML;

    @Override
    public void start(Stage stage) throws Exception {
        if (main == null) {
            Main.stage = stage;
            Main.main = this;

            Parent root;
            if (new File("MCWrapper.xml").exists() || MCWrapper.getMCWrapperConfig().exists())
                root = FXMLLoader.load(getClass().getResource(PageRegister.HOME.getLocation()));
            else
                root = FXMLLoader.load(getClass().getResource(PageRegister.INIT.getLocation()));
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        }
    }

    public static void stopMCWrapper() { Platform.exit(); }

    /**
     * Update root scene
     *
     * @param pageName  String - page name
     */
    public void setScene(String pageName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(PageRegister.get(pageName).getLocation()));
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) { writeError(e); }
    }

    // region Setters
    public static void setMCWrapper(MCWrapper mcWrapper) { Main.mcWrapper = mcWrapper; }
    public static void setMCWrapperXML(MCWrapperXML mcWrapperXML) { Main.mcWrapperXML = mcWrapperXML; }
    // endregion

    // region Getters
    public static Stage getStage() { return stage; }
    public static Main getMain() { return main; }
    public static MCWrapper getMCWrapper() { return mcWrapper; }
    public static MCWrapperXML getMCWrapperXML() { return mcWrapperXML; }
    // endregion

    private void writeError(Exception e) { Console.instance().writeLine(e, Console.PREFIX.ROOT, Console.PREFIX.ERROR); }
    private void writeInfo(String msg) { Console.instance().writeLine(msg, Console.PREFIX.ROOT, Console.PREFIX.INFO); }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) { launch(args); }
}