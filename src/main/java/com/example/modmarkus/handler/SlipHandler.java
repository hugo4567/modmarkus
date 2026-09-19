package com.example.modmarkus.handler;

import com.example.modmarkus.network.MessageSlipEffect;
import com.example.modmarkus.network.NetworkHandler;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.lang.reflect.Method;
import java.util.Random;

public class SlipHandler {
    private final Random random = new Random();
    private int slipTicks = 0;
    private static int clientSlipTicks = 0;
    private static final int SLIP_DURATION = 10; // 0.5 seconde de glissade (10 ticks)
    
    private static final Method SET_FLAG_METHOD = ReflectionHelper.findMethod(
        net.minecraft.entity.Entity.class, "setFlag", "func_70052_a", int.class, boolean.class);

    private void setEntityFlag(net.minecraft.entity.Entity entity, int flag, boolean value) {
        try {
            SET_FLAG_METHOD.invoke(entity, flag, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public static void setClientSlipTicks(int ticks) {
        clientSlipTicks = ticks;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        
        EntityPlayer player = event.player;

        if (player.world.isRemote) {
            updateClientSlip(player);
        } else {
            updateServerSlip(player);
        }
    }

    private void updateServerSlip(EntityPlayer player) {
        if (player.isSprinting()) {
            if (player.ticksExisted % 20 == 0) {
                if (random.nextInt(3600) == 0 && slipTicks <= 0) {
                    startSlipping(player);
                }
            }
        }

        if (slipTicks > 0) {
            slipTicks--;
            setEntityFlag(player, 7, true);
            
            // On plaque le joueur au sol
            player.motionY = -0.3;
            player.velocityChanged = true;
            
            if (slipTicks <= 0) {
                setEntityFlag(player, 7, false);
            }
        }
    }

    private void updateClientSlip(EntityPlayer player) {
        if (clientSlipTicks > 0) {
            clientSlipTicks--;
            // Sur le client, on force la hauteur des yeux
            player.eyeHeight = 0.1F;
            
            // On peut aussi incliner un peu la caméra en modifiant la rotation si on voulait, 
            // mais eyeHeight 0.1 c'est déjà radical.
            
            if (clientSlipTicks <= 0) {
                player.eyeHeight = player.getDefaultEyeHeight();
            }
        }
    }

    private void startSlipping(EntityPlayer player) {
        slipTicks = SLIP_DURATION;
        player.sendMessage(new TextComponentTranslation("chat.modmarkus.slipped"));
        
        // Ajouter lenteur et jump boost 2
        player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, SLIP_DURATION, 1));
        player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, SLIP_DURATION, 1));

        // Jouer le son de glissade
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundRegistry.SLIP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        
        // On booste la vitesse mais on la rend incontrôlable (le joueur glisse)
        player.motionX *= 2.5;
        player.motionZ *= 2.5;
        player.motionY = -0.5;
        
        // Envoyer le paquet au client
        if (player instanceof EntityPlayerMP) {
            NetworkHandler.INSTANCE.sendTo(new MessageSlipEffect(SLIP_DURATION), (EntityPlayerMP) player);
        }
        
        player.velocityChanged = true;
    }
}
