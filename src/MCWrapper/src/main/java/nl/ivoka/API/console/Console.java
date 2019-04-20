package nl.ivoka.API.console;

import nl.ivoka.MCWrapper;

import java.awt.*;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class Console {
    private static Console instance = null;

    public static synchronized Console instance() {
        if (instance == null)
            instance = new Console();
        return instance;
    }
    private Console() {}

    final static String name = ConsoleColors.GREEN_BOLD_BRIGHT+ MCWrapper.getName()+ ConsoleColors.RESET;

    // TODO create option in MCWrapper.xml to disable color
    public enum PREFIX {
        PLUGIN(ConsoleColors.PURPLE_UNDERLINED+"PLUGIN"+ ConsoleColors.RESET),
        INFO(ConsoleColors.GREEN_BOLD+"INFO"+ ConsoleColors.RESET),
        WARNING(ConsoleColors.RED_BOLD+"WARNING"+ ConsoleColors.RESET),
        DEBUG(ConsoleColors.YELLOW_BOLD+"DEBUG"+ ConsoleColors.RESET),
        ERROR(ConsoleColors.RED_BACKGROUND+""+ConsoleColors.YELLOW_BOLD+"ERROR"+ ConsoleColors.RESET),

        API(ConsoleColors.PURPLE_BOLD+"API"+ ConsoleColors.RESET),

        CONFIG(ConsoleColors.GREEN_UNDERLINED+"CONFIG"+ ConsoleColors.RESET),
        CONSOLE(ConsoleColors.YELLOW_UNDERLINED+"CONSOLE"+ ConsoleColors.RESET),
        LOGGER(ConsoleColors.RED_UNDERLINED+"LOGGER"+ ConsoleColors.RESET),
        PLAYER(ConsoleColors.CYAN_UNDERLINED+"PLAYER"+ ConsoleColors.RESET),
        SERVER(ConsoleColors.PURPLE_UNDERLINED+"SERER"+ ConsoleColors.RESET),

        JAVASCRIPTCONNECTOR(ConsoleColors.YELLOW_BOLD_BRIGHT+"JAVASCRIPTCONNECTOR"+ ConsoleColors.RESET),
        WEBUI(ConsoleColors.CYAN_BOLD_BRIGHT+"WEBUI"+ ConsoleColors.RESET),
        CUSTOMCOMMANDHANDLER(ConsoleColors.PURPLE_BOLD_BRIGHT+"CUSTOMCOMMANDHANDLER"+ ConsoleColors.RESET),

        ROOT(ConsoleColors.GREEN_BRIGHT+"ROOT"+ ConsoleColors.RESET),
        INIT(ConsoleColors.GREEN_BRIGHT+"INIT"+ ConsoleColors.RESET),
        MAIN(ConsoleColors.GREEN_BRIGHT+"MAIN"+ ConsoleColors.RESET),
        INPUT(ConsoleColors.WHITE_BRIGHT+"INPUT"+ ConsoleColors.RESET),

        USERS(ConsoleColors.WHITE_BRIGHT+"USERS"+ ConsoleColors.RESET),

        MCWRAPPER(ConsoleColors.BLACK_BRIGHT+""+ConsoleColors.GREEN_BACKGROUND+"CONSOLE"+ConsoleColors.RESET),
        SERVERMANAGER(ConsoleColors.BLACK_BOLD_BRIGHT+"SERVERMANAGER"+ ConsoleColors.RESET),
        PLUGINMANAGER(ConsoleColors.PURPLE_BOLD_BRIGHT+"PLUGINMANAGER"+ ConsoleColors.RESET),
        MINECRAFTCONNECTOR(ConsoleColors.BLUE_BOLD_BRIGHT+"MINECRAFTCONNECTOR"+ ConsoleColors.RESET),
        COMMANDHANDLER(ConsoleColors.WHITE_BOLD_BRIGHT+"COMMANDHANDLER"+ ConsoleColors.RESET),
        ;

        private final String prefix;

        PREFIX(String prefix) { this.prefix=prefix; }
        public String toString() { return prefix; }
    }

    public PREFIX getPrefix(String prefix) { return PREFIX.valueOf(prefix.toUpperCase()); }

    public void writeLine(String line, PREFIX... prefixes) { write(line+"\n", prefixes); }
    public void write(String line, PREFIX... prefixes) {
        Output(getPrefix(prefixes)+line);
    }

    public void writeLine(String line, Object[] args, PREFIX... prefixes) { write(line+"%n", args, prefixes); }
    public void write(String line, Object[] args, PREFIX... prefixes) {
        Output(String.format(getPrefix(prefixes)+line, args));
    }

    public void writeLine(Exception e, PREFIX... prefixes) { write(e+"\n", prefixes); }
    public void write(Exception e, PREFIX... prefixes) {
        Output(getPrefix(prefixes)+
                "Error: "+e.getMessage()+
                "\nStacktrace: "+e.getStackTrace());
    }

    private void Output(String line) {
        System.out.print(line);
        Logger.instance().writeLog(line);
    }

    public void clear() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {
            writeLine(ex, PREFIX.ERROR, PREFIX.CONSOLE);
        }
    }

    public void beep() { Toolkit.getDefaultToolkit().beep(); }

    // TODO add time to prefix
    private String getPrefix(PREFIX... prefixes) {
        String prefix;

        if (prefixes.length>=1) {
            prefix="["+name+"/";
            for (PREFIX loglevel : prefixes)
                prefix+=loglevel.toString()+"/";
            prefix=prefix.substring(0,prefix.length()-1)+"]: ";
        } else
            prefix="["+name+"]: ";

        prefix = "["+ ZonedDateTime.now().toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString()+"] "+prefix;

        return prefix;
    }
}
