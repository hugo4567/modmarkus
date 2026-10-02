package com.example.modmarkus.handler;

import com.example.modmarkus.client.audio.BhopSound;
import com.example.modmarkus.network.MessageBhopSync;
import com.example.modmarkus.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Locale;

@SideOnly(Side.CLIENT)
public class BhopClientHandler {

    private static int bhopStreak = 0;
    private static double targetAirSpeed = 0.0;
    private static double lastAirSpeed = 0.0;

    private static int spaceHeldTicks = -1;
    private static int ticksSinceSpacePressed = 999;
    private static int groundTicks = 0;
    private static int airTicks = 0;
    private static boolean wasOnGround = true;
    private static int lastBhopTick = -1;

    private static BhopSound currentSound = null;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (event.player != mc.player || mc.player == null) return;
        EntityPlayerSP player = mc.player;

        // Conditions invalidantes pour le b-hop
        boolean inCobweb = player.world.getBlockState(new BlockPos(player.posX, player.getEntityBoundingBox().minY, player.posZ)).getBlock() == Blocks.WEB;
        if (player.isDead || player.capabilities.isFlying || player.isSpectator()
                || player.isRiding() || player.isOnLadder() || player.isInWater()
                || player.isInLava() || inCobweb || player.isSneaking()) {
            if (bhopStreak > 0) {
                resetBhop(player);
            }
            return;
        }

        // Collision frontale contre un obstacle
        if (player.collidedHorizontally && bhopStreak > 0) {
            resetBhop(player);
            return;
        }

        // Si un GUI (menu pause, inventaire, chat) est ouvert, réinitialiser
        if (mc.currentScreen != null && bhopStreak > 0) {
            resetBhop(player);
            return;
        }

        // Suivi de la touche Espace
        boolean jumpKeyDown = mc.gameSettings.keyBindJump.isKeyDown();
        if (jumpKeyDown) {
            if (spaceHeldTicks < 0) {
                spaceHeldTicks = 0;
                ticksSinceSpacePressed = 0;
            } else {
                spaceHeldTicks++;
                ticksSinceSpacePressed++;
            }
        } else {
            spaceHeldTicks = -1;
            ticksSinceSpacePressed++;
        }

        // Suivi du sol / air
        if (player.onGround) {
            groundTicks++;
            airTicks = 0;

            // Si le joueur reste au sol plus de 2 ticks (100ms) sans sauter, streak brisé
            if (groundTicks > 2 && bhopStreak > 0) {
                resetBhop(player);
            }

            // Saut bufferisé (si le joueur a appuyé sur espace juste avant de toucher le sol)
            if (groundTicks <= 1 && ticksSinceSpacePressed <= 2 && spaceHeldTicks <= 3) {
                tryExecuteBhop(player);
            }
        } else {
            airTicks++;
            groundTicks = 0;

            // Enregistrement de la vitesse en vol
            double horizSpeed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
            if (horizSpeed > 0.05) {
                lastAirSpeed = Math.max(horizSpeed, targetAirSpeed);
            }
        }

        wasOnGround = player.onGround;
    }

    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.getEntityLiving() != mc.player || mc.player == null) return;
        EntityPlayerSP player = mc.player;

        tryExecuteBhop(player);
    }

    private static void tryExecuteBhop(EntityPlayerSP player) {
        if (lastBhopTick == player.ticksExisted) return;

        double currentHorizSpeed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
        double effectiveSpeed = Math.max(lastAirSpeed, currentHorizSpeed);

        // Si le joueur est immobile au départ, c'est un saut standard
        if (effectiveSpeed < 0.1 && bhopStreak == 0) {
            return;
        }

        // Condition de timing parfait :
        // 1. Touche espace appuyée récemment (<= 3 ticks = 150ms avant ou à l'atterrissage)
        // 2. Touche espace non maintenue en continu (> 4 ticks = pas de b-hop si on garde espace enfoncé)
        // 3. Joueur sur le sol depuis maximum 2 ticks
        boolean goodTiming = (ticksSinceSpacePressed <= 3) && (spaceHeldTicks <= 4) && (groundTicks <= 2);

        if (goodTiming) {
            lastBhopTick = player.ticksExisted;
            bhopStreak++;

            // Vitesse de base
            double baseSpeed = Math.max(lastAirSpeed, currentHorizSpeed);
            if (bhopStreak == 1) {
                baseSpeed = Math.max(baseSpeed, 0.285); // Vitesse sprint Minecraft vanilla de référence
            }

            // Accélération infinie mais progressive à chaque coup
            double newSpeed = baseSpeed * 1.05 + 0.035;

            // Calcul de l'angle de visée / déplacement
            float moveYaw = getMoveYaw(player);
            double yawRad = Math.toRadians(moveYaw);

            // Application de la vitesse boostée
            player.motionX = -Math.sin(yawRad) * newSpeed;
            player.motionZ = Math.cos(yawRad) * newSpeed;
            player.motionY = 0.42F + (player.isPotionActive(MobEffects.JUMP_BOOST)
                    ? (player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F : 0.0F);

            player.onGround = false;
            player.isAirBorne = true;
            player.velocityChanged = true;
            player.fallDistance = 0.0F;

            targetAirSpeed = newSpeed;
            lastAirSpeed = newSpeed;

            // Effet de vitesse (potion Speed) dont la puissance augmente avec le streak
            int amplifier = Math.min(bhopStreak / 2, 6);
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, amplifier, true, false));

            // Synchronisation avec le serveur
            NetworkHandler.INSTANCE.sendToServer(new MessageBhopSync(bhopStreak, (float) newSpeed));

            // Déclenchement du son à partir de 5 sauts corrects consécutifs
            if (bhopStreak >= 5) {
                startBhopSound(player);
            }

            // Message dans la barre d'action (au-dessus de la barre d'inventaire)
            double speedMps = newSpeed * 20.0;
            if (bhopStreak < 5) {
                player.sendStatusMessage(new TextComponentString(
                        String.format(Locale.US, "§b[B-Hop] §a%dx §7(%.1f m/s)", bhopStreak, speedMps)), true);
            } else {
                player.sendStatusMessage(new TextComponentString(
                        String.format(Locale.US, "§6§l[B-Hop] §e§l%dx §a(%.1f m/s) §d♫ RED SUN ♫", bhopStreak, speedMps)), true);
            }
        } else {
            // Timing raté : brise l'enchaînement en cours
            if (bhopStreak > 0) {
                resetBhop(player);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTickEnd(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (event.player != mc.player || mc.player == null) return;
        EntityPlayerSP player = mc.player;

        // En vol pendant un b-hop : conservation du momentum et air strafe style CS:GO
        if (bhopStreak > 0 && !player.onGround) {
            double currentSpeed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
            if (currentSpeed > 0.001) {
                // Compense la friction de l'air Minecraft (0.91) pour un glide CS:GO fluide
                double speed = Math.max(currentSpeed, targetAirSpeed * 0.998);

                float forward = player.movementInput.moveForward;
                float strafe = player.movementInput.moveStrafe;

                if (forward != 0.0F || strafe != 0.0F) {
                    float targetYaw = getMoveYaw(player);
                    double currentYaw = Math.toDegrees(Math.atan2(-player.motionX, player.motionZ));
                    double diff = MathHelper.wrapDegrees(targetYaw - currentYaw);

                    // Virage aérien fluide (jusqu'à 7 degrés par tick)
                    double maxTurn = 7.0;
                    if (diff > maxTurn) diff = maxTurn;
                    if (diff < -maxTurn) diff = -maxTurn;

                    double newYawRad = Math.toRadians(currentYaw + diff);
                    player.motionX = -Math.sin(newYawRad) * speed;
                    player.motionZ = Math.cos(newYawRad) * speed;
                } else {
                    double currentYawRad = Math.atan2(-player.motionX, player.motionZ);
                    player.motionX = -Math.sin(currentYawRad) * speed;
                    player.motionZ = Math.cos(currentYawRad) * speed;
                }

                targetAirSpeed = speed;
                lastAirSpeed = speed;
                player.fallDistance = 0.0F;
            }
        }
    }

    private static float getMoveYaw(EntityPlayerSP player) {
        float forward = player.movementInput.moveForward;
        float strafe = player.movementInput.moveStrafe;
        float yaw = player.rotationYaw;

        if (forward != 0.0F || strafe != 0.0F) {
            if (forward > 0.0F) {
                if (strafe > 0.0F) yaw -= 45.0F;
                else if (strafe < 0.0F) yaw += 45.0F;
            } else if (forward < 0.0F) {
                if (strafe > 0.0F) yaw -= 135.0F;
                else if (strafe < 0.0F) yaw += 135.0F;
                else yaw += 180.0F;
            } else {
                if (strafe > 0.0F) yaw -= 90.0F;
                else if (strafe < 0.0F) yaw += 90.0F;
            }
        }
        return yaw;
    }

    public static void resetBhop(EntityPlayerSP player) {
        if (bhopStreak > 0) {
            int previousStreak = bhopStreak;
            bhopStreak = 0;
            targetAirSpeed = 0.0;
            lastAirSpeed = 0.0;

            stopBhopSound();

            if (player != null) {
                player.removePotionEffect(MobEffects.SPEED);
                NetworkHandler.INSTANCE.sendToServer(new MessageBhopSync(0, 0.0F));
                if (previousStreak >= 2) {
                    player.sendStatusMessage(new TextComponentString(
                            String.format(Locale.US, "§c[B-Hop] Terminé (%d sauts)", previousStreak)), true);
                }
            }
        }
    }

    private static void startBhopSound(EntityPlayerSP player) {
        Minecraft mc = Minecraft.getMinecraft();
        if (currentSound == null || currentSound.isDonePlaying() || !mc.getSoundHandler().isSoundPlaying(currentSound)) {
            currentSound = new BhopSound(player, SoundRegistry.REDSUN);
            mc.getSoundHandler().playSound(currentSound);
        }
    }

    public static void stopBhopSound() {
        if (currentSound != null) {
            currentSound.stopPlaying();
            Minecraft.getMinecraft().getSoundHandler().stopSound(currentSound);
            currentSound = null;
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        stopBhopSound();
        bhopStreak = 0;
        targetAirSpeed = 0.0;
        lastAirSpeed = 0.0;
    }
}
