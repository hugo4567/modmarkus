package com.example.modmarkus.proxy;

public class CommonProxy {
    
    public void registerRenders() {
        System.out.println("[ModMarkus] CommonProxy.registerRenders() - côté serveur");
    }

    public void initClientHandlers() {
        // Rien côté serveur
    }

    public void openIncomingCallGui() {
        // Ne rien faire côté serveur
    }
}
