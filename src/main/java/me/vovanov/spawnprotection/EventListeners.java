package me.vovanov.spawnprotection;

import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.vovanov.spawnprotection.ProtectionImpl.hasNotPlayedEnough;
import static me.vovanov.spawnprotection.ProtectionImpl.isAllowedInteraction;

public class EventListeners implements Listener {

    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (hasNotPlayedEnough(player)) event.setCancelled(true);
    }

    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (hasNotPlayedEnough(player)) event.setCancelled(true);
    }

    @EventHandler
    private void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;
        if (isAllowedInteraction(event.getClickedBlock())) return;

        if (hasNotPlayedEnough(player)) event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemFrame || event.getRightClicked() instanceof GlowItemFrame)) return;
        Player player = event.getPlayer();
        if (hasNotPlayedEnough(player)) event.setCancelled(true);
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof ItemFrame || event.getEntity() instanceof GlowItemFrame)) return;
        if (!(event.getDamager() instanceof Player || event.getDamager() instanceof Projectile)) return;

        Player player;
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player) player = (Player) projectile.getShooter();
        else if (event.getDamager() instanceof Player) player = (Player) event.getDamager();
        else return;

        if (hasNotPlayedEnough(player)) event.setCancelled(true);
    }

    @EventHandler
    private void onHangingEntityDamage(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player || event.getRemover() instanceof Projectile)) return;

        Player player;
        if (event.getRemover() instanceof Projectile projectile && projectile.getShooter() instanceof Player) player = (Player) projectile.getShooter();
        else if (event.getRemover() instanceof Player) player = (Player) event.getRemover();
        else return;

        if (hasNotPlayedEnough(player)) event.setCancelled(true);
    }

    @EventHandler
    private void onHangingEntityPlace(HangingPlaceEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (hasNotPlayedEnough(player)) event.setCancelled(true);
    }

}
