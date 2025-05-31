package me.vovanov.spawnprotection;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import static me.vovanov.spawnprotection.ProtectionImpl.*;
import static me.vovanov.spawnprotection.ProtectionImpl.isNearSpawn;
import static me.vovanov.spawnprotection.SpawnProtection.PLUGIN;
import static me.vovanov.spawnprotection.SpawnProtection.debugMode;

public class EventListeners implements Listener {

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (checkPlayer(player, "попытался сломать блок")) event.setCancelled(true);
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (checkPlayer(player, "попытался поставить блок")) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPlace(EntityPlaceEvent event) {
        Entity placed = event.getEntity();
        Player player = event.getPlayer();
        if (player == null) return;
        if (isAllowedInteraction(placed)) return;

        if (checkPlayer(player, "попытался поставить сущность")) event.setCancelled(true);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;
        if (isAllowedInteraction(event.getClickedBlock())) return;

        if (checkPlayer(player, "попытался взаимодействовать с блоком")) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (isAllowedInteraction(clicked)) return;

        Player player = event.getPlayer();
        if (checkPlayer(player, "попытался взаимодействовать с сущностью")) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity clicked = event.getRightClicked();
        if (isAllowedInteraction(clicked)) return;

        Player player = event.getPlayer();
        if (checkPlayer(player, "попытался взаимодействовать с сущностью")) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if (isAllowedInteraction(damaged)) return;
        if (!(damager instanceof Player || damager instanceof Projectile)) return;

        Player player;
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player) player = (Player) projectile.getShooter();
        else if (damager instanceof Player) player = (Player) damager;
        else return;

        if (checkPlayer(player, "попытался навредить сущности")) event.setCancelled(true);
    }

    @EventHandler
    public void onHangingEntityDamage(HangingBreakByEntityEvent event) {
        Entity remover = event.getRemover();
        if (!(remover instanceof Player || remover instanceof Projectile)) return;

        Player player;
        if (remover instanceof Projectile projectile && projectile.getShooter() instanceof Player) player = (Player) projectile.getShooter();
        else if (remover instanceof Player) player = (Player) remover;
        else return;

        if (checkPlayer(player, "попытался сломать висящий блок")) event.setCancelled(true);
    }

    @EventHandler
    public void onHangingEntityPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (checkPlayer(player, "попытался поставить висящий блок")) event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEntity(PlayerBucketEntityEvent event) {
        Player player = event.getPlayer();
        if (checkPlayer(player, "попытался засунуть сущность в ведро")) event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (checkPlayer(player, "попытался опустошить ведро")) event.setCancelled(true);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        ItemStack result = event.getItemStack();
        if (result != null && result.getType().equals(Material.MILK_BUCKET)) return;
        if (checkPlayer(player, "попытался наполнить ведро")) event.setCancelled(true);
    }

    @EventHandler
    void blockExplode(BlockExplodeEvent event){
        if (isNearSpawn(event.getBlock().getLocation())) {
            event.blockList().clear();
            if (debugMode) PLUGIN.getLogger().info("Блок попытался взорваться");
        }
    }
    @EventHandler
    void entityExplode(EntityExplodeEvent event){
        if (isNearSpawn(event.getEntity().getLocation())) {
            event.blockList().clear();
            if (debugMode) PLUGIN.getLogger().info("Сущность попыталась взорваться");
        }
    }

    @EventHandler
    void entityBlockBreak(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if ((entity instanceof Wither || entity instanceof Enderman) && isNearSpawn(event.getBlock().getLocation())) {
            event.setCancelled(true);
            if (debugMode) PLUGIN.getLogger().info("Сущность попыталась изменить состояние блока");
        }
    }
}
