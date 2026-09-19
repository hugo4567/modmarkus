package com.example.modmarkus.command;

import com.example.modmarkus.entity.EntityImage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import java.util.Random;

public class CommandSpawnImage extends CommandBase {
    
    @Override
    public String getName() {
        return "spawnimage";
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "/spawnimage - Spawn une image à côté du joueur";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Tous les joueurs
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        try {
            EntityPlayer player = null;
            
            if (sender instanceof EntityPlayer) {
                player = (EntityPlayer) sender;
            } else if (args.length > 0) {
                player = server.getPlayerList().getPlayerByUsername(args[0]);
            }
            
            if (player != null && !player.world.isRemote) {
                sender.sendMessage(new TextComponentString("§a[ModMarkus] Commande /spawnimage exécutée"));
                spawnImageForPlayer(player);
            } else {
                sender.sendMessage(new TextComponentString("§c[ModMarkus] ERREUR: Joueur ou monde invalide"));
            }
        } catch (Exception e) {
            System.out.println("[ModMarkus] ERREUR COMMANDE: " + e.getMessage());
            e.printStackTrace();
            sender.sendMessage(new TextComponentString("§c[ModMarkus] ERREUR: " + e.getMessage()));
        }
    }
    
    private void spawnImageForPlayer(EntityPlayer player) {
        try {
            // Calculer la position 10 blocs devant le joueur
            float yaw = player.rotationYaw;
            double angle = Math.toRadians(yaw);
            
            // En Minecraft, -sin(angle) est X et cos(angle) est Z pour le regard
            // Mais pour "devant", il faut souvent inverser ou ajuster selon le repère
            double lookX = -Math.sin(angle);
            double lookZ = Math.cos(angle);
            
            double distance = 10.0;
            double spawnX = player.posX + (lookX * distance);
            double spawnY = player.posY + 1.0;
            double spawnZ = player.posZ + (lookZ * distance);
            
            player.sendMessage(new TextComponentString("§a[ModMarkus] Spawn de l'image à 10 blocs devant vous à (" + 
                Math.round(spawnX) + ", " + Math.round(spawnY) + ", " + Math.round(spawnZ) + ")"));
            
            EntityImage entity = new EntityImage(player.world, spawnX, spawnY, spawnZ);
            player.world.spawnEntity(entity);
            player.sendMessage(new TextComponentString("§a[ModMarkus] Image spawnée avec succès!"));
        } catch (Exception e) {
            System.out.println("[ModMarkus] ERREUR SPAWN: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(new TextComponentString("§c[ModMarkus] Erreur spawn: " + e.getMessage()));
        }
    }
}
