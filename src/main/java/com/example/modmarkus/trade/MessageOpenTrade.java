package com.example.modmarkus.trade;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class MessageOpenTrade implements IMessage {

    private UUID playerUUID;

    public MessageOpenTrade() {}

    public MessageOpenTrade(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerUUID = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(playerUUID.getMostSignificantBits());
        buf.writeLong(playerUUID.getLeastSignificantBits());
    }

    public static class Handler implements IMessageHandler<MessageOpenTrade, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(MessageOpenTrade message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                // Le serveur a déjà configuré openContainer
                // On doit juste afficher le GUI
                Minecraft.getMinecraft().displayGuiScreen(new GuiTrade(player.openContainer));
            });
            return null;
        }
    }
}
