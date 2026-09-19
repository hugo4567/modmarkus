package com.example.modmarkus.network;

import com.example.modmarkus.ModMarkus;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModMarkus.MODID);

    public static void init() {
        INSTANCE.registerMessage(MessageMissedCall.Handler.class, MessageMissedCall.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageHangUp.Handler.class, MessageHangUp.class, 1, Side.SERVER);
        INSTANCE.registerMessage(MessageSlipEffect.Handler.class, MessageSlipEffect.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(com.example.modmarkus.trade.MessageOpenTrade.Handler.class, com.example.modmarkus.trade.MessageOpenTrade.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(com.example.modmarkus.trade.MessageTradeReady.Handler.class, com.example.modmarkus.trade.MessageTradeReady.class, 4, Side.SERVER);
    }
}
