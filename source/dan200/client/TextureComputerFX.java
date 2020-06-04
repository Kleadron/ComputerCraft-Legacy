/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.client;

import net.minecraft.client.Minecraft;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ModTextureAnimation;
import net.minecraft.src.RenderEngine;

public class TextureComputerFX
extends ModTextureAnimation
 {
    public TextureComputerFX(Minecraft mc, int iconIndex, int nope) throws Exception {
        super(iconIndex, 1, 0, ModLoader.loadImage((RenderEngine)mc.renderEngine, (String)"/terrain/computer/4.png"), 6);
    }
}
