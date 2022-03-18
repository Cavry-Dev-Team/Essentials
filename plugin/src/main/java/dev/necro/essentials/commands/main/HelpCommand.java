package dev.necro.essentials.commands.main;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import dev.necro.essentials.commands.api.CommandClass;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HelpCommand extends CommandClass {

    @CommandMethod("necroessentials help|? [query]")
    @CommandDescription("Help menu for Necro Essentials")
    public void helpCommand(
            final @NonNull CommandSender sender,
            final @Nullable @Argument(value = "query", description = "The subcommand or the help page") @Greedy String query
    ) {
        plugin.getMainCommand().getMinecraftHelp().queryCommands(query == null ? "" : query, sender);
    }

}
