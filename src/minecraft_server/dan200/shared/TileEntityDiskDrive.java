/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import dan200.shared.IComputerCraftEntity;
import dan200.shared.ItemDisk;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemRecord;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_ComputerCraft;

public class TileEntityDiskDrive
extends TileEntity
implements IInventory,
IComputerCraftEntity
 {
    private ItemStack diskStack = null;
    private boolean m_firstFrame = true;
    private int m_clientDiskLight = 0;

    public void validate() {
        super.validate();
        if (mod_ComputerCraft.isMultiplayerClient()) {
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 5;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord};
            mod_ComputerCraft.sendToServer(packet);
        }
    }

    public Packet getDescriptionPacket() {
        return this.createDiskLightPacket();
    }

    @Override
	public int getSizeInventory() {
        return 1;
    }

    @Override
	public ItemStack getStackInSlot(int i) {
        return this.diskStack;
    }

    @Override
	public ItemStack decrStackSize(int i, int j) {
    	ItemStack disk = this.diskStack;
        this.setInventorySlotContents(i, null);
        return disk;
    }

    @Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
        boolean hadDisk = this.hasDisk();
        this.diskStack = itemstack;
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            mod_ComputerCraft.notifyBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_ComputerCraft.diskDrive.blockID);
            if (mod_ComputerCraft.isMultiplayerServer()) {
                int newDiskLight = 0;
                if (this.hasAnything()) {
                    int n = newDiskLight = this.hasDisk() ? 1 : 2;
                }
                if (newDiskLight != this.m_clientDiskLight) {
                    this.m_clientDiskLight = newDiskLight;
                    Packet230ModLoader diskLight = this.createDiskLightPacket();
                    mod_ComputerCraft.sendToAllPlayers(diskLight);
                }
            }
        }
    }

    @Override
	public String getInvName() {
        return "Disk Drive";
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        NBTTagCompound item = nbttagcompound.getCompoundTag("item");
        this.diskStack = new ItemStack(item);
        //System.out.println("Disk Drive Read: Item ID " + diskStack.itemID);
        //if the item id is 0 we have nothing in it
        if (diskStack.itemID == 0)
        	diskStack = null;
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        NBTTagCompound item = new NBTTagCompound();
        if (this.diskStack != null) {
        	//System.out.println("Disk Drive Save: Item ID " + diskStack.itemID);
            item = this.diskStack.writeToNBT(item);
        } else {
        	diskStack = new ItemStack(0, 0, 0);
        	//System.out.println("Disk Drive Save: Item ID " + diskStack.itemID);
        	item = this.diskStack.writeToNBT(item);
        	this.diskStack = null;
        }
      nbttagcompound.setCompoundTag("item", item);
    }

    @Override
	public int getInventoryStackLimit() {
        return 64;
    }

    @Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
        if (this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) {
            return false;
        }
        return entityplayer.getDistance((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
    }

    public boolean hasAnything() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            return this.diskStack != null;
        }
        return this.m_clientDiskLight > 0;
    }

    public boolean hasDisk() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            return this.getDataDiskID() >= 0 || this.getAudioDiscRecordName() != null;
        }
        return this.m_clientDiskLight == 1;
    }

    public void ejectContents(boolean destroyed) {
        if (mod_ComputerCraft.isMultiplayerClient()) {
            return;
        }
        if (this.diskStack != null) {
            ItemStack disks = this.diskStack;
            this.setInventorySlotContents(0, null);
            int xOff = 0;
            int zOff = 0;
            if (!destroyed) {
                int metaData = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
                switch (metaData) {
                    case 2: {
                        zOff = -1;
                        break;
                    }
                    case 3: {
                        zOff = 1;
                        break;
                    }
                    case 4: {
                        xOff = -1;
                        break;
                    }
                    case 5: {
                        xOff = 1;
                        break;
                    }
                }
            }
            double x = (double)this.xCoord + 0.5 + (double)xOff * 0.5;
            double y = (double)this.yCoord + 0.75;
            double z = (double)this.zCoord + 0.5 + (double)zOff * 0.5;
            EntityItem entityitem = new EntityItem(this.worldObj, x, y, z, disks);
            entityitem.motionX = (double)xOff * 0.15;
            entityitem.motionY = 0.0;
            entityitem.motionZ = (double)zOff * 0.15;
            this.worldObj.entityJoinedWorld((Entity)entityitem);
            if (!destroyed) {
                //this.worldObj.func_28106_e(1000, this.xCoord, this.yCoord, this.zCoord, 0);
            }
        }
    }

    public int getDataDiskID() {
        if (this.diskStack != null && this.diskStack.itemID == mod_ComputerCraft.disk.shiftedIndex) {
            return ItemDisk.getDiskID(this.diskStack, this.worldObj);
        }
        return -1;
    }

    public String getAudioDiscRecordName() {
        Item item;
        if (this.diskStack != null && (item = Item.itemsList[this.diskStack.itemID]) instanceof ItemRecord) {
            ItemRecord record = (ItemRecord)item;
            return record.recordName;
        }
        return null;
    }

    public void updateEntity() {
        if (this.m_firstFrame) {
            if (!mod_ComputerCraft.isMultiplayerClient()) {
                this.m_clientDiskLight = 0;
                if (this.hasAnything()) {
                    this.m_clientDiskLight = this.hasDisk() ? 1 : 2;
                }
                mod_ComputerCraft.notifyBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_ComputerCraft.diskDrive.blockID);
            }
            this.m_firstFrame = false;
        }
    }

    private Packet230ModLoader createDiskLightPacket() {
        Packet230ModLoader packet = new Packet230ModLoader();
        packet.packetType = 8;
        packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord, this.m_clientDiskLight};
        return packet;
    }

    private void updateClient(EntityPlayer player) {
        if (mod_ComputerCraft.isMultiplayerServer()) {
            Packet230ModLoader diskLight = this.createDiskLightPacket();
            mod_ComputerCraft.sendToPlayer(player, diskLight);
        }
    }

    @Override
    public void handlePacket(Packet230ModLoader packet, EntityPlayer player) {
        if (mod_ComputerCraft.isMultiplayerServer()) {
            switch (packet.packetType) {
                case 5: {
                    this.updateClient(player);
                    break;
                }
            }
        } else {
            switch (packet.packetType) {
                case 8: {
                    this.m_clientDiskLight = packet.dataInt[3];
                    mod_ComputerCraft.notifyBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_ComputerCraft.diskDrive.blockID);
                    break;
                }
            }
        }
    }
}
