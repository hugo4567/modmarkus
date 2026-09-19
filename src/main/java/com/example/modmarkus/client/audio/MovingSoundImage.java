package com.example.modmarkus.client.audio;

import com.example.modmarkus.entity.EntityImage;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MovingSoundImage extends MovingSound {
    private final EntityImage entityImage;

    public MovingSoundImage(EntityImage entityImage, SoundEvent soundIn) {
        super(soundIn, SoundCategory.MASTER);
        this.entityImage = entityImage;
        this.repeat = false;
        this.repeatDelay = 0;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.update();
    }

    @Override
    public void update() {
        if (this.entityImage.isDead) {
            this.donePlaying = true;
        } else {
            this.xPosF = (float) this.entityImage.posX;
            this.yPosF = (float) this.entityImage.posY;
            this.zPosF = (float) this.entityImage.posZ;
        }
    }
}
