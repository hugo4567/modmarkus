package com.example.modmarkus.render;

import com.example.modmarkus.entity.EntityImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;

public class RenderEntityImage extends Render<EntityImage> {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation("modmarkus", "textures/image.png");
    
    public RenderEntityImage(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 2.0F;
    }
    
    @Override
    public void doRender(EntityImage entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Sauvegarder l'état OpenGL
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        // Translater vers la position de l'entité
        GlStateManager.translate(x, y + 1.5, z);
        
        // Billboarding : Faire face à la caméra
        // On aligne la rotation du quad sur celle du joueur/caméra pour qu'il soit toujours de face
        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        
        // Charger la texture
        this.bindTexture(TEXTURE);
        
        // Dimensions du plan (quad plat)
        float size = 3.0F;
        float halfSize = size / 2.0F;
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        // Utilisation du Tessellator pour un rendu plus propre et "Minecraft-like"
        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
        net.minecraft.client.renderer.BufferBuilder bufferbuilder = tessellator.getBuffer();
        
        // Dessiner le quad (objet 3D plat)
        bufferbuilder.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(-halfSize, -halfSize, 0.0).tex(0.0, 1.0).endVertex();
        bufferbuilder.pos(halfSize, -halfSize, 0.0).tex(1.0, 1.0).endVertex();
        bufferbuilder.pos(halfSize, halfSize, 0.0).tex(1.0, 0.0).endVertex();
        bufferbuilder.pos(-halfSize, halfSize, 0.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        
        // Restaurer l'état OpenGL
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityImage entity) {
        return TEXTURE;
    }
    
    @Override
    public boolean shouldRender(EntityImage entity, net.minecraft.client.renderer.culling.ICamera camera, double camX, double camY, double camZ) {
        System.out.println("[ModMarkus] shouldRender() appelé - entity à (" + entity.posX + ", " + entity.posY + ", " + entity.posZ + ")");
        return true;
    }
}
