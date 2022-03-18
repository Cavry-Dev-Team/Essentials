package dev.necro.essentials.commands.main;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import dev.necro.essentials.commands.api.CommandClass;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfirmCommand extends CommandClass {

    @ProxiedBy("confirm")
    @CommandMethod("necroessentials confirm|yes|accept")
    @CommandDescription("Confirms a pending command action")
    public void confirmCommand(
            final @NonNull Player sender
    ) {
        plugin.getConfirmationManager().confirm(sender);
    }

    @ProxiedBy("cancel")
    @CommandMethod("necroessentials cancel|undo|deny|no")
    @CommandDescription("Cancels a pending command action")
    public void cancelCommand(
            final @NonNull Player sender
    ) {
        plugin.getConfirmationManager().deleteConfirmation(sender);
    }

}
