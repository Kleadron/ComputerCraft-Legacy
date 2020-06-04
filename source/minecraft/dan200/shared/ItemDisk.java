/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet230ModLoader;
import net.minecraft.src.World;
import net.minecraft.src.mod_ComputerCraft;

public class ItemDisk
extends Item
 {
    public static WeakReference labelWorld = null;
    private static Map labels = new HashMap();
    private static Map serverLabelRequests = new HashMap();
    private static boolean labelsChanged = false;

    public ItemDisk(int i) {
        super(i);
        this.setMaxStackSize(1);
    }

    private static int getNewDiskID(World world) {
        File baseUserDir = new File(mod_ComputerCraft.getWorldDir(world), "/computer/disk");
        int id = 1;
        while (new File(baseUserDir, Integer.toString(id)).exists()) {
            ++id;
        }
        File userDir = new File(baseUserDir, Integer.toString(id));
        userDir.mkdirs();
        return id;
    }

    public static int getDiskID(ItemStack itemstack, World world) {
        if (itemstack.itemID == 106) {
            int damage = itemstack.getItemDamage();
            if (damage == 0 && world != null) {
                damage = ItemDisk.getNewDiskID(world);
                itemstack.setItemDamage(damage);
            }
            return damage;
        }
        return -1;
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadLabelsIfWorldChanged(World world) {
        Map strin = labels;
        synchronized (strin) {
            World currentWorld = null;
            if (labelWorld != null) {
                currentWorld = (World)labelWorld.get();
            }
            if (world != currentWorld) {
                labels.clear();
                serverLabelRequests.clear();
                labelWorld = null;
                labelsChanged = false;
                if (world != null) {
                    labelWorld = new WeakReference<World>(world);
                }
                    ComputerThread.start();
                    ComputerThread.queueTask(new ITask(){

                        @Override
                        public Computer getOwner() {
                            return null;
                        }

                        @Override
                        public void execute() {
                            ItemDisk.loadLabels((World)labelWorld.get());
                        }
                    }, null
                    );

            }
        }
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadLabels(World world) {
        Map strin = labels;
        synchronized (strin) {
block12:            {
                labels.clear();
                serverLabelRequests.clear();
                labelWorld = null;
                labelsChanged = false;
                if (world == null) {
                    return;
                }
                labelWorld = new WeakReference<World>(world);
                labelsChanged = false;
                BufferedReader reader = null;
                try {
                    File labelFile = new File(mod_ComputerCraft.getWorldDir(world), "/computer/disk/labels.txt");
                    if (!labelFile.exists()) break block12;
                    reader = new BufferedReader(new FileReader(labelFile));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        int number;
                        int space = line.indexOf(32);
                        if (space <= 0) continue;
                        try {
                            number = Integer.parseInt(line.substring(0, space));
                        }
                        catch (NumberFormatException e) {
                            continue;
                        }
                        String label = line.substring(space + 1).trim();
                        labels.put(number, label);
                    }
                    reader.close();
                }
                catch (IOException e) {
                    System.out.println("ComputerCraft: Failed to write to labels file");
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    }
                    catch (IOException e2) {
// empty catch block
                    }
                }
            }
        }
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void saveLabels() {
        Map strin = labels;
        synchronized (strin) {
            if (labelWorld == null) {
                return;
            }
            World world = (World)labelWorld.get();
            if (world == null) {
                labelWorld = null;
                return;
            }
            if (labelsChanged) {
                BufferedWriter writer = null;
                try {
                    File labelFile = new File(mod_ComputerCraft.getWorldDir(world), "/computer/disk/labels.txt");
                    writer = new BufferedWriter(new FileWriter(labelFile));
                    Set<Map.Entry> entries = labels.entrySet();
                    for (Map.Entry entry : entries) {
                        writer.write(entry.getKey() + " " + (String)entry.getValue());
                        writer.newLine();
                    }
                    writer.close();
                }
                catch (IOException e) {
                    System.out.println("ComputerCraft: Failed to write to labels file");
                    try {
                        if (writer != null) {
                            writer.close();
                        }
                    }
                    catch (IOException e2) {
// empty catch block
                    }
                }
                finally {
                    labelsChanged = false;
                }
            }
        }
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getDiskLabel(int diskID) {
        if (diskID > 0) {
            Map strin = labels;
            synchronized (strin) {
                String label = (String)labels.get(diskID);
                if (label != null) {
                    return label;
                }
            }
        }
        return null;
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setDiskLabel(int diskID, String label) {
        if (diskID > 0) {
            Map strin = labels;
            synchronized (strin) {
                if (label != null && label.length() == 0) {
                    label = null;
                }
                boolean changed = false;
                String existing = (String)labels.get(diskID);
                if (label != null) {
                    if ((label = label.trim().replaceAll("[\r\n\t]+", "")).length() > 25) {
                        label = label.substring(0, 25);
                    }
                    if (!label.equals(existing)) {
                        labels.put(diskID, label);
                        changed = true;
                    }
                } else if (existing != null) {
                    labels.remove(diskID);
                    changed = true;
                }
                if (changed) {
                    if (!labelsChanged) {
                        labelsChanged = true;
                        ComputerThread.queueTask(new ITask(){

                            @Override
                            public Computer getOwner() {
                                return null;
                            }

                            @Override
                            public void execute() {
                                ItemDisk.saveLabels();
                            }
                        }, null
                        );
                    }
                    //Packet230ModLoader packet = ItemDisk.buildDiskLabelPacket(diskID, label);
                    //mod_KMachines.sendToAllPlayers(packet);
                }
            }
        }
    }

    public static String getDiskLabel(ItemStack itemstack, World world) {
        int diskID = ItemDisk.getDiskID(itemstack, world);
        return ItemDisk.getDiskLabel(diskID);
    }

    public void a(ItemStack itemstack, List list) {
        String label = ItemDisk.getDiskLabel(itemstack, null);
        if (label != null && label.length() > 0) {
            list.add(label);
        }
    }

    public static void sendDiskLabelToPlayer(EntityPlayer player, int diskID) {
        String label = ItemDisk.getDiskLabel(diskID);
        if (label != null) {
            //Packet230ModLoader packet = ItemDisk.buildDiskLabelPacket(diskID, label);
            //mod_KMachines.sendToPlayer(player, packet);
        }
    }

    private static Packet230ModLoader buildDiskLabelPacket(int diskID, String label) {
        Packet230ModLoader packet = new Packet230ModLoader();
        packet.packetType = 10;
        packet.dataInt = new int[]{diskID};
        packet.dataString = new String[]{label != null ? label : ""};
        return packet;
    }
}
