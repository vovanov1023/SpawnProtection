package me.vovanov.spawnprotection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.vovanov.spawnprotection.SpawnProtection.CONFIG;
import static me.vovanov.spawnprotection.SpawnProtection.PLUGIN;

public class SpawnProtectionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (!args[0].equalsIgnoreCase("reload")) {
            return false;
        }
        if (!sender.hasPermission("sp.reload")) {
            sender.sendMessage("§cНедостаточно прав");
            return true;
        }
        reloadPlugin();
        sender.sendMessage("§eФайл конфигурации плагина перезагружен");
        return true;
    }

    public static void reloadPlugin() {
        PLUGIN.reloadConfig();
        CONFIG = PLUGIN.getConfig();
        ProtectionImpl.setup();
        SpawnProtection.debugMode = CONFIG.getBoolean("debug");
    }
}
