package cpusimulator;

import cpusimulator.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.text.*;
import java.awt.event.*;

public class FrameAbout extends JFrame
 {
  public String    title;
  public FrameMain parent;
  public JButton   button_close;
  public JLabel    label1,label2;
  public JLabel    link1,link2,link3;

  public FrameAbout(FrameMain parent, String title, int w, int h)
   {
	super(title);

    this.title  = title;
    this.parent = parent;

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setLayout(null);
    setResizable(false);

    // Add GUI
    String url1 = "https://pentawire.altervista.org";
    String url2 = "https://www.youtube.com/@penta_video";
    String url3 = "https://github.com/pentawire-dev/cpujsim";
    String text = "                       CPUj Sim: " + FrameMain.VER + "                         ";
    String desc =  "<html>A generic CPU simulator written in Java for educational<br/>purposes.<br/>";
           desc += "You can simulate RAM, CPU with general purpose registers, pointer registers and stack and also input output devices (audio, video and keyboard).";
           desc += "</html>";
    label1       = parent.createLabel(text, 5, -3,w- 5,30,parent.font_small); add(label1);
    label2       = parent.createLabel(desc,24, 22,w-24,80,parent.font_tiny);  add(label2);
    link1        = parent.createLabel(url1,24,108,190,16,parent.font_tiny);   add(link1);  link1.setCursor(new Cursor(Cursor.HAND_CURSOR)); link1.setForeground(Color.BLUE);
    link2        = parent.createLabel(url2,24,128,250,16,parent.font_tiny);   add(link2);  link2.setCursor(new Cursor(Cursor.HAND_CURSOR)); link2.setForeground(Color.BLUE);
    link3        = parent.createLabel(url3,24,148,256,16,parent.font_tiny);   add(link3);  link3.setCursor(new Cursor(Cursor.HAND_CURSOR)); link3.setForeground(Color.BLUE);
    button_close = new JButton("Close"); button_close.setBounds(90, 172, 180, 26); button_close.setFont(parent.font_small); add(button_close);

    setVisible(true);
    setSize(w + getInsets().left + getInsets().right, h + getInsets().top + getInsets().bottom);
    setLocationRelativeTo(parent);
    setAlwaysOnTop(true);
    setVisible(false);

    // Events
    button_close.addActionListener(e -> { this.setVisible(false); });
    link1.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { FrameMain.openURL(url1); } });
    link2.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { FrameMain.openURL(url2); } });
    link3.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { FrameMain.openURL(url3); } });
   }

 }
