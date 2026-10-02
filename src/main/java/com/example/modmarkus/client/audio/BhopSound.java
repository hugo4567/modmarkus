package com.example.modmarkus.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BhopSound extends MovingSound {
    private final EntityPlayer player;

    public BhopSound(EntityPlayer player, SoundEvent soundIn) {
        super(soundIn, SoundCategory.MASTER);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.attenuationType = AttenuationType.NONE;
        this.update();
    }

    @Override
    public void update() {
        if (this.player == null || this.player.isDead) {
            this.donePlaying = true;
        } else {
            this.xPosF = (float) this.player.posX;
            this.yPosF = (float) this.player.posY;
            this.zPosF = (float) this.player.posZ;
        }
    }

    public void stopPlaying() {
        this.donePlaying = true;
    }
}
