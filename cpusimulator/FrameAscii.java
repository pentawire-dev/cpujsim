package cpusimulator;

import cpusimulator.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.text.*;

public class FrameAscii extends JFrame
 {
  public String      title;
  public FrameMain   parent;
  public JScrollPane scroll;
  public JTextPane   text_info;

  public FrameAscii(FrameMain parent, String title, int w, int h)
   {
	super(title);

    this.title                   = title;
    this.parent                  = parent;

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setLayout(null);
    setResizable(false);
        
    // Add GUI
    text_info = new JTextPane();
    text_info.setFont(parent.font_mono);
    text_info.setEditable(false);
    scroll    = new JScrollPane(text_info);
    scroll.setBounds(5,5,w-5,h-10);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    add(scroll);

    // Init info
    try                { text_info.setContentType("text/html");  text_info.setPage(new File(FrameMain.DATA_PATH + "ascii.html").toURI().toURL()); }
    catch(Exception e) { text_info.setContentType("text/plain"); text_info.setText("HTML file load error!");                                      }

    setVisible(true);
    setSize(w + getInsets().left + getInsets().right, h + getInsets().top + getInsets().bottom);
    setLocationRelativeTo(parent);
    setAlwaysOnTop(true);
    setVisible(false);
   }

  public void updateContent()
   {
	String text = text_info.getText();
	text = text.replace("${parent.cpu.instructionsInfos()}",                          parent.cpu.instructionsInfos());
    text = text.replace("${parent.screen.base_address}",                         "" + parent.screen.base_address);
    text = text.replace("${parent.screen.base_address + parent.screen.size - 1}","" + (parent.screen.base_address + parent.screen.size - 1));
    text = text.replace("${parent.screen.color_address}",                        "" + parent.screen.color_address);
    text = text.replace("${parent.keyboard.keyboard_buffer_address}",            "" + parent.keyboard.keyboard_buffer_address);
    text = text.replace("${parent.speaker.sound_buffer_address}",                "" + parent.speaker.sound_buffer_address);
    text_info.setText(text);
    text_info.setCaretPosition(0);
   }
 }
