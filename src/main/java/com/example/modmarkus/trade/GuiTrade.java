package com.example.modmarkus.trade;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import java.io.IOException;

public class GuiTrade extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png"); // On réutilise une texture de base
    private final ContainerTrade tradeContainer;
    private GuiButton readyButton;

    public GuiTrade(Container inventorySlotsIn) {
        super(inventorySlotsIn);
        this.tradeContainer = (ContainerTrade) inventorySlotsIn;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        int guiLeft = (this.width - this.xSize) / 2;
        int guiTop = (this.height - this.ySize) / 2;

        this.readyButton = new GuiButton(0, guiLeft + 70, guiTop + 65, 36, 16, "X");
        this.buttonList.add(readyButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            boolean currentReady = "V".equals(button.displayString);
            boolean newReady = !currentReady;
            button.displayString = newReady ? "V" : "X";
            
            // Envoyer au serveur
            com.example.modmarkus.network.NetworkHandler.INSTANCE.sendToServer(new MessageTradeReady(newReady));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        
        // On dessine juste une partie de la texture pour simuler un petit inventaire
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 80); // Haut
        this.drawTexturedModalRect(i, j + 80, 0, 134, this.xSize, 86); // Bas (Inventaire joueur)
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString("Échange", 8, 6, 4210752);
        
        // Afficher l'état de l'autre joueur (très simplifié)
        // Note: Idéalement on synchroniserait ready1/ready2 vers le client
        this.fontRenderer.drawString("Prêt ?", 40, 69, 4210752);
    }
}
