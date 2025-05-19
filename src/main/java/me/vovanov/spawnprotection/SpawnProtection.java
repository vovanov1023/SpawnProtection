package me.vovanov.spawnprotection;

import me.vovanov.spawnprotection.commands.SpawnProtectionCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnProtection extends JavaPlugin {
    public static Plugin PLUGIN;
    public static FileConfiguration CONFIG;
    @Override
    public void onEnable() {
        PLUGIN = this;
        CONFIG = getConfig();
        saveDefaultConfig();
        SpawnProtectionCommand.reloadPlugin();
        getCommand("spawnprotection").setExecutor(new SpawnProtectionCommand());
        getServer().getPluginManager().registerEvents(new explosions(), this);
        getServer().getPluginManager().registerEvents(new ProtectionImpl(), this);
        getLogger().info("Плагин запущен");
    }

    @Override
    public void onDisable() {getLogger().info("Плагин выключен");}
}
