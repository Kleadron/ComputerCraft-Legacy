/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package net.minecraft.src;

import java.io.File;

import dan200.client.FixedWidthFontRenderer;
import dan200.client.GuiComputer;
import dan200.client.GuiDiskDrive;
import dan200.client.TextureComputerFX;
import dan200.shared.BlockComputer;
import dan200.shared.BlockDiskDrive;
import dan200.shared.IComputerCraftEntity;
import dan200.shared.ItemDisk;
import dan200.shared.RedPowerInterop;
import dan200.shared.TileEntityComputer;
import dan200.shared.TileEntityDiskDrive;
import net.minecraft.client.Minecraft;

public class mod_ComputerCraft extends BaseModMp {
	
	@MLProp
    public static int computerBlockID = 207;
    @MLProp
    public static int diskDriveBlockID = 208;
    @MLProp
    public static int diskItemID = 4000;
    public static int diskDriveGUIID = 100;
    @MLProp
    public static int enableAPI_http = 1;
    @MLProp
    public static int enableAPI_luajava = 0;
    public static final int terminal_defaultWidth = 50;
    public static final int terminal_defaultHeight = 18;
    @MLProp
    public static int terminal_width = 50;
    @MLProp
    public static int terminal_height = 18;
    @MLProp
    public static int terminal_textColour_r = 255;
    @MLProp
    public static int terminal_textColour_g = 255;
    @MLProp
    public static int terminal_textColour_b = 255;
    
    public static mod_ComputerCraft instance;
    public static FixedWidthFontRenderer fixedWidthFontRenderer;
    public static BlockComputer computer;
    public static BlockDiskDrive diskDrive;
    public static ItemDisk disk;
    private static int m_tickCount;
	
    public static int[] loadTerrainTextures(String folder, int count) {
        int[] result = new int[count];
        for (int i = 0; i < count; ++i) {
            result[i] = ModLoader.addOverride((String)"/terrain.png", (String)("/terrain/" + folder + "/" + i + ".png"));
        }
        return result;
    }
    
	@Override
	public String Version() {
		// TODO Auto-generated method stub
		return "1.21";
	}
	
	public mod_ComputerCraft() {
		instance = this;
        System.out.println("ComputerCraft: computerBlockID " + computerBlockID);
        System.out.println("ComputerCraft: diskDriveBlockID " + diskDriveBlockID);
        System.out.println("ComputerCraft: diskItemID " + diskItemID);
        System.out.println("ComputerCraft: To change IDs, modify config/mod_ComputerCraft.cfg");
        computer = new BlockComputer(computerBlockID);
        computer.setHardness(1.0f).setBlockName("computer"); //.setRequiresSelfNotify(); does not exist in this version, might be why redstone doesn't work?
        ModLoader.RegisterBlock((Block)computer);
        ModLoader.AddName((Object)((Object)computer), (String)"Computer");
        ModLoader.AddRecipe((ItemStack)new ItemStack((Block)computer, 1), (Object[])new Object[]{"XXX", "XYX", "XZX", Character.valueOf('X'), Block.stone, Character.valueOf('Y'), Item.redstone, Character.valueOf('Z'), Block.glass});
        ModLoader.RegisterTileEntity((Class)RedPowerInterop.getComputerClass(), (String)"computer");
        diskDrive = new BlockDiskDrive(diskDriveBlockID);
        diskDrive.setHardness(1.0f).setBlockName("diskdrive"); //.setRequiresSelfNotify();
        ModLoader.RegisterBlock((Block)diskDrive);
        ModLoader.AddName((Object)((Object)diskDrive), (String)"Disk Drive");
        ModLoader.AddRecipe((ItemStack)new ItemStack((Block)diskDrive, 1), (Object[])new Object[]{"XXX", "XYX", "XYX", Character.valueOf('X'), Block.stone, Character.valueOf('Y'), Item.redstone});
        ModLoader.RegisterTileEntity(TileEntityDiskDrive.class, (String)"diskdrive");
        disk = new ItemDisk(diskItemID);
        disk.setItemName("disk");
        ModLoader.AddName((Object)((Object)disk), (String)"Floppy Disk");
        ModLoader.AddRecipe((ItemStack)new ItemStack((Item)disk, 1), (Object[])new Object[]{"X", "Y", Character.valueOf('X'), Item.redstone, Character.valueOf('Y'), Item.paper});
        
        //testing recipes
        //ModLoader.AddRecipe((ItemStack)new ItemStack((Item)disk, 1), (Object[])new Object[]{"XX", "X ", 'X', Block.dirt});
        //ModLoader.AddRecipe((ItemStack)new ItemStack(Item.recordCat, 1), (Object[])new Object[]{"X", "X", 'X', Block.dirt});
        //ModLoader.AddRecipe((ItemStack)new ItemStack((Block)diskDrive, 1), (Object[])new Object[]{"XX", 'X', Block.dirt});
        //ModLoader.AddRecipe((ItemStack)new ItemStack((Block)computer, 1), (Object[])new Object[]{"X", 'X', Block.dirt});

        ModLoaderMp.RegisterGUI((BaseModMp)this, (int)diskDriveGUIID);
        m_tickCount = 0;
        ModLoader.SetInGameHook((BaseMod)this, (boolean)true, (boolean)true);
        
        //load doesn't execute normally so i'm putting it here, it's also pretty important
        load();
	}

	public String getPriorities() {
        return "";
    }

    public void load() {
        Minecraft mc = ModLoader.getMinecraftInstance();
        fixedWidthFontRenderer = new FixedWidthFontRenderer(mc.gameSettings, "/font/default.png", mc.renderEngine);
        mod_ComputerCraft.computer.blockTextures = mod_ComputerCraft.loadTerrainTextures("computer", 4);
        mod_ComputerCraft.diskDrive.blockTextures = mod_ComputerCraft.loadTerrainTextures("diskdrive", 5);
        mod_ComputerCraft.disk.iconIndex = ModLoader.addOverride((String)"/gui/items.png", (String)"/items/floppy.png");
    }

    public void RegisterAnimation(Minecraft game) {
        mod_ComputerCraft.computer.blinkTexture = ModLoader.getUniqueSpriteIndex((String)"/terrain.png");
        try {
            ModLoader.addAnimation((TextureFX)new TextureComputerFX(game, mod_ComputerCraft.computer.blinkTexture, 42));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static boolean getCursorBlink() {
		return (m_tickCount / 6 % 2 == 0);
	}
	
	public boolean OnTickInGame(Minecraft game) {
	    ItemDisk.loadLabelsIfWorldChanged(game.theWorld);
	    m_tickCount++;
	    return true;
	  }

	public static void notifyBlockChange(World world, int x, int y, int z, int b) {
	    world.notifyBlockChange(x, y, z, b);
	  }

	public static File getModDir() {
        Minecraft mc = ModLoader.getMinecraftInstance();
        return new File(Minecraft.getMinecraftDir(), "mods/ComputerCraft");
    }

    public static File getWorldDir(World world) {
        Minecraft mc = ModLoader.getMinecraftInstance();
        return new File(Minecraft.getMinecraftDir(), "saves/" + world.worldInfo.getWorldName());
    }
    public static void openDiskDriveGUI(EntityPlayer entityplayer, TileEntityDiskDrive drive) {
        GuiDiskDrive gui = new GuiDiskDrive(entityplayer.inventory, drive);
        ModLoader.OpenGUI((EntityPlayer)entityplayer, (GuiScreen)gui);
    }

    public GuiScreen HandleGUI(int inventoryType) {
        EntityPlayer player = ModLoader.getMinecraftInstance().thePlayer;
        if (inventoryType == diskDriveGUIID) {
            TileEntityDiskDrive tempDrive = new TileEntityDiskDrive();
            return new GuiDiskDrive(player.inventory, tempDrive);
        }
        return null;
    }

    public static boolean isMultiplayerClient() {
        World world = ModLoader.getMinecraftInstance().theWorld;
        if (world != null) {
            return world.multiplayerWorld;
        }
        return false;
    }

    public static boolean isMultiplayerServer() {
        return false;
    }

    public static void sendToPlayer(EntityPlayer player, Packet230ModLoader packet) {
        if (player instanceof EntityPlayerSP) {
            instance.HandlePacket(packet);
        }
    }

    public static void sendToAllPlayers(Packet230ModLoader packet) {
        instance.HandlePacket(packet);
    }

    public static void sendToServer(Packet230ModLoader packet) {
        ModLoaderMp.SendPacket((BaseModMp)instance, (Packet230ModLoader)packet);
    }

    public void HandlePacket(Packet230ModLoader packet) {
        World world = ModLoader.getMinecraftInstance().theWorld;
        EntityPlayer player = ModLoader.getMinecraftInstance().thePlayer;
        if (world != null) {
            if (packet.packetType == 10) {
                for (int n = 0; n < packet.dataInt.length; ++n) {
                    int id = packet.dataInt[n];
                    String label = packet.dataString[n];
                    ItemDisk.setDiskLabel(id, label);
                }
            } else {
                int i = packet.dataInt[0];
                int j = packet.dataInt[1];
                int k = packet.dataInt[2];
                TileEntity entity = world.getBlockTileEntity(i, j, k);
                if (entity != null && entity instanceof IComputerCraftEntity) {
                    IComputerCraftEntity iComputerCraftEntity = (IComputerCraftEntity)entity;
                    if (packet.packetType == 1) {
                        ModLoader.OpenGUI((EntityPlayer)player, (GuiScreen)new GuiComputer((TileEntityComputer)iComputerCraftEntity));
                    } else {
                        iComputerCraftEntity.handlePacket(packet, (EntityPlayer)player);
                    }
                }
            }
        }
    }

}

