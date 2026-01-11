package github.vanes430.bunbridge.velocity;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.CommandSource;
import github.vanes430.bunbridge.common.BunConstants;
import github.vanes430.bunbridge.common.BunManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.concurrent.CompletableFuture;

public class BunCommandVelocity implements SimpleCommand {

    private final BunManager manager;
    private final String commandName;

    public BunCommandVelocity(BunManager manager, String commandName) {
        this.manager = manager;
        this.commandName = commandName;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!source.hasPermission("bunbridge.admin")) {
            source.sendMessage(LegacyComponentSerializer.legacySection().deserialize(BunConstants.PREFIX + "§cNo permission."));
            return;
        }

        if (commandName.equals("bunsetup")) {
            source.sendMessage(LegacyComponentSerializer.legacySection().deserialize(BunConstants.PREFIX + "§eStarting Bun setup..."));
            CompletableFuture.runAsync(() -> {
                manager.install();
                source.sendMessage(LegacyComponentSerializer.legacySection().deserialize(BunConstants.PREFIX + "§aSetup complete (check console)."));
            });
            return;
        }

        if (commandName.equals("bun")) {
            if (args.length == 0) {
                source.sendMessage(LegacyComponentSerializer.legacySection().deserialize(BunConstants.PREFIX + "Usage: /bun <args>"));
                return;
            }
            CompletableFuture.runAsync(() -> manager.execute(args));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bunbridge.admin");
    }
}
