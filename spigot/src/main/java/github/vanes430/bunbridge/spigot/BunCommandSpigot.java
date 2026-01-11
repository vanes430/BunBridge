package github.vanes430.bunbridge.spigot;

import github.vanes430.bunbridge.common.BunConstants;
import github.vanes430.bunbridge.common.BunManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BunCommandSpigot implements CommandExecutor {

    private final BunManager manager;

    public BunCommandSpigot(BunManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bunbridge.admin")) {
            sender.sendMessage(BunConstants.PREFIX + "§cNo permission.");
            return true;
        }

        if (label.equalsIgnoreCase("bunsetup")) {
            sender.sendMessage(BunConstants.PREFIX + "§eStarting Bun setup...");
            new Thread(() -> {
                manager.install();
                sender.sendMessage(BunConstants.PREFIX + "§aSetup complete (check console).");
            }).start();
            return true;
        }

        if (label.equalsIgnoreCase("bun")) {
            if (args.length == 0) {
                sender.sendMessage(BunConstants.PREFIX + "Usage: /bun <args>");
                return true;
            }
            new Thread(() -> manager.execute(args)).start();
            return true;
        }

        return false;
    }
}
