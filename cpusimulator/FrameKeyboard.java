package cpusimulator;

import cpusimulator.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class FrameKeyboard extends JFrame
 {
  public String            title;
  public FrameMain         parent;
  public JButton           send;
  public JTextField        data;
  public JLabel            label;
  public JLabel            label1,label2,label3;
  public JComboBox<String> combo_modes;
  public String[]          modes_labels;
  private Thread           refresh_thread;

  public int keyboard_buffer_address;
  public int mode;
  
  public FrameKeyboard(FrameMain parent, String title, int w, int h, int keyboard_buffer_address)
   {
	super(title);
    
    this.title                   = title;
    this.parent                  = parent;
    this.keyboard_buffer_address = keyboard_buffer_address;

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setLayout(null);
    setResizable(false);

    // Init keyboard
    mode = 0;
        
    // Add GUI
    label1       = parent.createLabel("Send data to RAM via Keyboard",                       7,-10,252,40,parent.font_small); add(label1);
    label2       = parent.createLabel("Keyboard buffer address #" + keyboard_buffer_address,13,108,252,40,parent.font_small); add(label2);
    label3       = parent.createLabel("        [00000000]       "                          ,13,128,252,40,parent.font_mono);  add(label3);
    modes_labels = new String[] { "Read Character", "Read Integer" };
    send         = new JButton("Send"); send.setBounds(38,168,180,30); send.setFont(parent.font); add(send);
    data         = parent.createRegister              (32, 70,196,36,parent.font, true);          add(data);
    label        = parent.createLabel("Read mode:",    10, 30, 96,30,parent.font_small);          add(label);
    combo_modes  = new JComboBox<String>(modes_labels); combo_modes.setBounds(108,30,138,30);     add(combo_modes);
    
    // Add backgroung
    JLabel bg = new JLabel(new ImageIcon(FrameMain.DATA_PATH + "keyboard.png"));
    bg.setBounds(0, 0, w, h);
    add(bg);
    
    setVisible(true);
    setSize(w + getInsets().left + getInsets().right, h + getInsets().top + getInsets().bottom);
    setLocationRelativeTo(parent);
    setAlwaysOnTop(true);
    setVisible(false);

    // Events
    send.addActionListener(e -> { sendPressed(); });
    data.addActionListener(e -> { sendPressed(); });

    // Start thread
    refresh_thread = new Thread() { @Override public void run() { while(true) { FrameMain.sleep(20); Refresh(); } } };
    refresh_thread.start();
   }

  // Buttons events  
  public void sendPressed()
   {
	int value = 0;
	
	if(data.getText().equals("")) return;
	mode      = combo_modes.getSelectedIndex();
	if(mode == 0) value = (int) data.getText().charAt(0);
	if(mode == 1) try { value = Integer.parseInt(data.getText()); } catch(NumberFormatException e)  { }
	data.setText("");
	parent.ram.Write(keyboard_buffer_address, "" + value);
   }

  // Refresh thread
  private void Refresh() { updateBufferAddress(); }

  private void updateBufferAddress()
   {
    int value = 0;
	try { value = Integer.parseInt(parent.ram.Read(keyboard_buffer_address)); }
    catch(NumberFormatException e) { }
    catch(NullPointerException e)  { }
    String bin   = String.format("%8s", Integer.toBinaryString(value)).replace(' ', '0');
    label3.setText("        [" + bin + "]       ");   
   }

 }
