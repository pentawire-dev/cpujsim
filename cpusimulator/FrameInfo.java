package cpusimulator;

import cpusimulator.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.text.*;

public class FrameInfo extends JFrame
 {
  public String      title;
  public FrameMain   parent;
  public FrameAscii  ascii;
  public FrameAbout  about;
  public JScrollPane scroll;
  public JTextPane   text_info;
  public JButton     button_ascii,button_about;

  public FrameInfo(FrameMain parent, String title, int w, int h)
   {
	super(title);

    this.title                   = title;
    this.parent                  = parent;

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setLayout(null);
    setResizable(false);

    ascii = new FrameAscii(parent,"ASCII Table",680,480);
    about = new FrameAbout(parent,"About",      360,200);

    // Add GUI
    text_info = new JTextPane();
    text_info.setFont(parent.font_mono);
    text_info.setEditable(false);
    scroll    = new JScrollPane(text_info);
    scroll.setBounds(5,5,w-5,h-10-30);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    add(scroll);
    button_ascii = new JButton("ASCII table"); button_ascii.setBounds(16,       407, 300, 30); button_ascii.setFont(parent.font_small); add(button_ascii);
    button_about = new JButton("About");       button_about.setBounds(w-16-300, 407, 300, 30); button_about.setFont(parent.font_small); add(button_about);

    // Init info
    try                { text_info.setContentType("text/html");  text_info.setPage(new File(FrameMain.DATA_PATH + "instructions.html").toURI().toURL()); }
    catch(Exception e) { text_info.setContentType("text/plain"); text_info.setText("HTML file load error!");                                             }

    setVisible(true);
    setSize(w + getInsets().left + getInsets().right, h + getInsets().top + getInsets().bottom);
    setLocationRelativeTo(parent);
    setAlwaysOnTop(true);
    setVisible(false);

    // Events
    button_ascii.addActionListener(e -> { ascii.setVisible(true); });
    button_about.addActionListener(e -> { about.setVisible(true); });
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
