package com.example.modmarkus.trade;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandTrade extends CommandBase {

    // Stockage temporaire des requêtes: Destinataire UUID -> Expéditeur UUID
    public static final Map<UUID, UUID> pendingRequests = new HashMap<>();

    @Override
    public String getName() {
        return "trade";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/trade <joueur> ou /trade accept";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            throw new CommandException("Seul un joueur peut utiliser cette commande.");
        }

        EntityPlayer player = (EntityPlayer) sender;

        if (args.length < 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        if (args[0].equalsIgnoreCase("accept")) {
            handleAccept(server, player);
        } else {
            handleRequest(server, player, args[0]);
        }
    }

    private void handleRequest(MinecraftServer server, EntityPlayer sender, String targetName) throws CommandException {
        EntityPlayer target = server.getPlayerList().getPlayerByUsername(targetName);

        if (target == null) {
            throw new CommandException("Joueur introuvable: " + targetName);
        }

        if (target == sender) {
            throw new CommandException("Vous ne pouvez pas échanger avec vous-même.");
        }

        pendingRequests.put(target.getUniqueID(), sender.getUniqueID());

        sender.sendMessage(new TextComponentString("§aRequête d'échange envoyée à " + target.getName()));

        TextComponentString msg = new TextComponentString("§e" + sender.getName() + " souhaite faire un échange avec vous. ");
        TextComponentString acceptBtn = new TextComponentString("§6§l[ACCEPTER]");
        acceptBtn.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade accept"));
        acceptBtn.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Cliquez pour accepter l'échange")));
        msg.appendSibling(acceptBtn);

        target.sendMessage(msg);
    }

    private void handleAccept(MinecraftServer server, EntityPlayer acceptor) throws CommandException {
        UUID senderUUID = pendingRequests.remove(acceptor.getUniqueID());

        if (senderUUID == null) {
            throw new CommandException("Vous n'avez aucune requête d'échange en attente.");
        }

        EntityPlayer sender = server.getPlayerList().getPlayerByUUID(senderUUID);

        if (sender == null) {
            throw new CommandException("Le joueur qui a envoyé la requête n'est plus en ligne.");
        }

        // Ici on va ouvrir le GUI d'échange pour les deux joueurs
        TradeHandler.openTrade(sender, acceptor);
    }
}
