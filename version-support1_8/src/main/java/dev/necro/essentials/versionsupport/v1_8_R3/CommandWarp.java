package dev.necro.essentials.versionsupport.v1_8_R3;

import dev.necro.essentials.versionsupport.BukkitCommandWrap;
import org.bukkit.command.Command;

public class CommandWarp extends BukkitCommandWrap {

    @Override
    public void wrap(Command command, String alias) {
    }

    @Override
    public void unwrap(String command) {
    }

    @Override
    public boolean isUsed() {
        return false;
    }
}
