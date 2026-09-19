package com.example.modmarkus.command;

import com.example.modmarkus.item.ModItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class CommandMsg2 extends CommandBase {

    @Override
    public String getName() {
        return "msg2";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/msg2 <joueur> <message>";
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
        if (args.length < 2) {
            sender.sendMessage(new TextComponentString("§cUsage: /msg2 <joueur> <message>"));
            return;
        }

        String targetName = args[0];
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();

        EntityPlayer target = server.getPlayerList().getPlayerByUsername(targetName);
        if (target == null) {
            sender.sendMessage(new TextComponentString("§cJoueur introuvable: " + targetName));
            return;
        }

        // Chercher le téléphone dans l'inventaire du destinataire
        ItemStack phone = ItemStack.EMPTY;
        for (int i = 0; i < target.inventory.getSizeInventory(); i++) {
            ItemStack stack = target.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == ModItems.TELEPHONE) {
                phone = stack;
                break;
            }
        }

        if (phone.isEmpty()) {
            sender.sendMessage(new TextComponentString("§c" + targetName + " n'a pas son téléphone sur lui !"));
            return;
        }

        // Ajouter le message au NBT du téléphone
        if (!phone.hasTagCompound()) {
            phone.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = phone.getTagCompound();
        NBTTagList msgList;
        if (nbt.hasKey("Messages", Constants.NBT.TAG_LIST)) {
            msgList = nbt.getTagList("Messages", Constants.NBT.TAG_STRING);
        } else {
            msgList = new NBTTagList();
        }

        msgList.appendTag(new NBTTagString("De " + sender.getName() + ": " + message));
        nbt.setTag("Messages", msgList);
        
        sender.sendMessage(new TextComponentString("§aMessage envoyé à " + targetName));
        target.sendMessage(new TextComponentString("§e[Portable] Vous avez reçu un nouveau message !"));
    }
}
