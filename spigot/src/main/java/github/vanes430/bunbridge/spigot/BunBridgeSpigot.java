package github.vanes430.bunbridge.spigot;

import github.vanes430.bunbridge.common.BunManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public class BunBridgeSpigot extends JavaPlugin {

    private BunManager manager;

    @Override
    public void onEnable() {
        SpigotBridgeLogger logger = new SpigotBridgeLogger();
        logger.info("§aSpigot enabled!");

        manager = new BunManager(Paths.get("bun"), logger);
        manager.init();

        BunCommandSpigot cmd = new BunCommandSpigot(manager);
        getCommand("bun").setExecutor(cmd);
        getCommand("bunsetup").setExecutor(cmd);
    }
}
