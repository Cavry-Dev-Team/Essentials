package dev.necro.essentials.commands.essentials;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.specifier.Greedy;
import dev.necro.essentials.commands.api.CommandClass;
import dev.necro.essentials.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RenameCommand extends CommandClass {

    @CommandMethod("rename <name>")
    @CommandDescription("Renames the item that you are currently holding")
    public void renameCommand(
            final @NonNull Player player,
            final @NonNull @Argument(value = "name", description = "The new name for the held item") @Greedy String name
    ) {
        if (!Utils.checkPermission(player, "rename")) {
            return;
        }

        ItemStack itemInHand = plugin.getVersionSupport().getItemInHand(player);
        if (itemInHand == null || itemInHand.getType() == Material.AIR) {
            player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§cYou are not holding anything!");
            return;
        }

        ItemMeta itemMeta = itemInHand.getItemMeta();
        itemMeta.setDisplayName(Utils.colorize(name));

        itemInHand.setItemMeta(itemMeta);

        player.updateInventory();
        player.sendMessage(plugin.getMainConfigManager().getPrefix() + "§eItem successfully renamed.");
    }
}
