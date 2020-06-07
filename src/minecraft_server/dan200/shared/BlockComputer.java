/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import java.util.Random;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.ModLoader;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.mod_ComputerCraft;

public class BlockComputer extends BlockContainer {
    private Random random = new Random();

    public int[] blockTextures = new int[]{0, 0, 0, 0};
    public int blinkTexture = 0;
    
    public BlockComputer(int i) {
        super(i, Material.rock);
        setTickOnLoad(true);
    }

    public int idDropped(int paramInt, Random paramRandom) {
        return this.blockID;
    }

    public void onBlockAdded(World world, int i, int j, int k) {
        super.onBlockAdded(world, i, j, k);
        this.setDefaultDirection(world, i, j, k);
        refreshInput(world, i, j, k);
        //world.scheduleBlockUpdate(i, j, k, blockID, tickRate());
    }
    
    private boolean isBlockProvidingPower(World world, int i, int j, int k, int l) {
    	return world.isBlockIndirectlyProvidingPowerTo(i, j, k, l) || world.getBlockId(i, j, k) == Block.redstoneWire.blockID && world.getBlockMetadata(i, j, k) > 0 || RedPowerInterop.isPoweringTo((World)world, i, j, k, l);
      }
    
    private int getBundledPowerOutput(World world, int i, int j, int k, int side) {
        int cons = RedPowerInterop.getConDirMask(side);
        int combination = 0;
        for (int c = 0; c < 16; ++c) {
            if (RedPowerInterop.getPowerState((World)world, i, j, k, cons, c + 1) <= 0) continue;
            combination |= 1 << c;
        }
        return combination;
    }
    
    private void refreshInput(World world, int i, int j, int k) {
        TileEntityComputer computer = (TileEntityComputer)world.getBlockTileEntity(i, j, k);
        if (computer != null) {
            int m = world.getBlockMetadata(i, j, k);
            computer.providePower(BlockComputer.getLocalSide(0, m), this.isBlockProvidingPower(world, i, j + 1, k, 1));
            computer.providePower(BlockComputer.getLocalSide(1, m), this.isBlockProvidingPower(world, i, j - 1, k, 0));
            computer.providePower(BlockComputer.getLocalSide(2, m), this.isBlockProvidingPower(world, i, j, k + 1, 3));
            computer.providePower(BlockComputer.getLocalSide(3, m), this.isBlockProvidingPower(world, i, j, k - 1, 2));
            computer.providePower(BlockComputer.getLocalSide(4, m), this.isBlockProvidingPower(world, i + 1, j, k, 5));
            computer.providePower(BlockComputer.getLocalSide(5, m), this.isBlockProvidingPower(world, i - 1, j, k, 4));
            if (RedPowerInterop.isRedPowerInstalled()) {
                computer.setBundledPowerInput(BlockComputer.getLocalSide(0, m), this.getBundledPowerOutput(world, i, j, k, 1));
                computer.setBundledPowerInput(BlockComputer.getLocalSide(1, m), this.getBundledPowerOutput(world, i, j, k, 0));
                computer.setBundledPowerInput(BlockComputer.getLocalSide(2, m), this.getBundledPowerOutput(world, i, j, k, 3));
                computer.setBundledPowerInput(BlockComputer.getLocalSide(3, m), this.getBundledPowerOutput(world, i, j, k, 2));
                computer.setBundledPowerInput(BlockComputer.getLocalSide(4, m), this.getBundledPowerOutput(world, i, j, k, 5));
                computer.setBundledPowerInput(BlockComputer.getLocalSide(5, m), this.getBundledPowerOutput(world, i, j, k, 4));
            }
            computer.updateDiskInfo(BlockComputer.getLocalSide(0, m), BlockComputer.getDiskDriveAt(world, i, j + 1, k));
            computer.updateDiskInfo(BlockComputer.getLocalSide(1, m), BlockComputer.getDiskDriveAt(world, i, j - 1, k));
            computer.updateDiskInfo(BlockComputer.getLocalSide(2, m), BlockComputer.getDiskDriveAt(world, i, j, k + 1));
            computer.updateDiskInfo(BlockComputer.getLocalSide(3, m), BlockComputer.getDiskDriveAt(world, i, j, k - 1));
            computer.updateDiskInfo(BlockComputer.getLocalSide(4, m), BlockComputer.getDiskDriveAt(world, i + 1, j, k));
            computer.updateDiskInfo(BlockComputer.getLocalSide(5, m), BlockComputer.getDiskDriveAt(world, i - 1, j, k));
        }
      }

    public static TileEntityDiskDrive getDiskDriveAt(World world, int i, int j, int k) {
        TileEntity entity = world.getBlockTileEntity(i, j, k);
        if (entity != null && entity instanceof TileEntityDiskDrive)
          return (TileEntityDiskDrive)entity; 
        return null;
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

    public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    	if (l == 1 || l == 0) {
            return this.blockTextures[3];
        }
        int i1 = iblockaccess.getBlockMetadata(i, j, k);
        if (l == i1) {
            TileEntityComputer computer = (TileEntityComputer)iblockaccess.getBlockTileEntity(i, j, k);
            if (computer != null && computer.isSwitchedOn()) {
                if (computer.isCursorVisible()) {
                    return this.blinkTexture;
                }
                return this.blockTextures[2];
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
            return this.blinkTexture;
        }
        return this.blockTextures[1];
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
    
    public boolean blockActivated(World world, int x, int y, int z, EntityPlayer entityplayer) {
    	if (mod_ComputerCraft.isMultiplayerClient()) {
            return true;
        }
        if (!entityplayer.isSneaking()) {
            TileEntityComputer computer = (TileEntityComputer)world.getBlockTileEntity(x, y, z);
            if (computer != null) {
                Packet230ModLoader packet = new Packet230ModLoader();
                packet.packetType = 1;
                packet.dataInt = new int[]{x, y, z};
                mod_ComputerCraft.sendToPlayer(entityplayer, packet);
                computer.setSwitchedOn(true);
                computer.updateClient(entityplayer);
            }
            return true;
        }
        return false;
    }
    
    public boolean isBlockNormalCube(World world, int i, int j, int k) {
	    return false;
    }
    
    public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    	//System.out.println("ComputerCraft: isPoweringTo");
        TileEntityComputer computer = (TileEntityComputer)iblockaccess.getBlockTileEntity(i, j, k);
        if (computer != null) {
            int side = BlockComputer.getLocalSide(l, iblockaccess.getBlockMetadata(i, j, k));
            return computer.isPowering(side);
        }
        return false;
        
    }
    
    public void updateTick(World world, int i, int j, int k, Random random) {
        this.refreshInput(world, i, j, k);
        //System.out.println("ComputerCraft: updateTick");
        
        //things don't update properly unless we do this terribleness
        //world.scheduleBlockUpdate(i, j, k, blockID, tickRate());
    }
    
    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
    	this.refreshInput(world, i, j, k);
    }
    
    public int tickRate()
    {
        return 1;
    }
    
    public void onBlockRemoval(World world, int i, int j, int k) {
    	TileEntityComputer computer = (TileEntityComputer)world.getBlockTileEntity(i, j, k);
        if (computer != null)
          computer.destroy();
        super.onBlockRemoval(world, i, j, k);
    }

    public static int getOppositeSide(int side) {
        switch (side) {
          case 0:
            return 1;
          case 1:
            return 0;
          case 2:
            return 3;
          case 3:
            return 2;
          case 4:
            return 5;
          case 5:
            return 4;
        } 
        return side;
      }
    
    public static int getLocalSide(int worldSide, int metadata) {
        int right;
        int left;
        int back;
        int front = metadata;
        switch (front) {
            case 2: {
                back = 3;
                left = 4;
                right = 5;
                break;
            }
            case 3: {
                back = 2;
                left = 5;
                right = 4;
                break;
            }
            case 4: {
                back = 5;
                left = 3;
                right = 2;
                break;
            }
            case 5: {
                back = 4;
                left = 2;
                right = 3;
                break;
            }
            default: {
                return worldSide;
            }
        }
        if (worldSide == front) {
            return 3;
        }
        if (worldSide == back) {
            return 2;
        }
        if (worldSide == left) {
            return 4;
        }
        if (worldSide == right) {
            return 5;
        }
        return worldSide;
    }

    public boolean canProvidePower() {
        return true;
    }
    
	@Override
	protected TileEntity getBlockEntity() {
		Class computer = RedPowerInterop.getComputerClass();
		try {
		      return (TileEntity) computer.newInstance();
		    } catch (Exception e) {
		      return new TileEntityComputer();
		    } 
	}
}
