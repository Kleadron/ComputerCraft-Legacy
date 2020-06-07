/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;
import dan200.shared.BlockComputer;
import dan200.shared.RedPowerInterop;
import dan200.shared.TileEntityComputer;
import eloraam.core.IRedPowerAPI;
import net.minecraft.src.TileEntity;;

public class RedPowerTileEntityComputer
extends TileEntityComputer
implements IRedPowerAPI
 {
    private static int computerConnectClass = 1337;
    private static boolean computerConnectMappingsAdded = false;

    public RedPowerTileEntityComputer() {
        if (!computerConnectMappingsAdded) {
            RedPowerInterop.addCompatibleMapping(0, computerConnectClass);
            RedPowerInterop.addCompatibleMapping(18, computerConnectClass);
            for (int i = 0; i < 16; ++i) {
                RedPowerInterop.addCompatibleMapping(1 + i, computerConnectClass);
                RedPowerInterop.addCompatibleMapping(19 + i, computerConnectClass);
            }
            computerConnectMappingsAdded = true;
        }
    }

    public int getConnectableMask() {
        return -1;
    }

    public int getConnectClass(int side) {
        return computerConnectClass;
    }

    public int getCornerPowerMode() {
        return 0;
    }

    public int getPoweringMask(int ch) {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        if (ch == 0) {
            int mask = 0;
            for (int side = 0; side < 6; ++side) {
                int localSide = BlockComputer.getOppositeSide(BlockComputer.getLocalSide(side, metadata));
                if (!this.isPowering(localSide)) continue;
                mask |= RedPowerInterop.getConDirMask(side);
            }
            return mask;
        }
        int mask = 0;
        for (int side = 0; side < 6; ++side) {
            int localSide = BlockComputer.getOppositeSide(BlockComputer.getLocalSide(side, metadata));
            int channelMask = this.getBundledPowerOutput(localSide);
            if ((channelMask & 1 << ch - 1) <= 0) continue;
            mask |= RedPowerInterop.getConDirMask(side);
        }
        return mask;
    }

    //this is very dumb and shouldn't need to be done but here we are
	@Override
	public int getPowerClass(int var1) {
		//System.out.println("Computer: getPowerClass on Side " + var1);
		switch (var1)
		{
		case 0: //bottom
			TileEntity wire = worldObj.getBlockTileEntity(xCoord, yCoord - 1, zCoord);
			return ((IRedPowerAPI) wire).getPowerClass(BlockComputer.getOppositeSide(var1));
		case 1: //top
			TileEntity wire2 = worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
			return ((IRedPowerAPI) wire2).getPowerClass(BlockComputer.getOppositeSide(var1));
		case 2: //back
			TileEntity wire3 = worldObj.getBlockTileEntity(xCoord, yCoord, zCoord - 1);
			return ((IRedPowerAPI) wire3).getPowerClass(BlockComputer.getOppositeSide(var1));
		case 3: //front
			TileEntity wire4 = worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + 1);
			return ((IRedPowerAPI) wire4).getPowerClass(BlockComputer.getOppositeSide(var1));
		case 4: //left
			TileEntity wire5 = worldObj.getBlockTileEntity(xCoord - 1, yCoord, zCoord);
			return ((IRedPowerAPI) wire5).getPowerClass(BlockComputer.getOppositeSide(var1));
		case 5: //right
			TileEntity wire6 = worldObj.getBlockTileEntity(xCoord + 1, yCoord, zCoord);
			return ((IRedPowerAPI) wire6).getPowerClass(BlockComputer.getOppositeSide(var1));
		}
		return 0;
	}
}
