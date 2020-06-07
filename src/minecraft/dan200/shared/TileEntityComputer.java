/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.TileEntity;
import net.minecraft.src.mod_ComputerCraft;

public class TileEntityComputer extends TileEntity implements IComputerCraftEntity
 {
    private Terminal m_terminal;
    private Computer m_computer;
    private ClientData m_clientData;
    private boolean m_destroyed;

    public TileEntityComputer() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.m_terminal = new Terminal(mod_ComputerCraft.terminal_width, mod_ComputerCraft.terminal_height);
            this.m_computer = new Computer(this, this.m_terminal);
            this.m_clientData = null;
        } else {
            this.m_terminal = new Terminal(50, 18);
            this.m_computer = null;
            this.m_clientData = new ClientData();
        }
        this.m_destroyed = false;
    }

    public void func_31004_j() {
        super.func_31004_j();
        if (mod_ComputerCraft.isMultiplayerClient()) {
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 5;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord};
            mod_ComputerCraft.sendToServer(packet);
        }
    }

    public Packet getDescriptionPacket() {
        return this.createOutputChangedPacket();
    }

    public void destroy() {
        if (!this.m_destroyed) {
            if (!mod_ComputerCraft.isMultiplayerClient()) {
                this.m_computer.destroy();
            }
            this.m_destroyed = true;
        }
    }

    public boolean isDestroyed() {
        return this.m_destroyed;
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateEntity() {
        double dt = 0.05;
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            Packet230ModLoader packet;
            this.m_computer.advance(dt);
            if (this.m_computer.pollChanged()) {
                mod_ComputerCraft.notifyBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_ComputerCraft.computer.blockID);
                if (mod_ComputerCraft.isMultiplayerServer()) {
                    packet = this.createOutputChangedPacket();
                    mod_ComputerCraft.sendToAllPlayers(packet);
                }
            }
            if (mod_ComputerCraft.isMultiplayerServer()) {
                packet = null;
                Terminal terminal = this.m_terminal;
                synchronized (terminal) {
                    if (this.m_terminal.getChanged()) {
                        packet = this.createTerminalChangedPacket(false);
                        this.m_terminal.clearChanged();
                    }
                }
                if (packet != null) {
                    mod_ComputerCraft.sendToAllPlayers(packet);
                }
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            nbttagcompound.setBoolean("on", this.isSwitchedOn());
            String userDir = this.m_computer.getUserDir();
            if (userDir != null) {
                nbttagcompound.setString("userDir", userDir);
            }
        } else {
            nbttagcompound.setBoolean("on", this.m_clientData.on);
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.setSwitchedOn(nbttagcompound.getBoolean("on"));
            String userDir = nbttagcompound.getString("userDir");
            if (userDir != null && userDir.length() > 0) {
                this.m_computer.setUserDir(userDir);
            }
        } else {
            this.m_clientData.on = nbttagcompound.getBoolean("on");
        }
    }

    public void keyTyped(char ch, int key) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.m_computer.pressKey(ch, key);
        } else {
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 2;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord, key};
            packet.dataString = new String[]{"" + ch};
            mod_ComputerCraft.sendToServer(packet);
        }
    }

    public void terminate() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.m_computer.terminate();
        } else {
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 6;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord};
            mod_ComputerCraft.sendToServer(packet);
        }
    }

    public void reboot() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.m_computer.reboot();
        } else {
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 9;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord};
            mod_ComputerCraft.sendToServer(packet);
        }
    }

    public void shutdown() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.m_computer.turnOff();
        } else {
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 12;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord};
            mod_ComputerCraft.sendToServer(packet);
        }
    }

    public boolean isSwitchedOn() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            return this.m_computer.isOn();
        }
        return this.m_clientData.on;
    }

    public void setSwitchedOn(boolean on) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            if (on) {
                this.m_computer.turnOn();
            } else {
                this.m_computer.turnOff();
            }
        }
    }

    public boolean isCursorVisible() {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            return this.m_computer.isBlinking();
        }
        return this.m_clientData.on && this.m_clientData.blinking;
    }

    public Terminal getTerminal() {
        return this.m_terminal;
    }

    public boolean isPowering(int side) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            return this.m_computer.getOutput(side);
        }
        return this.m_clientData.output[side];
    }

    public void providePower(int side, boolean onOff) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.m_computer.setInput(side, onOff);
        }
    }

    public int getBundledPowerOutput(int side) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            return this.m_computer.getBundledOutput(side);
        }
        return this.m_clientData.bundledOutput[side];
    }

    public void setBundledPowerInput(int side, int combination) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            this.m_computer.setBundledInput(side, combination);
        }
    }

    public void updateDiskInfo(int side, TileEntityDiskDrive diskDrive) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            if (diskDrive != null && diskDrive.hasAnything()) {
                this.m_computer.setDiskInfo(side, true, diskDrive.getDataDiskID(), diskDrive.getAudioDiscRecordName());
            } else {
                this.m_computer.setDiskInfo(side, false, -1, null);
            }
        }
    }

    public void playRecord(String record) {
        if (mod_ComputerCraft.isMultiplayerServer()) {
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 7;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord};
            if (record != null) {
                packet.dataString = new String[]{record};
            }
            mod_ComputerCraft.sendToAllPlayers(packet);
        } else {
            this.worldObj.playRecord(record, this.xCoord, this.yCoord, this.zCoord);
        }
    }

    private void tryEjectDisk(int targetSide, int testSide, int i, int j, int k) {
        TileEntityDiskDrive drive;
        if (targetSide == testSide && (drive = BlockComputer.getDiskDriveAt(this.worldObj, i, j, k)) != null) {
            drive.ejectContents(false);
        }
    }

    public void ejectDisk(int side) {
        if (!mod_ComputerCraft.isMultiplayerClient()) {
            int i = this.xCoord;
            int j = this.yCoord;
            int k = this.zCoord;
            int m = this.worldObj.getBlockMetadata(i, j, k);
            this.tryEjectDisk(side, BlockComputer.getLocalSide(0, m), i, j + 1, k);
            this.tryEjectDisk(side, BlockComputer.getLocalSide(1, m), i, j - 1, k);
            this.tryEjectDisk(side, BlockComputer.getLocalSide(2, m), i, j, k + 1);
            this.tryEjectDisk(side, BlockComputer.getLocalSide(3, m), i, j, k - 1);
            this.tryEjectDisk(side, BlockComputer.getLocalSide(4, m), i + 1, j, k);
            this.tryEjectDisk(side, BlockComputer.getLocalSide(5, m), i - 1, j, k);
        }
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    private Packet230ModLoader createTerminalChangedPacket(boolean _includeAllText) {
        Terminal terminal = this.m_terminal;
        synchronized (terminal) {
            boolean[] lineChanged = this.m_terminal.getLinesChanged();
            int lineChangeMask = this.m_terminal.getCursorBlink() ? 1 : 0;
            int lineChangeCount = 0;
            for (int y = 0; y < this.m_terminal.getHeight(); ++y) {
                if (!lineChanged[y] && !_includeAllText) continue;
                lineChangeMask += 1 << y + 1;
                ++lineChangeCount;
            }
            Packet230ModLoader packet = new Packet230ModLoader();
            packet.packetType = 3;
            packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord, this.m_terminal.getCursorX(), this.m_terminal.getCursorY(), lineChangeMask};
            packet.dataString = new String[lineChangeCount];
            int n = 0;
            for (int y = 0; y < this.m_terminal.getHeight(); ++y) {
                if (!lineChanged[y] && !_includeAllText) continue;
                packet.dataString[n++] = this.m_terminal.getLine(y).replaceAll(" +$", "");
            }
            return packet;
        }
    }

    private Packet230ModLoader createOutputChangedPacket() {
        Packet230ModLoader packet = new Packet230ModLoader();
        packet.packetType = 4;
        int flags = 0;
        if (this.m_computer.isOn()) {
            ++flags;
        }
        if (this.m_computer.isBlinking()) {
            flags += 2;
        }
        for (int i = 0; i < 6; ++i) {
            if (!this.m_computer.getOutput(i)) continue;
            flags += 1 << i + 2;
        }
        packet.dataInt = new int[]{this.xCoord, this.yCoord, this.zCoord, flags, this.m_computer.getBundledOutput(0), this.m_computer.getBundledOutput(1), this.m_computer.getBundledOutput(2), this.m_computer.getBundledOutput(3), this.m_computer.getBundledOutput(3), this.m_computer.getBundledOutput(5)};
        return packet;
    }

    public void updateClient(EntityPlayer player) {
        if (mod_ComputerCraft.isMultiplayerServer()) {
            Packet230ModLoader terminalChanged = this.createTerminalChangedPacket(true);
            mod_ComputerCraft.sendToPlayer(player, terminalChanged);
            Packet230ModLoader outputChanged = this.createOutputChangedPacket();
            mod_ComputerCraft.sendToPlayer(player, outputChanged);
        }
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handlePacket(Packet230ModLoader packet, EntityPlayer player) {
        if (mod_ComputerCraft.isMultiplayerServer()) {
            switch (packet.packetType) {
                case 2: {
                    int key = packet.dataInt[3];
                    char ch = packet.dataString[0].charAt(0);
                    this.keyTyped(ch, key);
                    break;
                }
                case 6: {
                    this.terminate();
                    break;
                }
                case 9: {
                    this.reboot();
                    break;
                }
                case 12: {
                    this.shutdown();
                    break;
                }
                case 5: {
                    this.updateClient(player);
                    break;
                }
            }
        } else {
            switch (packet.packetType) {
                case 3: {
                    Terminal key = this.m_terminal;
                    synchronized (key) {
                        int n = 0;
                        int lineChangeMask = packet.dataInt[5];
                        for (int y = 0; y < this.m_terminal.getHeight(); ++y) {
                            if ((lineChangeMask & 1 << y + 1) <= 0) continue;
                            this.m_terminal.setCursorPos(0, y);
                            this.m_terminal.clearLine();
                            this.m_terminal.write(packet.dataString[n++]);
                        }
                        this.m_terminal.setCursorPos(packet.dataInt[3], packet.dataInt[4]);
                        this.m_terminal.setCursorBlink((lineChangeMask & 1) > 0);
                        break;
                    }
                }
                case 4: {
                    int flags = packet.dataInt[3];
                    this.m_clientData.on = (flags & 1) > 0;
                    this.m_clientData.blinking = (flags & 2) > 0;
                    for (int i = 0; i < 6; ++i) {
                        this.m_clientData.output[i] = (flags & 1 << i + 2) > 0;
                        this.m_clientData.bundledOutput[i] = packet.dataInt[4 + i];
                    }
                    mod_ComputerCraft.notifyBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, mod_ComputerCraft.computer.blockID);
                    break;
                }
                case 7: {
                    if (packet.dataString != null && packet.dataString.length > 0) {
                        this.playRecord(packet.dataString[0]);
                        break;
                    }
                    this.playRecord(null);
                    break;
                }
            }
        }
    }

    private class ClientData
     {
        boolean on = false;
        boolean blinking = false;
        boolean[] output = new boolean[6];
        int[] bundledOutput = new int[6];

        ClientData() {
        }
    }
}