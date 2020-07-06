/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.client;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import dan200.shared.Terminal;
import dan200.shared.TileEntityComputer;
import net.minecraft.src.GLAllocation;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.Tessellator;
import net.minecraft.src.mod_ComputerCraft;

public class GuiComputer extends GuiScreen {
  private TileEntityComputer m_computer;
  private float m_terminateTimer;
  private float m_rebootTimer;
  private float m_shutdownTimer;
  private static int graphicsScreenID = GL11.glGenTextures();
  
  public GuiComputer(TileEntityComputer tileentitycomputer) {
    this.m_computer = tileentitycomputer;
    this.m_terminateTimer = 0.0F;
    this.m_rebootTimer = 0.0F;
  }
  
  public void initGui() {
    super.initGui();
    Keyboard.enableRepeatEvents(true);
    this.m_terminateTimer = 0.0F;
    this.m_rebootTimer = 0.0F;
  }
  
  public void onGuiClosed() {
    super.onGuiClosed();
    Keyboard.enableRepeatEvents(false);
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public void updateScreen() {
    super.updateScreen();
    if (this.m_computer.isDestroyed()) {
      this.mc.displayGuiScreen(null);
      return;
    } 
    if (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)) {
      if (Keyboard.isKeyDown(20)) {
        if (this.m_terminateTimer < 1.0F) {
          this.m_terminateTimer += 0.05F;
          if (this.m_terminateTimer >= 1.0F)
            this.m_computer.terminate(); 
        } 
      } else {
        this.m_terminateTimer = 0.0F;
      } 
      if (Keyboard.isKeyDown(19)) {
        if (this.m_rebootTimer < 1.0F) {
          this.m_rebootTimer += 0.05F;
          if (this.m_rebootTimer >= 1.0F)
            this.m_computer.reboot(); 
        } 
      } else {
        this.m_rebootTimer = 0.0F;
      } 
      if (Keyboard.isKeyDown(31)) {
        if (this.m_shutdownTimer < 1.0F) {
          this.m_shutdownTimer += 0.05F;
          if (this.m_shutdownTimer >= 1.0F)
            this.m_computer.shutdown(); 
        } 
      } else {
        this.m_shutdownTimer = 0.0F;
      } 
    } else {
      this.m_terminateTimer = 0.0F;
      this.m_rebootTimer = 0.0F;
      this.m_shutdownTimer = 0.0F;
    } 
  }
  
  protected void keyTyped(char c, int k) {
    super.keyTyped(c, k);
    if (this.m_terminateTimer < 0.5F && this.m_rebootTimer < 0.5F && this.m_shutdownTimer < 0.5F)
      this.m_computer.keyTyped(c, k); 
  }
  
  public void drawScreen(int i, int j, float f) {
    Terminal terminal = this.m_computer.getTerminal();
    synchronized (terminal) {
      boolean tblink = terminal.getCursorBlink();
      int tw = terminal.getWidth();
      int th = terminal.getHeight();
      int tx = terminal.getCursorX();
      int ty = terminal.getCursorY();
      String[] tlines = terminal.getLines();
      drawDefaultBackground();
      int termWidth = 4 + tw * mod_ComputerCraft.fixedWidthFontRenderer.FONT_WIDTH;
      int termHeight = 4 + th * mod_ComputerCraft.fixedWidthFontRenderer.FONT_HEIGHT;
      int term = this.mc.renderEngine.getTexture("/gui/terminal.png");
      int corners = this.mc.renderEngine.getTexture("/gui/corners.png");
      int vfix = this.mc.renderEngine.getTexture("/gui/vertical_bar_fix.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int startX = (this.width - termWidth) / 2;
      int startY = (this.height - termHeight) / 2;
      int endX = startX + termWidth;
      int endY = startY + termHeight;
      
      this.mc.renderEngine.bindTexture(term);
      drawTexturedModalRect(startX, startY, 0, 0, termWidth, termHeight); //bg
      this.mc.renderEngine.bindTexture(corners);
      
      //drawing border
      drawTexturedModalRect(startX - 12, startY - 12, 12, 28, 12, 12); //top left corner
      drawTexturedModalRect(startX - 12, endY, 12, 40, 12, 16); //bottom left corner
      drawTexturedModalRect(endX, startY - 12, 24, 28, 12, 12); //top right corner
      drawTexturedModalRect(endX, endY, 24, 40, 12, 16); //bottom right corner
      
      drawTexturedModalRect(startX, startY - 12, 0, 0, termWidth, 12); //top bar
      drawTexturedModalRect(startX, endY, 0, 12, termWidth, 16); //bottom bar
      
      this.mc.renderEngine.bindTexture(vfix);
      drawTexturedModalRect(startX - 12, startY, 0, 28, 12, termHeight); //left bar
      drawTexturedModalRect(endX, startY, 36, 28, 12, termHeight); //right bar
       
      if (terminal.getBitmapMode()) {
  		this.mc.renderEngine.setupTexture(terminal.getScreenBuffer(), graphicsScreenID);
  		drawTexturedModalRectClamped(startX+2, startY+2, termWidth-4, termHeight-4);
  	  } else {
  		int textColour = (mod_ComputerCraft.terminal_textColour_r << 16) + (mod_ComputerCraft.terminal_textColour_g << 8) + (mod_ComputerCraft.terminal_textColour_b << 0);
        int x = startX + 2;
        int y = startY + 2;
        for (int line = 0; line < th; line++) {
          String text = tlines[line];
          if (tblink && ty == line && mod_ComputerCraft.getCursorBlink() && 
            tx >= 0 && tx < tw)
          	mod_ComputerCraft.fixedWidthFontRenderer.drawString("_", x + mod_ComputerCraft.fixedWidthFontRenderer.FONT_WIDTH * tx, y, textColour); 
          mod_ComputerCraft.fixedWidthFontRenderer.drawString(text, x, y, textColour);
          y += mod_ComputerCraft.fixedWidthFontRenderer.FONT_HEIGHT;
        } 
  	  }
      
    } 
    super.drawScreen(i, j, f);
  }
  
  public void drawTexturedModalRectClamped(int i, int j, int i1, int j1)
  {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.instance;
      tessellator.startDrawingQuads();
      tessellator.addVertexWithUV(i + 0, j + j1, zLevel, 0, 1);
      tessellator.addVertexWithUV(i + i1, j + j1, zLevel, 1, 1);
      tessellator.addVertexWithUV(i + i1, j + 0, zLevel, 1, 0);
      tessellator.addVertexWithUV(i + 0, j + 0, zLevel, 0, 0);
      tessellator.draw();
  }
}
