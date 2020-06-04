/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;
/* 
 * UNIMPLEMENTED
 * 
 * 
import dan200.shared.BlockComputer;
import dan200.shared.RedPowerInterop;
import dan200.shared.TileEntityComputer;
import eloraam.core.IRedPowerConnectable;

public class RedPowerTileEntityComputer
extends TileEntityComputer
implements IRedPowerConnectable
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
        int metadata = this.i.e(this.j, this.k, this.l);
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
}*/
