package com.example.modmarkus.proxy;

import com.example.modmarkus.entity.EntityImage;
import com.example.modmarkus.render.RenderEntityImage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxyNew extends CommonProxy {
    
    @Override
    public void registerRenders() {
        System.out.println("[ModMarkus] ClientProxyNew.registerRenders()");
        
        try {
            Minecraft mc = Minecraft.getMinecraft();
            RenderEntityImage renderer = new RenderEntityImage(mc.getRenderManager());
            mc.getRenderManager().entityRenderMap.put(EntityImage.class, renderer);
            
            // Renderer pour le punisher
            // Utiliser RenderingRegistry au lieu de manipuler directement la map
            net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler(
                com.example.modmarkus.entity.EntityPunisher.class,
                new net.minecraft.client.renderer.entity.RenderZombie(mc.getRenderManager())
            );
            
            System.out.println("[ModMarkus] ✓ Renderers enregistrés avec succès!");
        } catch (Exception e) {
            System.out.println("[ModMarkus] ERREUR enregistrement renderer: " + e);
            e.printStackTrace();
        }
    }

    @Override
    public void openIncomingCallGui() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().displayGuiScreen(new com.example.modmarkus.gui.GuiIncomingCall());
        });
    }
}
