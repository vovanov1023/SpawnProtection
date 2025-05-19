package me.vovanov.spawnprotection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import static me.vovanov.spawnprotection.ProtectionImpl.isNearSpawn;

public class explosions implements Listener {

    @EventHandler
    void blockExplode(BlockExplodeEvent event){
        if (isNearSpawn(event.getBlock().getLocation())) {
            event.blockList().clear();
        }
    }
    @EventHandler
    void entityExplode(EntityExplodeEvent event){
        if (isNearSpawn(event.getEntity().getLocation())) {
            event.blockList().clear();
        }
    }
}
