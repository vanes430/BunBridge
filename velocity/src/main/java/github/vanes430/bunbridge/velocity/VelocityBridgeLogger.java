package github.vanes430.bunbridge.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import github.vanes430.bunbridge.common.BridgeLogger;
import github.vanes430.bunbridge.common.BunConstants;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class VelocityBridgeLogger implements BridgeLogger {

    private final ProxyServer server;

    public VelocityBridgeLogger(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void info(String message) {
        log(message);
    }

    @Override
    public void warning(String message) {
        log("§e" + message);
    }

    @Override
    public void severe(String message) {
        log("§c" + message);
    }

    private void log(String message) {
        server.getConsoleCommandSource().sendMessage(
            LegacyComponentSerializer.legacySection().deserialize(BunConstants.PREFIX + message)
        );
    }
}
