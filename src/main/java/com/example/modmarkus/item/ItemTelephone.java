package com.example.modmarkus.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemTelephone extends Item {
    
    public ItemTelephone() {
        setTranslationKey("telephone");
        setRegistryName("telephone");
        setCreativeTab(CreativeTabs.TOOLS);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        
        if (!worldIn.isRemote) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("Messages", Constants.NBT.TAG_LIST)) {
                NBTTagList msgList = nbt.getTagList("Messages", Constants.NBT.TAG_STRING);
                int count = msgList.tagCount();
                
                // État de lecture
                int currentIdx = nbt.getInteger("ReadingIndex");
                boolean isReading = nbt.getBoolean("IsReading");
                
                if (!isReading) {
                    // Premier clic: Afficher la liste
                    playerIn.sendMessage(new TextComponentString("§b--- Messagerie (" + count + ") ---"));
                    for (int i = 0; i < count; i++) {
                        String msg = msgList.getStringTagAt(i);
                        String preview = msg.length() > 20 ? msg.substring(0, 17) + "..." : msg;
                        playerIn.sendMessage(new TextComponentString("§7[" + i + "] " + preview));
                    }
                    playerIn.sendMessage(new TextComponentString("§eCliquez à nouveau pour lire le message [" + currentIdx + "]"));
                    nbt.setBoolean("IsReading", true);
                } else {
                    // Second clic: Lire le message actuel
                    if (currentIdx < count) {
                        String fullMsg = msgList.getStringTagAt(currentIdx);
                        playerIn.sendMessage(new TextComponentString("§fMessage [" + currentIdx + "]:"));
                        playerIn.sendMessage(new TextComponentString("§a" + fullMsg));
                        
                        // Passer au message suivant pour le prochain cycle
                        int nextIdx = (currentIdx + 1) % count;
                        nbt.setInteger("ReadingIndex", nextIdx);
                    }
                    nbt.setBoolean("IsReading", false);
                }
            } else {
                playerIn.sendMessage(new TextComponentTranslation("chat.modmarkus.no_missed_calls"));
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}
