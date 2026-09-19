package com.example.modmarkus.handler;

import com.example.modmarkus.entity.EntityImage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import java.util.Random;

public class EntitySpawner {
    
    private int spawnCooldown = 0;
    private static final int SPAWN_INTERVAL = 200; // Spawn toutes les 10 secondes
    
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        
        spawnCooldown--;
        
        if (spawnCooldown <= 0) {
            spawnCooldown = SPAWN_INTERVAL;
            spawnImage();
        }
    }
    
    private void spawnImage() {
        // Récupérer tous les joueurs connectés
        // Note: À améliorer avec une gestion serveur appropriée
    }
    
    public static void spawnImageForPlayer(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;
        
        Random random = new Random();
        
        // Position aléatoire à côté du joueur
        double offsetX = (random.nextDouble() - 0.5) * 6.0; // Entre -3 et 3
        double offsetZ = (random.nextDouble() - 0.5) * 6.0;
        
        double spawnX = player.posX + offsetX;
        double spawnY = player.posY;
        double spawnZ = player.posZ + offsetZ;
        
        EntityImage entity = new EntityImage(player.world, spawnX, spawnY, spawnZ);
        player.world.spawnEntity(entity);
    }
}
