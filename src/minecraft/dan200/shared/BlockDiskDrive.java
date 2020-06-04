/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import dan200.shared.TileEntityDiskDrive;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_ComputerCraft;

import java.util.Random;

public class BlockDiskDrive
extends BlockContainer
 {
    public int[] blockTextures = new int[]{0, 0, 0, 0};

    public BlockDiskDrive(int i) {
        super(i, Material.rock);
    }

    public int idDropped(int i, Random random) {
        return blockID;
    }

    public void onBlockAdded(World world, int i, int j, int k) {
        super.onBlockAdded(world, i, j, k);
        this.setDefaultDirection(world, i, j, k);
    }

    public void onBlockRemoval(World world, int i, int j, int k) {
        TileEntityDiskDrive drive = (TileEntityDiskDrive)world.getBlockTileEntity(i, j, k);
        if (drive != null) {
            drive.ejectContents(true);
        }
        super.onBlockRemoval(world, i, j, k);
    }

    private void setDefaultDirection(World world, int i, int j, int k) {
        int l = world.getBlockId(i, j, k - 1);
        int i1 = world.getBlockId(i, j, k + 1);
        int j1 = world.getBlockId(i - 1, j, k);
        int k1 = world.getBlockId(i + 1, j, k);
        int byte0 = 3;
        if (Block.opaqueCubeLookup[l] && !Block.opaqueCubeLookup[i1]) {
            byte0 = 3;
        }
        if (Block.opaqueCubeLookup[i1] && !Block.opaqueCubeLookup[l]) {
            byte0 = 2;
        }
        if (Block.opaqueCubeLookup[j1] && !Block.opaqueCubeLookup[k1]) {
            byte0 = 5;
        }
        if (Block.opaqueCubeLookup[k1] && !Block.opaqueCubeLookup[j1]) {
            byte0 = 4;
        }
        world.setBlockMetadataWithNotify(i, j, k, byte0);
    }

    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
        int l = MathHelper.floor_double((double)((double)(entityliving.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3;
        if (l == 0) {
            world.setBlockMetadataWithNotify(i, j, k, 2);
        }
        if (l == 1) {
            world.setBlockMetadataWithNotify(i, j, k, 5);
        }
        if (l == 2) {
            world.setBlockMetadataWithNotify(i, j, k, 3);
        }
        if (l == 3) {
            world.setBlockMetadataWithNotify(i, j, k, 4);
        }
    }

    public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        if (l == 1 || l == 0) {
            return this.blockTextures[3];
        }
        int i1 = iblockaccess.getBlockMetadata(i, j, k);
        if (l == i1) {
            TileEntityDiskDrive drive = (TileEntityDiskDrive)iblockaccess.getBlockTileEntity(i, j, k);
            if (drive != null && drive.hasAnything()) {
                if (drive.hasDisk()) {
                    return this.blockTextures[2];
                }
                return this.blockTextures[4];
            }
            return this.blockTextures[0];
        }
        return this.blockTextures[1];
    }

    public int getBlockTextureFromSide(int i) {
        if (i == 1 || i == 0) {
            return this.blockTextures[3];
        }
        if (i == 3) {
            return this.blockTextures[0];
        }
        return this.blockTextures[1];
    }

    public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
        if (mod_ComputerCraft.isMultiplayerClient()) {
            return true;
        }
        if (!entityplayer.isSneaking()) {
            TileEntityDiskDrive drive = (TileEntityDiskDrive)world.getBlockTileEntity(i, j, k);
            if (drive != null) {
                mod_ComputerCraft.openDiskDriveGUI(entityplayer, drive);
            }
            return true;
        }
        return false;
    }

    public TileEntity getBlockEntity() {
        return new TileEntityDiskDrive();
    }

    public boolean canProvidePower() {
        return false;
    }
}