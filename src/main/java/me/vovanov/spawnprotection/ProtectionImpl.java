package me.vovanov.spawnprotection;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.round;
import static me.vovanov.spawnprotection.SpawnProtection.*;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;

public class ProtectionImpl {
    private static Set<Material> FORBIDDEN_BLOCK_INTERACTION;
    private static Set<EntityType> FORBIDDEN_ENTITY_INTERACTION;
    private static Set<String> FORBIDDEN_SUFFIXES;
    private static int SPAWN_RADIUS;
    private static long REQ_TIME;

    public static void setup() {
        FORBIDDEN_SUFFIXES = new HashSet<>();
        FORBIDDEN_BLOCK_INTERACTION = new HashSet<>();
        FORBIDDEN_ENTITY_INTERACTION = new HashSet<>();

        List<String> forbBlockInt = CONFIG.getStringList("forbidden-block-interaction");
        List<String> forbEntityInt = CONFIG.getStringList("forbidden-entity-interaction");

        FORBIDDEN_BLOCK_INTERACTION = forbBlockInt
                .stream()
                .filter(ProtectionImpl::isNotSuffix)
                .map(Material::matchMaterial)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        FORBIDDEN_ENTITY_INTERACTION = forbEntityInt
                .stream()
                .map(ProtectionImpl::matchEntityType)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        SPAWN_RADIUS = CONFIG.getInt("spawn-radius");
        REQ_TIME = CONFIG.getInt("time-required");
    }

    private static boolean isNotSuffix(String s) {
        if (!s.startsWith("#")) return true;
        FORBIDDEN_SUFFIXES.add(s.substring(1));
        return false;
    }

    private static EntityType matchEntityType(String name) {
        if (name == null) return null;
        String enumString = name
                .strip()
                .replace("minecraft:", "")
                .replace(" ", "_")
                .replace("-", "_")
                .toUpperCase();
        if (isValidEnum(EntityType.class, enumString)) return EntityType.valueOf(enumString);
        return null;
    }

    public static boolean isAllowedInteraction(Block clickedBlock) {
        if (clickedBlock == null) return true;
        Material clickedBlockType = clickedBlock.getType();
        if (FORBIDDEN_BLOCK_INTERACTION.contains(clickedBlockType)) return false;
        String clickedBlockName = clickedBlockType.toString();
        for (String forbiddenSuffix : FORBIDDEN_SUFFIXES) {
            if (clickedBlockName.endsWith(forbiddenSuffix)) return false;
        }
        return true;
    }

    public static boolean isAllowedInteraction(Entity clickedEntity) {
        if (clickedEntity == null) return true;
        EntityType clickedEntityType = clickedEntity.getType();
        return !FORBIDDEN_ENTITY_INTERACTION.contains(clickedEntityType);
    }

    public static boolean isNearSpawn(Location loc) {
        Location locClone = loc.clone();
        Location spawn = loc.getWorld().getSpawnLocation();
        spawn.setY(0);
        locClone.setY(0);
        return spawn.distanceSquared(locClone) < SPAWN_RADIUS*SPAWN_RADIUS;
    }

    public static boolean hasNotPlayedEnough(Player player) {
        if (player.hasPermission("sp.bypass")) return false;
        long playTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        return playTime < REQ_TIME;
    }

    private static Component message(long totalTicks) {
        long totalSeconds = totalTicks / 20;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        String formattedTime = String.format("%02dч %02dм %02dс", round(hours), round(minutes), round(seconds));
        return Component.text("§cНаиграйте ещё "+formattedTime+" чтобы сделать это в радиусе "+SPAWN_RADIUS+" блоков от спавна");
    }

    public static boolean checkPlayer(Player player, String debugMessage) {
        if (!hasNotPlayedEnough(player) || !isNearSpawn(player.getLocation())) return false;
        long playTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        player.sendActionBar(message(REQ_TIME-playTime));
        if (debugMode) PLUGIN.getLogger().info(player.getName()+" "+debugMessage);
        return true;
    }
}
