package github.vanes430.bunbridge.spigot;

import github.vanes430.bunbridge.common.BridgeLogger;
import github.vanes430.bunbridge.common.BunConstants;
import org.bukkit.Bukkit;

public class SpigotBridgeLogger implements BridgeLogger {
    @Override
    public void info(String message) {
        Bukkit.getConsoleSender().sendMessage(BunConstants.PREFIX + message);
    }

    @Override
    public void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(BunConstants.PREFIX + "§e" + message);
    }

    @Override
    public void severe(String message) {
        Bukkit.getConsoleSender().sendMessage(BunConstants.PREFIX + "§c" + message);
    }
}
