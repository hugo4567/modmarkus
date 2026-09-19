package com.example.modmarkus.proxy;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class ClientProxy {
    
    private static ResourceLocation imageTexture;
    private boolean textureLoaded = false;
    
    public ClientProxy() {
        imageTexture = new ResourceLocation("modmarkus", "textures/image.png");
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void renderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        
        if (mc.player == null) return;
        
        float partialTicks = event.getPartialTicks();
        
        // Position de l'image (flottante devant le joueur)
        double x = mc.player.posX + 5;
        double y = mc.player.posY + 2;
        double z = mc.player.posZ;
        
        // Matrice sauvegarde
        GL11.glPushMatrix();
        
        // Traduire et préparer
        GL11.glTranslated(x - mc.getRenderManager().viewerPosX, 
                         y - mc.getRenderManager().viewerPosY,
                         z - mc.getRenderManager().viewerPosZ);
        
        // Faire face au joueur
        GL11.glRotatef(-mc.player.rotationYaw, 0.0F, 1.0F, 0.0F);
        
        // Lier la texture
        mc.getTextureManager().bindTexture(new ResourceLocation("modmarkus", "textures/image.png"));
        
        // Paramètres OpenGL
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        // Dessiner un quad simple
        GL11.glBegin(GL11.GL_QUADS);
        
        // Couleur blanche
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        float size = 4.0F;
        
        // Quad avec texture
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(-size, -size, 0.0F);
        
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(size, -size, 0.0F);
        
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(size, size, 0.0F);
        
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(-size, size, 0.0F);
        
        GL11.glEnd();
        
        // Restaurer les paramètres
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        GL11.glPopMatrix();
    }
}
