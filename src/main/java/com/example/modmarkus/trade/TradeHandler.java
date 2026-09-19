package com.example.modmarkus.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

public class TradeHandler {

    /**
     * Ouvre l'interface d'échange pour les deux joueurs.
     * Cette méthode doit être appelée côté serveur.
     */
    public static void openTrade(EntityPlayer p1, EntityPlayer p2) {
        if (p1.world.isRemote) return;

        // Créer un container partagé
        ContainerTrade container = new ContainerTrade(p1, p2);

        // Ouvrir pour Player 1
        openForPlayer(p1, container);
        // Ouvrir pour Player 2
        openForPlayer(p2, container);
    }

    private static void openForPlayer(EntityPlayer player, ContainerTrade container) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playermp = (EntityPlayerMP) player;
            playermp.getNextWindowId();
            playermp.closeContainer();
            
            // Envoyer le paquet pour ouvrir le GUI côté client
            // NetworkHandler.sendTo(new MessageOpenTrade(container), playermp);
            
            // Pour l'instant on va utiliser une approche simplifiée si on n'a pas de GuiHandler enregistré
            // Mais le standard Forge est d'utiliser IGuiHandler.
            
            // Hack pour forcer l'ouverture sans IGuiHandler complet immédiatement
            playermp.openContainer = container;
            playermp.openContainer.windowId = playermp.currentWindowId;
            playermp.openContainer.addListener(playermp);

            // Important: Configurer l'inventaire dans le container POUR ce joueur
            container.setupPlayerInventory(player);
            
            // On aura besoin d'un message réseau pour dire au client d'afficher le GuiTrade
            com.example.modmarkus.network.NetworkHandler.INSTANCE.sendTo(
                new MessageOpenTrade(player.getUniqueID()), playermp
            );
        }
    }
}
