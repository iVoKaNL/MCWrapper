package nl.ivoka.CustomCommands;

import nl.ivoka.API.Commands.CustomCommand;
import nl.ivoka.API.Config;
import nl.ivoka.CustomCommandHandler;
import nl.ivoka.Events.MCWrapperEvents.CommandEvent;
import nl.ivoka.Main;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;

public class oldconfigcommand implements CustomCommand {
    private CustomCommandHandler c;
    private String[] args;

    public oldconfigcommand(CommandEvent e, CustomCommandHandler c) {
        if (e!=null&&c!=null) {
            this.c = c;
            this.args = e.args;

            if (args.length == 0) {
                help();
            } else {
                if (args[0].equals("list")) list();
                else if (args[0].equals("show")) show();
                else if (args[0].equals("getvalues")) getvalues();
                else help();
            }
        }
    }

    public void help() {
        writeInfo("Usage 'oldconfigcommand':");
        writeInfo("  !oldconfigcommand help - Show this help menu");
        writeInfo("  !oldconfigcommand list - Shows a list of all oldconfigcommand files");
        writeInfo("  !oldconfigcommand show [oldconfigcommand] - Show 'oldconfigcommand', if not specified show default MCWrapper.xml oldconfigcommand");
        writeInfo("  !oldconfigcommand getvalues [oldconfigcommand] - Shows all values of 'oldconfigcommand', if not specified show default MCWrapper.xml oldconfigcommand");

        writeInfo("  !oldconfigcommand getvalue <key> [oldconfigcommand] - TODO");
        writeInfo("  !oldconfigcommand setvalue <key> <value> [oldconfigcommand] - TODO");
        writeInfo("  !oldconfigcommand getchildvalue <parentKey> <childKey> [oldconfigcommand] - TODO");
        writeInfo("  !oldconfigcommand setchildvalue <parentKey> <childKey> <value> [oldconfigcommand] - TODO");
        // TODO Add more commands
    }

    private void list() {
        File[] configFiles = Main.getConfigsDir().listFiles(name -> name.getPath().toLowerCase().endsWith(".xml"));

        String msg="Config _files ("+configFiles.length+"): ";
        for (File configFile : configFiles)
            msg+=configFile.getName()+", ";
        msg = msg.substring(0, msg.length()-2);

        writeInfo(msg);
    }

    private void show() {
        if (args.length==1) {
            writeInfo(Main.getConfig().toString());
        } else {
            try { writeInfo(new Config(Main.getConfigsDir()+"/"+args[1]).toString()); }
            catch (DocumentException | IOException e) { writeError(e); }
        }
    }

    private void getvalues() {
        if (args.length==1) {
            String msg="{";

            for (String line : Main.getConfig().getValues())
                msg+=line+", ";

            msg = msg.substring(0, msg.length()-2)+"}";

            writeInfo(msg);
        } else {
            try {
                String msg="{";

                for (String line : new Config(Main.getConfigsDir()+"/"+args[1]).getValues())
                    msg+=line+", ";

                msg = msg.substring(0, msg.length()-2)+"}";

                writeInfo(msg);
            }
            catch (DocumentException | IOException e) { writeError(e); }
        }
    }
}
