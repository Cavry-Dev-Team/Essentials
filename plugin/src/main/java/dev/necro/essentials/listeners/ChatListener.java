package dev.necro.essentials.listeners;

import dev.necro.essentials.NecroEssentials;
import dev.necro.essentials.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@AllArgsConstructor
public class ChatListener implements Listener {

    private final NecroEssentials plugin;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getMainConfigManager().isChatLocked()) {
            if (!Utils.checkPermission(event.getPlayer(), "chatlock.bypass", false, false, true, null)) {
                event.setCancelled(true);
            }
        }
    }
}
