package com.example.modmarkus.handler;

import com.example.modmarkus.entity.EntityImage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Random;
import java.util.List;

public class AutoSpawnHandler {
    
    private static final Random random = new Random();
    private int ticksSinceLastCheck = 0;
    // Vérification toutes les minutes (20 ticks/sec * 60 sec = 1200 ticks)
    private static final int CHECK_INTERVAL = 1200; 

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START || event.world.isRemote) return;
        
        ticksSinceLastCheck++;
        
        // Toutes les minutes, on a 1 chance sur 60 de faire apparaître l'image
        if (ticksSinceLastCheck >= CHECK_INTERVAL) {
            ticksSinceLastCheck = 0;
            
            if (random.nextInt(60) == 0) {
                List<EntityPlayer> players = event.world.playerEntities;
                if (!players.isEmpty()) {
                    EntityPlayer target = players.get(random.nextInt(players.size()));
                    spawnImageForPlayer(target);
                    System.out.println("[ModMarkus] Spawn chanceux (1/60) d'EntityImage sur " + target.getName());
                }
            }
        }
    }
    
    public static void spawnImageForPlayer(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;
        
        World world = player.world;
        
        // Position à 10 blocs devant le joueur (comme la commande /spawnimage pour la cohérence)
        float yaw = player.rotationYaw;
        double angle = Math.toRadians(yaw);
        
        double spawnX = player.posX - Math.sin(angle) * 10.0;
        double spawnZ = player.posZ + Math.cos(angle) * 10.0;
        double spawnY = player.posY + 1.0;
        
        EntityImage entity = new EntityImage(world, spawnX, spawnY, spawnZ);
        world.spawnEntity(entity);
    }
}
