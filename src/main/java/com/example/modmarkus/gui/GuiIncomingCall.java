package com.example.modmarkus.gui;

import com.example.modmarkus.handler.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiIncomingCall extends GuiScreen {

    private ISound ringtone;
    private int ticksOpen = 0;
    private static final int TIMEOUT_TICKS = 10 * 20; // 10 secondes

    @Override
    public void updateScreen() {
        super.updateScreen();
        ticksOpen++;
        
        if (ticksOpen >= TIMEOUT_TICKS) {
            // Temps écoulé sans réponse
            com.example.modmarkus.network.NetworkHandler.INSTANCE.sendToServer(new com.example.modmarkus.network.MessageMissedCall());
            stopRingtone();
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        
        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 20;
        
        // Bouton Décrocher (Accept)
        this.buttonList.add(new GuiButton(0, this.width / 2 - buttonWidth - spacing / 2, this.height / 2 + 20, buttonWidth, buttonHeight, I18n.format("gui.modmarkus.accept")));
        
        // Bouton Raccrocher (Decline)
        this.buttonList.add(new GuiButton(1, this.width / 2 + spacing / 2, this.height / 2 + 20, buttonWidth, buttonHeight, I18n.format("gui.modmarkus.decline")));

        // Jouer la sonnerie si elle n'est pas déjà en cours
        if (ringtone == null) {
            try {
                ringtone = PositionedSoundRecord.getRecord(SoundRegistry.REDSUN, 1.0F, 1.0F);
                this.mc.getSoundHandler().playSound(ringtone);
            } catch (Exception e) {
                System.err.println("[ModMarkus] Erreur lors de la lecture de la sonnerie: " + e.getMessage());
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        String text = I18n.format("gui.modmarkus.incoming_call");
        this.drawCenteredString(this.fontRenderer, text, this.width / 2, this.height / 2 - 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            // Décrocher
            stopRingtone();
            this.mc.displayGuiScreen(null);
            // On pourrait ajouter d'autres actions ici plus tard
        } else if (button.id == 1) {
            // Raccrocher
            com.example.modmarkus.network.NetworkHandler.INSTANCE.sendToServer(new com.example.modmarkus.network.MessageHangUp());
            stopRingtone();
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void onGuiClosed() {
        stopRingtone();
    }

    private void stopRingtone() {
        if (ringtone != null) {
            this.mc.getSoundHandler().stopSound(ringtone);
            ringtone = null;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
