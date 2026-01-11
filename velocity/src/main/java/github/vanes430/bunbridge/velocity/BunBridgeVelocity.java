package github.vanes430.bunbridge.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import github.vanes430.bunbridge.common.BunManager;

import java.nio.file.Paths;

@Plugin(
    id = "bunbridge",
    name = "BunBridge",
    version = "1.0.0",
    authors = {"vanes430"}
)
public class BunBridgeVelocity {

    private final ProxyServer server;
    private BunManager manager;

    @Inject
    public BunBridgeVelocity(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        VelocityBridgeLogger logger = new VelocityBridgeLogger(server);
        logger.info("§aVelocity enabled!");

        manager = new BunManager(Paths.get("bun"), logger);
        manager.init();
        
        server.getCommandManager().register("bun", new BunCommandVelocity(manager, "bun"));
        server.getCommandManager().register("bunsetup", new BunCommandVelocity(manager, "bunsetup"));
    }
}
