package dev.necro.essentials.managers;

import dev.necro.essentials.NecroEssentials;
import dev.necro.essentials.config.ConfigFile;
import dev.necro.essentials.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainConfigManager {

    private final NecroEssentials plugin;

    private String prefix;
    private boolean chatLocked;
    private boolean oldInvsee;
    private boolean discouraged;
    private boolean rightClickInvsee;
    private boolean useConfirmation;

    public MainConfigManager(NecroEssentials plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.setMainConfig(new ConfigFile(plugin, "config.yml"));

        this.prefix = Utils.colorize(plugin.getMainConfig().getString("prefix", "§e§NECRO ESSENTIALS §a/ "));
        this.chatLocked = plugin.getMainConfig().getBoolean("chat-lock", false);
        this.oldInvsee = plugin.getMainConfig().getBoolean("old-invsee", false);
        this.discouraged = plugin.getMainConfig().getBoolean("discouraged", false);
        this.rightClickInvsee = plugin.getMainConfig().getBoolean("rightclick-invsee", true);
        this.useConfirmation = plugin.getMainConfig().getBoolean("use-confirmation", true);
    }

    public void save() {
        plugin.getMainConfig().set("prefix", this.prefix);
        plugin.getMainConfig().set("chat-lock", this.chatLocked);
        plugin.getMainConfig().set("discouraged", this.discouraged);

        plugin.getMainConfig().save();
    }
}
