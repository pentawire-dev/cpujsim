package cpusimulator.components;

import cpusimulator.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class RAM
 {
  public JTextField[] TxtRAM;
  public JLabel[]     LblRAM;
  public int bytes;

  public RAM(int bytes)
   {
    TxtRAM = new JTextField[bytes];
    LblRAM = new JLabel[bytes];
    this.bytes = bytes;
   }

  public void Attach(FrameMain frame)
   {
    // Add RAM
    int x = 2;
    int y = 16;
    int w = 110;
    int h = 22;
    for(int i = 0; i < bytes;     i++)
     {
	  LblRAM[i] = frame.createLabel   (frame.formatAddress(i,3), x + (i/32)*w     , y+(i%32*h), 30, h, frame.font_tiny);        frame.add(LblRAM[i]);
	  TxtRAM[i] = frame.createRegister(                          x + (i/32)*w + 30, y+(i%32*h), 80, h, frame.font_small, true); frame.add(TxtRAM[i]);
	 }
    for(int i = frame.screen.base_address; i < frame.screen.base_address + frame.screen.size; i++)
     {
	  setLabelColors(i,null,Color.ORANGE);
	  LblRAM[i].setOpaque(true);
	 }
	setLabelColors(frame.screen.color_address,            null,Color.YELLOW); LblRAM[frame.screen.color_address].            setOpaque(true);
    setLabelColors(frame.keyboard.keyboard_buffer_address,null,Color.GREEN);  LblRAM[frame.keyboard.keyboard_buffer_address].setOpaque(true);
    setLabelColors(frame.speaker.sound_buffer_address,    null,Color.CYAN);   LblRAM[frame.speaker.sound_buffer_address].    setOpaque(true);
    
    // Events
    //reset.addActionListener (e -> { resetPressed();  });
   }

  public void loadProgram(String prg[])
   {
	int address = 0;
    for(int i = 0; i < prg.length; i++) { Write(address,prg[i]); address++; }
   }

  public void Clear() { for(int i = 0; i < bytes; i++) Write(i,""); } // Clear RAM
  
  public String Read(int address)               { return TxtRAM[address].getText(); }
  
  public void  Write(int address, String value) { TxtRAM[address].setText(value);   }

  public int ReadInt(int address) { int v; try { v = Integer.parseInt(Read(address)); } catch(Exception e) { v = 0; } return v; }
  
  public void setTextColors (int address, Color fg, Color bg) {
	                                                           if(fg != null) TxtRAM[address].setForeground(fg);
	                                                           if(bg != null) TxtRAM[address].setBackground(bg);
	                                                          }
  
  public void setLabelColors(int address, Color fg, Color bg) {
	                                                           if(fg != null) LblRAM[address].setForeground(fg);
	                                                           if(bg != null) LblRAM[address].setBackground(bg);
	                                                          }
 }
