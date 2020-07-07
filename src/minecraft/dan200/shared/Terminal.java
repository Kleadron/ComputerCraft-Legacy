/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2020. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.shared;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Stack;

import net.minecraft.src.mod_ComputerCraft;

public class Terminal {
  private int m_cursorX;
  private int m_cursorY;
  private boolean m_cursorBlink;
  private int m_width;
  private int m_height;
  private String m_emptyLine;
  private String[] m_lines;
  private boolean m_changed;
  private boolean[] m_lineChanged;
  
  private boolean m_bitmapEnabled;
  private BufferedImage m_bitmapScreen;
  private Graphics m_graphics;
  private int m_bitmapWidth;
  private int m_bitmapHeight;
  private Stack<Integer> m_coordStack = new Stack<Integer>();
  
  public Terminal(int width, int height) {
    this.m_width = width;
    this.m_height = height;
    StringBuilder emptyLine = new StringBuilder();
    int i;
    for (i = 0; i < this.m_width; i++)
      emptyLine.append(' '); 
    this.m_emptyLine = emptyLine.toString();
    this.m_lines = new String[this.m_height];
    for (i = 0; i < this.m_height; i++)
      this.m_lines[i] = this.m_emptyLine; 
    this.m_cursorX = 0;
    this.m_cursorY = 0;
    this.m_cursorBlink = false;
    this.m_changed = false;
    this.m_lineChanged = new boolean[this.m_height];
    this.m_bitmapEnabled = false;
    m_bitmapWidth = m_width*mod_ComputerCraft.fixedWidthFontRenderer.FONT_WIDTH;
    m_bitmapHeight = m_height*mod_ComputerCraft.fixedWidthFontRenderer.FONT_HEIGHT;
    m_bitmapScreen = new BufferedImage(m_bitmapWidth, m_bitmapHeight, BufferedImage.TYPE_INT_RGB);
    m_graphics = m_bitmapScreen.getGraphics();
	m_graphics.setColor(new Color(mod_ComputerCraft.terminal_textColour_r, mod_ComputerCraft.terminal_textColour_g, mod_ComputerCraft.terminal_textColour_b));
    clearBitmap();
  }
  
  public void destroy() {
	  m_graphics.dispose();
  }
  
  public int getWidth() {
    return this.m_width;
  }
  
  public int getHeight() {
    return this.m_height;
  }
  
  public void setCursorPos(int x, int y) {
    if (this.m_cursorX != x || this.m_cursorY != y) {
      this.m_cursorX = x;
      this.m_cursorY = y;
      this.m_changed = true;
    } 
  }
  
  public void setCursorBlink(boolean blink) {
    if (this.m_cursorBlink != blink) {
      this.m_cursorBlink = blink;
      this.m_changed = true;
    } 
  }
  
  public int getCursorX() {
    return this.m_cursorX;
  }
  
  public int getCursorY() {
    return this.m_cursorY;
  }
  
  public boolean getCursorBlink() {
    return this.m_cursorBlink;
  }
  
  public void write(String string) {
    if (this.m_cursorY >= 0 && this.m_cursorY < this.m_height) {
      string = string.replace('\t', ' ');
      string = string.replace('\r', ' ');
      int writeX = this.m_cursorX;
      int spaceLeft = this.m_width - this.m_cursorX;
      if (spaceLeft > this.m_width + string.length())
        return; 
      if (spaceLeft > this.m_width) {
        writeX = 0;
        string = string.substring(spaceLeft - this.m_width);
        spaceLeft = this.m_width;
      } 
      if (spaceLeft > 0) {
        String oldLine = this.m_lines[this.m_cursorY];
        StringBuilder newLine = new StringBuilder();
        newLine.append(oldLine.substring(0, writeX));
        if (string.length() < spaceLeft) {
          newLine.append(string);
          newLine.append(oldLine.substring(writeX + string.length()));
        } else {
          newLine.append(string.substring(0, spaceLeft));
        } 
        this.m_lines[this.m_cursorY] = newLine.toString();
        this.m_changed = true;
        this.m_lineChanged[this.m_cursorY] = true;
      } 
    } 
  }
  
  public void scroll(int yDiff) {
    String[] newLines = new String[this.m_height];
    for (int y = 0; y < this.m_height; y++) {
      int oldY = y + yDiff;
      if (oldY >= 0 && oldY < this.m_height) {
        newLines[y] = this.m_lines[oldY];
      } else {
        newLines[y] = this.m_emptyLine;
      } 
      if (!newLines[y].equals(this.m_lines[y]))
        this.m_lineChanged[y] = true; 
    } 
    this.m_lines = newLines;
    this.m_changed = true;
  }
  
  public void clear() {
    for (int y = 0; y < this.m_height; y++) {
      if (!this.m_lines[y].equals(this.m_emptyLine)) {
        this.m_lines[y] = this.m_emptyLine;
        this.m_lineChanged[y] = true;
        this.m_changed = true;
      } 
    } 
  }
  
  public void clearLine() {
    if (this.m_cursorY >= 0 && this.m_cursorY < this.m_height && 
      !this.m_lines[this.m_cursorY].equals(this.m_emptyLine)) {
      this.m_lines[this.m_cursorY] = this.m_emptyLine;
      this.m_lineChanged[this.m_cursorY] = true;
      this.m_changed = true;
    } 
  }
  
  public String getLine(int y) {
    assert y >= 0 && y < this.m_height;
    return this.m_lines[y];
  }
  
  public String[] getLines() {
    return this.m_lines;
  }
  
  public boolean getChanged() {
    return this.m_changed;
  }
  
  public boolean[] getLinesChanged() {
    return this.m_lineChanged;
  }
  
  public void clearChanged() {
    if (this.m_changed) {
      this.m_changed = false;
      for (int y = 0; y < this.m_height; y++)
        this.m_lineChanged[y] = false; 
    } 
  }
  
  //bitmap ahead
  
  public void setBitmapMode(boolean enable) {
	  m_bitmapEnabled = enable;
  }
  public boolean getBitmapMode() {
	  return m_bitmapEnabled;
  }
  
  public BufferedImage getScreenBuffer() {
	  return m_bitmapScreen;
  }
  
  public void setPixel() {
	  int x = m_coordStack.pop() - 1;
	  int y = m_coordStack.pop() - 1;
	  m_graphics.fillRect(x, y, 1, 1);
  }
  public void clearPixel() {
	  int x = m_coordStack.pop() - 1;
	  int y = m_coordStack.pop() - 1;
	  m_graphics.clearRect(x, y, 1, 1);
  }
  
  public void pushCoord(int x, int y) {
	  m_coordStack.push(y);
	  m_coordStack.push(x);
  }
  //rects
  public void fillRect() {
	  int w = m_coordStack.pop();
	  int h = m_coordStack.pop();
	  int x = m_coordStack.pop() - 1;
	  int y = m_coordStack.pop() - 1;
	  m_graphics.fillRect(x, y, w, h);  }
  public void clearRect() {
	  int w = m_coordStack.pop();
	  int h = m_coordStack.pop();
	  int x = m_coordStack.pop() - 1;
	  int y = m_coordStack.pop() - 1;
	  m_graphics.clearRect(x, y, w, h);
  }
  public void drawRect() {
	  int w = m_coordStack.pop();
	  int h = m_coordStack.pop();
	  int x = m_coordStack.pop() - 1;
	  int y = m_coordStack.pop() - 1;
	  m_graphics.drawRect(x, y, w, h);
  }/*
  public void drawRoundRect() {
	  m_graphics.drawRoundRect(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop());
  }
  public void fillRoundRect() {
	  m_graphics.fillRoundRect(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop());
  }
  public void draw3DRectUnraised() {
	  m_graphics.draw3DRect(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), false);
  }
  public void draw3DRectRaised() {
	  m_graphics.draw3DRect(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), true);
  }
  public void fill3DRectUnraised() {
	  m_graphics.fill3DRect(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), false);
  }
  public void fill3DRectRaised() {
	  m_graphics.fill3DRect(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), true);
  }
  */
  public void copyArea() {
	  int dx = m_coordStack.pop();
	  int dy = m_coordStack.pop();
	  int w = m_coordStack.pop();
	  int h = m_coordStack.pop();
	  int x = m_coordStack.pop() - 1;
	  int y = m_coordStack.pop() - 1;
	  m_graphics.copyArea(x, y, w, h, dx, dy);
  }
  
  //lines arcs
  public void drawLine() {
	  int x2 = m_coordStack.pop() - 1;
	  int y2 = m_coordStack.pop() - 1;
	  int x1 = m_coordStack.pop() - 1;
	  int y1 = m_coordStack.pop() - 1;
	  m_graphics.drawLine(x1, y1, x2, y2);
  }
  /*public void drawArc() {
	  m_graphics.drawArc(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop());
  }
  public void fillArc() {
	  m_graphics.fillArc(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop());
  }
  
  //rounds
  public void drawOval() {
	  m_graphics.drawOval(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop());
  }
  public void fillOval() {
	  m_graphics.fillOval(m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop(), m_coordStack.pop());
  }
  */
  public void drawString(String string) {
	  int x = m_coordStack.pop();
	  int y = m_coordStack.pop();
	  m_graphics.drawString(string, x, y);
  }
  
  public int getBitmapWidth() {
	  return m_bitmapWidth;
  }
  public int getBitmapHeight() {
	  return m_bitmapHeight;
  }
  
  public void clearBitmap() {
	  m_graphics.clearRect(0, 0, m_bitmapWidth, m_bitmapHeight);
  }
}
