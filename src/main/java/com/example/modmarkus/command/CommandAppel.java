package com.example.modmarkus.command;

import com.example.modmarkus.gui.GuiIncomingCall;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommandAppel extends CommandBase {

    @Override
    public String getName() {
        return "appel";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/appel - Simule un appel entrant";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0; // Tous les joueurs
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true; // Autoriser tout le monde explicitement
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            // Note: Normalement on devrait envoyer un packet si c'est exécuté côté serveur
            // Mais pour simplifier et comme c'est souvent testé en solo:
            // Nous allons utiliser le proxy pour déclencher l'affichage ou un packet.
            // Vu que l'UI est Client Only, on va juste dire au client d'ouvrir l'écran.
            
            // Pour être propre, on envoie un packet de "Déclenchement d'appel" du serveur vers le client.
            // Mais ici, on va faire un petit hack pour la démo si c'est possible:
            com.example.modmarkus.ModMarkus.proxy.openIncomingCallGui();
            sender.sendMessage(new TextComponentString("§a[ModMarkus] Appel lancé !"));
        }
    }
}
