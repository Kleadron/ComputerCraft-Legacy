/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import dan200.shared.TileEntityComputer;
import net.minecraft.src.IBlockAccess;

import java.lang.reflect.Method;

public class RedPowerInterop
 {
    private static boolean redPowerSearched = false;
    private static Class redPowerLib = null;
    private static Method redPowerLib_isSearching = null;
    private static Method redPowerLib_isPoweringTo = null;
    private static Method redPowerLib_getPowerState = null;
    private static Method redPowerLib_getConDirMask = null;
    private static Method redPowerLib_addCompatibleMapping = null;

    private static Method findRedPowerMethod(String name, Class[] args) {
        try {
            return redPowerLib.getMethod(name, args);
        }
        catch (NoSuchMethodException e) {
            System.out.println("ComputerCraft: RedPowerLib method " + name + " not found.");
            return null;
        }
    }

    /*
// WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void findRedPower() {
        if (!redPowerSearched) {
            try {
                System.out.println("ComputerCraft: Searching for RedPowerLib...");
                redPowerLib = Class.forName("eloraam.core.RedPowerLib");
                redPowerLib_isSearching = RedPowerInterop.findRedPowerMethod("isSearching", new Class[0]);
                redPowerLib_isPoweringTo = RedPowerInterop.findRedPowerMethod("isPoweringTo", new Class[]{IBlockAccess.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE});
                redPowerLib_getPowerState = RedPowerInterop.findRedPowerMethod("getPowerState", new Class[]{IBlockAccess.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE});
                redPowerLib_getConDirMask = RedPowerInterop.findRedPowerMethod("getConDirMask", new Class[]{Integer.TYPE});
                redPowerLib_addCompatibleMapping = RedPowerInterop.findRedPowerMethod("addCompatibleMapping", new Class[]{Integer.TYPE, Integer.TYPE});
                System.out.println("ComputerCraft: RedPowerLib and methods located.");
            }
            catch (ClassNotFoundException e) {
                System.out.println("ComputerCraft: RedPowerLib not found.");
            }
            finally {
                redPowerSearched = true;
            }
        }
    }

    public static boolean isRedPowerInstalled() {
        RedPowerInterop.findRedPower();
        return redPowerLib != null;
    }

    public static boolean isSearching() {
        RedPowerInterop.findRedPower();
        if (redPowerLib_isSearching != null) {
            try {
                Object result = redPowerLib_isSearching.invoke(null, new Object[0]);
                return (Boolean)result;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        RedPowerInterop.findRedPower();
        if (redPowerLib_isPoweringTo != null) {
            try {
                Object result = redPowerLib_isPoweringTo.invoke(null, new Object[]{iblockaccess, i, j, k, l});
                return (Boolean)result;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static int getPowerState(IBlockAccess iblockaccess, int i, int j, int k, int cons, int ch) {
        RedPowerInterop.findRedPower();
        if (redPowerLib_getPowerState != null) {
            try {
                Object result = redPowerLib_getPowerState.invoke(null, new Object[]{iblockaccess, i, j, k, cons, ch});
                return (Integer)result;
            }
            catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static int getConDirMask(int dir) {
        RedPowerInterop.findRedPower();
        if (redPowerLib_getConDirMask != null) {
            try {
                Object result = redPowerLib_getConDirMask.invoke(null, dir);
                return (Integer)result;
            }
            catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public static void addCompatibleMapping(int a, int b) {
        RedPowerInterop.findRedPower();
        if (redPowerLib_addCompatibleMapping != null) {
            try {
                redPowerLib_addCompatibleMapping.invoke(null, a, b);
            }
            catch (Exception exception) {
// empty catch block
            }
        }
    }

    public static Class getComputerClass() {
        RedPowerInterop.findRedPower();
        if (RedPowerInterop.isRedPowerInstalled()) {
            try {
                Class<?> redPowerComputer = Class.forName("dan200.shared.RedPowerTileEntityComputer");
                return redPowerComputer;
            }
            catch (ClassNotFoundException e) {
                System.out.println("ComputerCraft: Exception loading dan200.shared.RedPowerTileEntityComputer");
                System.out.println("ComputerCraft: Computers will not have RedPower support");
            }
        }
        return TileEntityComputer.class;
    }
}
