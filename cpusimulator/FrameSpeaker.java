package cpusimulator;

import cpusimulator.components.*;

import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class FrameSpeaker extends JFrame
 {
  public  String         title;
  public  FrameMain      parent;
  public  JLabel         label,label1,label2,label3,label4;
  public  JButton        mute;
  private Thread         refresh_thread;
  private Thread         sound_thread;
  private SourceDataLine audio_data;
  private byte[][][]     buffer;

  public  int     sound_buffer_address;
  public  boolean mute_status;
  private int     old_value;
  private boolean restart;

  public FrameSpeaker(FrameMain parent, String title, int w, int h, int sound_buffer_address)
   {
	super(title);
    
    this.title                = title;
    this.parent               = parent;
    this.sound_buffer_address = sound_buffer_address;

    setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    setLayout(null);
    setResizable(false);

    // Init speaker
    mute_status  = false;
    restart      = true;
    old_value    = 0;
    int duration = 1000;
    int size     = (int)(duration * (float)44100 / 1000) - 1;
    try { audio_data = AudioSystem.getSourceDataLine(new AudioFormat((float)44100,8,1,true,false)); audio_data.open(); } catch(Exception e) { }
    buffer = new byte[2][12][size];
    float freq[] = { 130.8f, 138.6f, 146.8f, 155.6f, 164.8f, 174.6f, 185.8f, 196.0f, 207.7f, 220.0f, 233.1f, 246.9f };
    for(int o = 0; o < buffer.length; o++)
     for(int t = 0; t < freq.length; t++)
      for(int i = 0; i < size; i++) { double angle = i / ((float)44100/(freq[t]*Math.pow(2,o))) * 2.0 * Math.PI; buffer[o][t][i] = (byte)(Math.sin(angle)*100); }

    // Add GUI
    label  = parent.createLabel("    Play sound data from RAM    ",              7,-10,252,40,parent.font_small); add(label);
    label1 = parent.createLabel("<html><pre>Tones:<br/> 0  None<br/> 1  C     2  C#    3  D<br/> 4  D#    5  E     6  F<br/> 7  F#    8  G     9  G#<br/> 10 A     11 A#    12 B</pre></html>",4,-42,252,250,parent.font_mono); add(label1);
    label2 = parent.createLabel("Sound buffer address #" + sound_buffer_address,18,144,252,40,parent.font_mono);  add(label2);
    label3 = parent.createLabel("     [L][SP][H][TONE]     "                   ,13,164,252,40,parent.font_mono);  add(label3);
    label4 = parent.createLabel("     [0][00][0][0000]     "                   ,13,184,252,40,parent.font_mono);  add(label4);
    mute   = new JButton("Mute"); mute.setBounds(38,224,180,30); mute.setFont(parent.font); add(mute);

    // Add backgroung
    JLabel bg = new JLabel(new ImageIcon(FrameMain.DATA_PATH + "speaker.png"));
    bg.setBounds(0, 0, w, h);
    add(bg);
    
    setVisible(true);
    setSize(w + getInsets().left + getInsets().right, h + getInsets().top + getInsets().bottom);
    setLocationRelativeTo(parent);
    setAlwaysOnTop(true);
    setVisible(false);

    // Events
    mute.addActionListener(e -> { mutePressed(); });

    // Start thread
    refresh_thread = new Thread() { @Override public void run() { while(true) { FrameMain.sleep(20); Refresh();   } } };
    refresh_thread.start();
    sound_thread = new Thread()   { @Override public void run() { while(true) { FrameMain.sleep(1);  playSound(); } } };
    sound_thread.start();
   }

  // Buttons events  
  public void mutePressed()
   {
	mute_status = ! mute_status;
	if(mute_status) {
		             parent.button_speaker.setIcon(new ImageIcon(FrameMain.DATA_PATH + "icon_speaker_mute.png"));
		             mute.setText("Unmute");
		            }
	else            {
		             parent.button_speaker.setIcon(new ImageIcon(FrameMain.DATA_PATH + "icon_speaker.png"));
		             mute.setText("Mute"); 
		            }
   }

  // Refresh thread
  private void Refresh() { updateBufferAddress(); }
  
  // Sound thread
  private void playSound()
   {
    int value = 0;
	try { value = Integer.parseInt(parent.ram.Read(sound_buffer_address)); }
    catch(NumberFormatException e) { }
    catch(NullPointerException e)  { }
    int loop  = (value & 0x80) >> 7;
    int speed = (value & 0x60) >> 5;
	int oct   = (value & 0x10) >> 4;
	int tone  = (value & 0x0f);
	if(old_value != value) { old_value = value; restart = true; }
	if(tone > 0 && tone < 13) if(!mute_status) if(restart || loop == 1) { Play(restart,loop,speed,oct,tone - 1); restart = false; }
   }

  private void updateBufferAddress()
   {
    int value = 0;
	try { value = Integer.parseInt(parent.ram.Read(sound_buffer_address)); }
    catch(NumberFormatException e) { }
    catch(NullPointerException e)  { }
    int loop  = (value & 0x80) >> 7;
    int speed = (value & 0x60) >> 5;
	int oct   = (value & 0x10) >> 4;
	int tone  = (value & 0x0f);

    String l = String.format("%1s", Integer.toBinaryString(loop)).replace(' ', '0');
    String s = String.format("%2s", Integer.toBinaryString(speed)).replace(' ', '0');
    String h = String.format("%1s", Integer.toBinaryString(oct)) .replace(' ', '0');
    String t = String.format("%4s", Integer.toBinaryString(tone)).replace(' ', '0');
    label4.setText("     [" + l + "][" + s + "][" + h + "][" + t + "]     ");   
   }

  private void Play(boolean restart, int loop, int speed, int oct, int tone)
   {
    try {
         if(loop == 1 || restart) audio_data.start();
         audio_data.write(buffer[oct][tone],0,buffer[0][0].length / (speed + 1));
         if(speed == 0) audio_data.drain();
         if(loop  == 0) audio_data.stop();
        }
    catch(Exception e) { }
   }

 }
