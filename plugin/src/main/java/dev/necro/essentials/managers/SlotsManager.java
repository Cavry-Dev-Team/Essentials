package dev.necro.essentials.managers;

import dev.necro.essentials.NecroEssentials;
import dev.necro.essentials.config.ConfigFile;
import dev.necro.essentials.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

@Getter
@Setter
public class SlotsManager {

    private final NecroEssentials plugin;

    private boolean enabled = false;
    private int maxPlayers = 1000;
    private String denyMessage = "§cServer is full!";

    public SlotsManager(NecroEssentials plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.setSlotsConfig(new ConfigFile(plugin, "slots.yml"));

        this.enabled = plugin.getSlotsConfig().getBoolean("toggled", false);
        this.maxPlayers = plugin.getSlotsConfig().getInt("max-players", 1000);
        this.denyMessage = Utils.colorize(plugin.getSlotsConfig().getString("deny-message", "§cServer is full!"));
    }

    public void save() {
        plugin.getSlotsConfig().set("toggled", this.enabled);
        plugin.getSlotsConfig().set("max-players", this.maxPlayers);
        plugin.getSlotsConfig().set("deny-message", this.denyMessage);

        plugin.getSlotsConfig().save();
    }

    public void checkJoin(PlayerLoginEvent event) {
        if (this.enabled) {
            PlayerLoginEvent.Result result = event.getResult();
            if (result.equals(PlayerLoginEvent.Result.KICK_FULL) || result.equals(PlayerLoginEvent.Result.ALLOWED)) {
                int currentOnline = Bukkit.getOnlinePlayers().size();
                if (currentOnline < plugin.getSlotsManager().getMaxPlayers()) {
                    event.allow();
                    return;
                }

                Player player = event.getPlayer();
                if (Utils.checkPermission(player, "join_full")) {
                    event.allow();
                } else {
                    event.disallow(PlayerLoginEvent.Result.KICK_FULL, plugin.getSlotsManager().getDenyMessage());
                }
            }
        }
    }
}
