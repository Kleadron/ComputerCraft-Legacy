/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import dan200.shared.TileEntityDiskDrive;
import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemMap;
import net.minecraft.src.ItemMapBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;

public class ContainerDiskDrive extends Container {
    private TileEntityDiskDrive diskDrive;

    public ContainerDiskDrive(IInventory iinventory, TileEntityDiskDrive tileentitydiskdrive) {
        this.diskDrive = tileentitydiskdrive;
        
        //disk drive slot
        this.addSlot(new Slot((IInventory)this.diskDrive, 0, 80, 35));
        
        //inventory
        for (int j = 0; j < 3; ++j) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(iinventory, i1 + j * 9 + 9, 8 + i1 * 18, 84 + j * 18));
            }
        }
        
        //hotbar
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(iinventory, k, 8 + k * 18, 142));
        }
    }

	@Override
    public boolean isUsableByPlayer(EntityPlayer entityplayer) {
        return this.diskDrive.canInteractWith(entityplayer);
    }
	
    public ItemStack getStackInSlot(int i) {
    	ItemStack itemstack = null;
    	Slot slot = (Slot)this.slots.get(i);
        if (slot != null && slot.getHasStack()) {
        	ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy(); 
            //can't figure out what these methods were supposed to be, one matching method but doesn't return a boolean
            //if (i == 0 ? !this.func_28125_a(itemstack1, 1, 37, true) : !this.func_28125_a(itemstack1, 0, 1, false)) {
            //    return null;
            //}
            if(i == 0)
            {
            	this.func_28125_a(itemstack1, 0, 1, false);
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize != itemstack.stackSize) {
                slot.onPickupFromSlot(itemstack1);
            } else {
                return null;
            }
        }
        return itemstack;
    }
}
