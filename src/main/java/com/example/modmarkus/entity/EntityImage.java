package com.example.modmarkus.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.MobEffects;

public class EntityImage extends Entity {
    
    private static final net.minecraft.network.datasync.DataParameter<Integer> PHASE = net.minecraft.network.datasync.EntityDataManager.createKey(EntityImage.class, net.minecraft.network.datasync.DataSerializers.VARINT);
    
    public enum PhaseType { HEAL, FIRE }
    
    private int lifetime = 400; // 20 secondes (400 ticks) = 10s + 10s
    private int phaseStartTick = 0;
    private boolean hasSoundPlayed = false;
    private boolean hasFired = false;
    
    public EntityImage(World world) {
        super(world);
        this.setSize(3.0F, 3.0F);
        if (!world.isRemote) {
            this.setPhaseType(Math.random() < 0.5 ? PhaseType.HEAL : PhaseType.FIRE);
        }
        this.phaseStartTick = 0;
        
        // Activer noClip
        this.noClip = true;
    }
    
    public EntityImage(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
        if (!world.isRemote) {
            System.out.println("[ModMarkus] EntityImage créée côté SERVER à (" + x + ", " + y + ", " + z + ") - Phase: " + getPhaseType());
        }
    }
    
    public PhaseType getPhaseType() {
        return PhaseType.values()[this.dataManager.get(PHASE)];
    }
    
    public void setPhaseType(PhaseType type) {
        this.dataManager.set(PHASE, type.ordinal());
    }
    
    public boolean hasSoundPlayed() {
        return hasSoundPlayed;
    }
    
    public void setHasSoundPlayed(boolean played) {
        this.hasSoundPlayed = played;
    }
    
    @Override
    protected void entityInit() {
        this.dataManager.register(PHASE, 0);
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        PhaseType phaseType = getPhaseType();
        
        // Jouer le son une seule fois côté CLIENT
        if (!hasSoundPlayed && this.world.isRemote) {
            hasSoundPlayed = true;
            try {
                String musicName = phaseType == PhaseType.HEAL ? "redsun" : "banjo";
                net.minecraft.util.SoundEvent soundEvent = net.minecraft.util.SoundEvent.REGISTRY.getObject(
                    new net.minecraft.util.ResourceLocation("modmarkus", musicName));
                if (soundEvent != null) {
                    // Utilisation d'un son qui suit l'entité
                    com.example.modmarkus.client.audio.MovingSoundImage movingSound = 
                        new com.example.modmarkus.client.audio.MovingSoundImage(this, soundEvent);
                    net.minecraft.client.Minecraft.getMinecraft().getSoundHandler().playSound(movingSound);
                    System.out.println("[ModMarkus] ✓ Son mobile joué: " + musicName + " (phase: " + phaseType + ")");
                } else {
                    System.out.println("[ModMarkus] ✗ Sound event NULL: " + musicName);
                }
            } catch (Exception e) {
                System.out.println("[ModMarkus] ✗ Erreur son: " + e);
            }
        }
        
        // Déplacement vers le joueur (côté serveur)
        if (!this.world.isRemote) {
            // Trouver le joueur le plus proche
            EntityPlayer player = null;
            double distance = Double.MAX_VALUE;
            
            for (EntityPlayer p : this.world.playerEntities) {
                double d = this.getDistanceSq(p);
                if (d < distance && d < 10000) { // 100 blocs
                    distance = d;
                    player = p;
                }
            }
            
            if (player != null) {
                // Direction vers le joueur
                double dx = player.posX - this.posX;
                double dy = player.posY - this.posY;
                double dz = player.posZ - this.posZ;
                double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                
                if (dist > 1.0) { // Au moins 1 bloc de distance
                    double speed = 0.23; // Vitesse de mouvement (un peu moins qu'un joueur qui court ~0.28)
                    
                    // Calcul du mouvement horizontal
                    double newX = this.posX + (dx / dist) * speed;
                    double newZ = this.posZ + (dz / dist) * speed;
                    
                    // Gestion de la hauteur (Y) pour éviter de s'enfoncer
                    // On cherche le bloc solide le plus haut à la nouvelle position
                    net.minecraft.util.math.BlockPos groundPos = this.world.getTopSolidOrLiquidBlock(new net.minecraft.util.math.BlockPos(newX, this.posY + 2, newZ));
                    double groundY = groundPos.getY();
                    
                    // La hauteur cible est 1.5 blocs au dessus du sol
                    double targetY = groundY + 1.5;
                    
                    // Si on suit le joueur en hauteur (ex: il est sur une colline), on ajuste doucement
                    double newY;
                    if (this.posY < targetY) {
                        newY = Math.min(targetY, this.posY + 0.1); // Monte doucement
                    } else if (this.posY > targetY + 2.0) {
                        newY = Math.max(targetY, this.posY - 0.1); // Descend doucement
                    } else {
                        // Si le joueur est plus haut que nous mais qu'on est déjà au dessus du sol, 
                        // on peut essayer de monter un peu pour le rejoindre
                        double playerY = player.posY + 1.0;
                        if (playerY > this.posY) {
                            newY = Math.min(playerY, this.posY + 0.1);
                        } else {
                            newY = this.posY;
                        }
                    }
                    
                    this.setPosition(newX, newY, newZ);
                    System.out.println("[ModMarkus] Déplacement → (" + String.format("%.2f", newX) + ", " + String.format("%.2f", newY) + ", " + String.format("%.2f", newZ) + ") dist=" + String.format("%.2f", dist));
                }
                
                // Appliquer les effets
                int currentPhase = phaseStartTick / 200; // 0 = phase 1, 1+ = phase 2
                if (currentPhase == 0) { // Phase 1 (ticks 0-199)
                    if (phaseType == PhaseType.HEAL) {
                        // Nouveaux effets puissants pour la phase HEAL
                        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 40, 4)); // Regen V
                        player.addPotionEffect(new PotionEffect(MobEffects.SATURATION, 40, 1));    // Saturation
                        player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 2));  // Absorption III (donne 3 coeurs d'or)
                    }
                } else { // Phase 2 (ticks 200+)
                    if (phaseType == PhaseType.FIRE) {
                        if (dist < 2.0) { // Si l'image rattrape le joueur (distance < 2 blocs)
                            if (!hasFired) {
                                player.setFire(3);
                                player.attackEntityFrom(net.minecraft.util.DamageSource.MAGIC, 2.0F);
                                hasFired = true;
                                System.out.println("[ModMarkus] Phase FIRE: Feu appliqué une fois!");
                            } else {
                                // Après le feu, on applique Blindness
                                player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 60, 0));
                            }
                        }
                    }
                }
            }
        }
        
        if (lifetime == 400 || lifetime % 100 == 0) {
            System.out.println("[ModMarkus] EntityImage onUpdate - lifetime: " + lifetime + ", phase: " + phaseType);
        }
        
        lifetime--;
        phaseStartTick++;
        
        // Disparaître après 20 secondes
        if (lifetime <= 0) {
            this.setDead();
            if (!this.world.isRemote) {
                System.out.println("[ModMarkus] Entité disparue");
            }
        }
    }
    
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.lifetime = compound.getInteger("lifetime");
        this.hasSoundPlayed = compound.getBoolean("hasSoundPlayed");
        this.phaseStartTick = compound.getInteger("phaseStartTick");
        String phase = compound.getString("phaseType");
        try {
            this.setPhaseType(PhaseType.valueOf(phase));
        } catch (Exception e) {
            this.setPhaseType(PhaseType.HEAL);
        }
    }
    
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("lifetime", this.lifetime);
        compound.setBoolean("hasSoundPlayed", this.hasSoundPlayed);
        compound.setInteger("phaseStartTick", this.phaseStartTick);
        compound.setString("phaseType", getPhaseType().toString());
    }
    
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    

}
