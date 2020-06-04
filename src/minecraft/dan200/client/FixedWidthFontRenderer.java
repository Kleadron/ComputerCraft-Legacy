/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.ChatAllowedCharacters;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GameSettings;
import net.minecraft.src.RenderEngine;
import net.minecraft.src.Tessellator;

public class FixedWidthFontRenderer {
	  private int[] charWidth;
	  
	  public int fontTextureName;
	  
	  public int FONT_HEIGHT;
	  
	  public int FONT_WIDTH;
	  
	  private int fontDisplayLists;
	  
	  private IntBuffer buffer;
	  
	  public Random field_41064_c;
	  
	  public FixedWidthFontRenderer(GameSettings gamesettings, String s, RenderEngine renderengine) {
	    BufferedImage bufferedimage;
	    this.FONT_HEIGHT = 9; //9
	    this.FONT_WIDTH = 6; //6
	    this.charWidth = new int[256];
	    this.fontTextureName = 0;
	    this.buffer = GLAllocation.createDirectIntBuffer(1024);
	    this.field_41064_c = new Random();
	    try {
	      bufferedimage = ImageIO.read(RenderEngine.class.getResourceAsStream(s));
	    } catch (IOException ioexception) {
	      throw new RuntimeException(ioexception);
	    } 
	    int i = bufferedimage.getWidth();
	    int j = bufferedimage.getHeight();
	    int[] ai = new int[i * j];
	    bufferedimage.getRGB(0, 0, i, j, ai, 0, i);
	    for (int k = 0; k < 256; k++) {
	      int l = k % 16;
	      int k1 = k / 16;
	      int j2 = 7;
	      while (j2 >= 0) {
	        int i3 = l * 8 + j2;
	        boolean flag = true;
	        for (int l3 = 0; l3 < 8 && flag; l3++) {
	          int i4 = (k1 * 8 + l3) * i;
	          int k4 = ai[i3 + i4] & 0xFF;
	          if (k4 > 0)
	            flag = false; 
	        } 
	        if (!flag)
	          break; 
	        j2--;
	      } 
	      if (k == 32)
	        j2 = 2; 
	      this.charWidth[k] = j2 + 2;
	    } 
	    this.fontTextureName = renderengine.allocateAndSetupTexture(bufferedimage);
	    this.fontDisplayLists = GLAllocation.generateDisplayLists(288);
	    Tessellator tessellator = Tessellator.instance;
	    for (int i1 = 0; i1 < 256; i1++) {
	      int startSpace = (this.FONT_WIDTH - this.charWidth[i1]) / 2;
	      GL11.glNewList(this.fontDisplayLists + i1, 4864);
	      GL11.glTranslatef(startSpace, 0.0F, 0.0F);
	      tessellator.startDrawingQuads();
	      int l1 = i1 % 16 * 8;
	      int k2 = i1 / 16 * 8;
	      float f = 7.99F;
	      float f1 = 0.0F;
	      float f2 = 0.0F;
	      tessellator.addVertexWithUV(0.0D, (0.0F + f), 0.0D, (l1 / 128.0F + f1), ((k2 + f) / 128.0F + f2));
	      tessellator.addVertexWithUV((0.0F + f), (0.0F + f), 0.0D, ((l1 + f) / 128.0F + f1), ((k2 + f) / 128.0F + f2));
	      tessellator.addVertexWithUV((0.0F + f), 0.0D, 0.0D, ((l1 + f) / 128.0F + f1), (k2 / 128.0F + f2));
	      tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (l1 / 128.0F + f1), (k2 / 128.0F + f2));
	      tessellator.draw();
	      GL11.glTranslatef((this.FONT_WIDTH - startSpace), 0.0F, 0.0F);
	      GL11.glEndList();
	    } 
	    for (int j1 = 0; j1 < 32; j1++) {
	      int i2 = (j1 >> 3 & 0x1) * 85;
	      int l2 = (j1 >> 2 & 0x1) * 170 + i2;
	      int j3 = (j1 >> 1 & 0x1) * 170 + i2;
	      int k3 = (j1 >> 0 & 0x1) * 170 + i2;
	      if (j1 == 6)
	        l2 += 85; 
	      boolean flag1 = (j1 >= 16);
	      if (gamesettings.anaglyph) {
	        int j4 = (l2 * 30 + j3 * 59 + k3 * 11) / 100;
	        int l4 = (l2 * 30 + j3 * 70) / 100;
	        int i5 = (l2 * 30 + k3 * 70) / 100;
	        l2 = j4;
	        j3 = l4;
	        k3 = i5;
	      } 
	      if (flag1) {
	        l2 /= 4;
	        j3 /= 4;
	        k3 /= 4;
	      } 
	      GL11.glNewList(this.fontDisplayLists + 256 + j1, 4864);
	      GL11.glColor3f(l2 / 255.0F, j3 / 255.0F, k3 / 255.0F);
	      GL11.glEndList();
	    } 
	  }
	  
	  public void drawString(String s, int i, int j, int k) {
	    renderString(s, i, j, k);
	  }
	  
	  public void renderString(String s, int i, int j, int k) {
	    if (s == null)
	      return; 
	    GL11.glBindTexture(3553, this.fontTextureName);
	    float f = (k >> 16 & 0xFF) / 255.0F;
	    float f1 = (k >> 8 & 0xFF) / 255.0F;
	    float f2 = (k & 0xFF) / 255.0F;
	    float f3 = (k >> 24 & 0xFF) / 255.0F;
	    if (f3 == 0.0F)
	      f3 = 1.0F; 
	    GL11.glColor4f(f, f1, f2, f3);
	    this.buffer.clear();
	    GL11.glPushMatrix();
	    GL11.glTranslatef(i, j, 0.0F);
	    for (int i1 = 0; i1 < s.length(); i1++) {
	      if (i1 < s.length()) {
	        int j1 = ChatAllowedCharacters.allowedCharacters.indexOf(s.charAt(i1));
	        if (j1 < 0)
	          j1 = ChatAllowedCharacters.allowedCharacters.indexOf('?'); 
	        if (j1 >= 0)
	          this.buffer.put(this.fontDisplayLists + j1 + 32); 
	      } 
	      if (this.buffer.remaining() == 0) {
	        this.buffer.flip();
	        GL11.glCallLists(this.buffer);
	        this.buffer.clear();
	      } 
	    } 
	    this.buffer.flip();
	    GL11.glCallLists(this.buffer);
	    GL11.glPopMatrix();
	  }
	  
	  public int getStringWidth(String s) {
	    if (s == null)
	      return 0; 
	    int i = 0;
	    for (int j = 0; j < s.length(); j++) {
	      if (/*s.charAt(j) == '§'*/false) {
	        j++;
	      } else {
	        i += this.FONT_WIDTH;
	      } 
	    } 
	    return i;
	  }
	}
