package com.example.modmarkus.entity;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityPunisher extends EntityZombie {

    private int ticksToDespawn = -1;

    public EntityPunisher(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.8F); // Taille d'un humain
        this.setCustomNameTag("JOHN");
        this.setAlwaysRenderNameTag(true);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    public boolean isChild() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote) {
            if (ticksToDespawn > 0) {
                ticksToDespawn--;
                if (ticksToDespawn == 0) {
                    this.setDead();
                }
            } else if (ticksToDespawn == -1) {
                // Chercher le joueur le plus proche pour le frapper
                EntityPlayer player = this.world.getNearestAttackablePlayer(this, 3.0D, 3.0D);
                if (player != null) {
                    float currentHealth = player.getHealth();
                    float damage = currentHealth / 2.0F;
                    
                    // Frapper le joueur
                    player.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
                    
                    // Animation de bras
                    this.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
                    
                    // Programmer le despawn (après 10 ticks pour qu'on le voit un peu)
                    ticksToDespawn = 10;
                } else {
                    // Si pas de joueur à proximité après 100 ticks, despawn quand même
                    if (this.ticksExisted > 100) {
                        this.setDead();
                    }
                }
            }
        }
    }
}
