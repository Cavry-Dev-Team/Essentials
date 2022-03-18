package dev.necro.essentials.commands.main;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import dev.necro.essentials.commands.api.CommandClass;
import dev.necro.essentials.utils.Utils;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class InfoCommand extends CommandClass {

    @CommandMethod("necroessentials info|ver")
    @CommandDescription("Information about the plugin")
    public void infoCommand(
            final @NonNull CommandSender sender
    ) {
        sender.sendMessage(Utils.getPluginDescription());
    }

}
