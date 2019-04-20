package nl.ivoka;

import java.util.Arrays;

/**
 * This class is used for page registration
 */
public enum PageRegister {
    INIT( "/pages/Init.fxml", "init"),
    HOME("/pages/Home.fxml", "home"),

    USERS("/pages/Players.fxml", "players"),

    CONSOLE( "/pages/Console.fxml", "console"),
    SETTINGS("/pages/Settings.fxml", "settings"),
    PLUGINS("/pages/Plugins.fxml", "plugins"),

    DETAILS("/pages/Details.fxml", "details");

    private String location, alias;

    /**
     * @param location      String - URL to fxml file (located in resources->pages)
     */
    PageRegister(String location, String alias) { this.location = location; this.alias = alias; }

    /**
     * Get a specific PageRegistration by its alias
     *
     * @param   pageName    String - page alias
     * @return  PageRegister or null
     */
    public static PageRegister get(String pageName) {
        return Arrays.stream(PageRegister.values())
                .filter(x -> x.alias.equalsIgnoreCase(pageName))
                .findFirst()
                .orElse(null);
    }

    // region getters
    public String getLocation() { return location; }
    public String getAlias() { return alias; }
    // endregion
}