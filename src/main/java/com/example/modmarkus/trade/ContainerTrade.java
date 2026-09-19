package com.example.modmarkus.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTrade extends Container {

    private final EntityPlayer player1;
    private final EntityPlayer player2;
    private final IInventory tradeInventory1; // Items de player1
    private final IInventory tradeInventory2; // Items de player2

    private boolean ready1 = false;
    private boolean ready2 = false;

    public ContainerTrade(EntityPlayer p1, EntityPlayer p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.tradeInventory1 = new InventoryBasic("Trade1", false, 4);
        this.tradeInventory2 = new InventoryBasic("Trade2", false, 4);

        // Slots de Player 1 (Gauche) - Index 0-3
        for (int i = 0; i < 4; i++) {
            this.addSlotToContainer(new SlotTrade(tradeInventory1, i, 44 + i * 18, 20, p1));
        }

        // Slots de Player 2 (Droite) - Index 4-7
        for (int i = 0; i < 4; i++) {
            this.addSlotToContainer(new SlotTrade(tradeInventory2, i, 44 + i * 18, 45, p2));
        }

        // Inventaire du joueur qui ouvre (Player 1 dans la vue de Player 1, Player 2 dans la vue de Player 2)
        // Note: On va tricher un peu pour que chaque joueur voit ses propres items d'inventaire en bas.
        // Mais pour un Container générique, on met l'inventaire de p1.
        // En réalité, on ouvrira deux instances différentes de ContainerTrade si on veut être propre.
    }

    public void setupPlayerInventory(EntityPlayer viewingPlayer) {
        // Ajouter l'inventaire principal du joueur qui regarde
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(viewingPlayer.inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(viewingPlayer.inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return playerIn == player1 || playerIn == player2;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.world.isRemote) {
            // Rendre les items
            returnItems(player1, tradeInventory1);
            returnItems(player2, tradeInventory2);
            
            // Fermer pour l'autre aussi
            EntityPlayer other = (playerIn == player1) ? player2 : player1;
            if (other.openContainer == this) {
                other.closeScreen();
            }
        }
    }

    private void returnItems(EntityPlayer player, IInventory inv) {
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.removeStackFromSlot(i);
            if (!stack.isEmpty()) {
                if (!player.inventory.addItemStackToInventory(stack)) {
                    player.dropItem(stack, false);
                }
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 8) { // Slots de trade
                if (!this.mergeItemStack(itemstack1, 8, 44, true)) {
                    return ItemStack.EMPTY;
                }
            } else { // Inventaire du joueur
                // On essaie de mettre dans les 4 slots du joueur concerné
                int start = (playerIn == player1) ? 0 : 4;
                if (!this.mergeItemStack(itemstack1, start, start + 4, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    public void setReady(EntityPlayer player, boolean ready) {
        if (player == player1) ready1 = ready;
        if (player == player2) ready2 = ready;

        if (ready1 && ready2) {
            executeTrade();
        }
    }

    private void executeTrade() {
        // Échanger les items
        exchange(player1, tradeInventory2);
        exchange(player2, tradeInventory1);
        
        // Vider pour éviter le retour d'items dans onContainerClosed
        tradeInventory1.clear();
        tradeInventory2.clear();

        player1.sendMessage(new net.minecraft.util.text.TextComponentString("§aÉchange réussi !"));
        player2.sendMessage(new net.minecraft.util.text.TextComponentString("§aÉchange réussi !"));

        player1.closeScreen();
        player2.closeScreen();
    }

    private void exchange(EntityPlayer toPlayer, IInventory fromInv) {
        for (int i = 0; i < fromInv.getSizeInventory(); i++) {
            ItemStack stack = fromInv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (!toPlayer.inventory.addItemStackToInventory(stack)) {
                    toPlayer.dropItem(stack, false);
                }
            }
        }
    }

    public boolean isReady1() { return ready1; }
    public boolean isReady2() { return ready2; }

    public static class SlotTrade extends Slot {
        private final EntityPlayer owner;
        public SlotTrade(IInventory inventoryIn, int index, int xPosition, int yPosition, EntityPlayer owner) {
            super(inventoryIn, index, xPosition, yPosition);
            this.owner = owner;
        }

        @Override
        public boolean canTakeStack(EntityPlayer playerIn) {
            return playerIn == owner;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            // On pourrait restreindre ici, mais on laisse tout passer
            return true;
        }
    }
}
