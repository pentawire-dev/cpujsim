package cpusimulator;

import cpusimulator.components.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.io.*;
import java.net.URI;

public class FrameMain extends JFrame
 {
  public static String VER           = "1.0.0";
  public static String DATA_PATH     = "data/";
  public static String PROGRAMS_PATH = "./programs";
  
  public String current_save_filename = "myfile.rasm";
  public String title;
  
  public RAM ram;
  public CPU cpu;
  
  public FrameScreen   screen;
  public FrameKeyboard keyboard;
  public FrameSpeaker  speaker;
  public FrameInfo     informations;

  private Thread  play_thread;
  private boolean play_thread_running;
    
  public JButton reset,step,play,info,clear;
  public JComboBox<String> combo_speeds;
  public String[] speeds_labels;
  public int[]    speeds_values;
  public JLabel label_speeds;
  public JButton source,load,load_file,save_file;
  public JComboBox<String> combo_programs;
  public String[][] programs;

  public JButton button_screen,button_keyboard,button_speaker;

  public Color lred,cyan;
  public Font font       = new Font("Arial",     Font.PLAIN, 24);
  public Font font_small = new Font("Arial",     Font.PLAIN, 17);
  public Font font_tiny  = new Font("Arial",     Font.BOLD,  13);
  public Font font_mono  = new Font("Monospaced",Font.BOLD,  15);

  public FrameMain(String title, int w, int h, String[] programs_labels, String[][] programs)
   {
	super(title);
    
    this.title      = title;
    this.programs   = programs;

    ram             = new RAM(128);
    cpu             = new CPU(ram);
    screen          = new FrameScreen  (this,"Video output",  320,276,104,24,103);
    keyboard        = new FrameKeyboard(this,"Keyboard input",256,200,102);
    speaker         = new FrameSpeaker (this,"Audio output",  256,256,101);
    informations    = new FrameInfo    (this,"Informations",  650,440);
    lred            = new Color(255,160,160);
    cyan            = new Color(0  ,218,255);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(null);
    setResizable(false);

    ram.Attach(this); // Add RAM
    cpu.Attach(this); // Add CPU
    
    // Add buttons and combobox
    speeds_labels  = new String[] { "Very slow", "Slow", "Normal", "Fast", "Very fast", "Maximum" };
    speeds_values  = new int[]    { 600, 300, 100, 50, 10, 1 };
    reset          = new JButton("Reset");                                    reset.         setBounds(354+88, 114, 186, 36); reset.    setFont(font);      reset.setForeground(Color.RED); add(reset);
    step           = new JButton("IDLE");                                     step.          setBounds(354+88, 152, 186, 36); step.     setFont(font);                                      add(step);
    play           = new JButton(new ImageIcon(DATA_PATH + "icon_play.png")); play.          setBounds(354+88, 190,  48, 48);                                                               add(play);
    label_speeds   = createLabel("Speed:",                                                             404+88, 190,  98, 23,font_small);                                                    add(label_speeds);
    combo_speeds   = new JComboBox<String>(speeds_labels);                    combo_speeds.  setBounds(404+88, 215,  86, 23);                                                               add(combo_speeds);
    info           = new JButton(new ImageIcon(DATA_PATH + "icon_info.png")); info.          setBounds(492+88, 190,  48, 48);                                                               add(info);
    source         = new JButton("Source");                                   source.        setBounds(354+88, 240,  80, 48); source.   setFont(font_tiny);                                 add(source);
    load           = new JButton("Load");                                     load.          setBounds(436+88, 265, 104, 23); load.     setFont(font_tiny);                                 add(load);
    combo_programs = new JComboBox<String>(programs_labels);                  combo_programs.setBounds(436+88, 240, 104, 23);                                                               add(combo_programs);
    load_file      = new JButton("Load file");                                load_file.     setBounds(354+88, 290,  92, 30); load_file.setFont(font_tiny);                                 add(load_file);
    save_file      = new JButton("Save file");                                save_file.     setBounds(448+88, 290,  92, 30); save_file.setFont(font_tiny);                                 add(save_file);
    clear          = new JButton("Clear RAM");                                clear.         setBounds(398+88, 544,  98, 32); clear.    setFont(font_tiny);                                 add(clear);

    button_screen   = new JButton(new ImageIcon(DATA_PATH + "icon_monitor.png"));  button_screen.  setBounds(1228, 565,       48, 48); add(button_screen);
    button_keyboard = new JButton(new ImageIcon(DATA_PATH + "icon_keyboard.png")); button_keyboard.setBounds(1228, 565+52,    48, 48); add(button_keyboard);
    button_speaker  = new JButton(new ImageIcon(DATA_PATH + "icon_speaker.png"));  button_speaker. setBounds(1228, 565+52+52, 48, 48); add(button_speaker);

    // Add backgroung
    JLabel bg = new JLabel(new ImageIcon(DATA_PATH + "layout.png"));
    bg.setBounds(0, 0, 1280, 720);
    add(bg);

    setVisible(true);
    setSize(w + getInsets().left + getInsets().right, h + getInsets().top + getInsets().bottom);
    setLocationRelativeTo(null);

    // Events
    reset.addActionListener    (e -> { resetPressed();    });
    step.addActionListener     (e -> { stepPressed();     });
    play.addActionListener     (e -> { playPressed();     });
    info.addActionListener     (e -> { infoPressed();     });
    source.addActionListener   (e -> { sourcePressed();   });
    load.addActionListener     (e -> { loadPressed();     });
    clear.addActionListener    (e -> { clearPressed();    });
    load_file.addActionListener(e -> { loadFilePressed(); });
    save_file.addActionListener(e -> { saveFilePressed(); });

    button_screen.  addActionListener(e -> { screen.  setVisible(true); });
    button_keyboard.addActionListener(e -> { keyboard.setVisible(true); });
    button_speaker. addActionListener(e -> { speaker .setVisible(true); });

    // Start thread
    play_thread_running = false;
    play_thread = new Thread() { @Override public void run() { while(true) { FrameMain.sleep(speeds_values[combo_speeds.getSelectedIndex()]); if(play_thread_running) stepPressed(); } } }; // TODO speed in sleep(10)
    play_thread.start();
   }

   // Buttons events   
   public void resetPressed()
    {
     cpu.reset();
	 step.setText("FETCH");
	 updateALL();
    }
   
   public void stepPressed()
    {
	 if(     step.getText().equals("IDLE"))    { step.setText(cpu.idle());    }
	 else if(step.getText().equals("FETCH"))   { step.setText(cpu.fetch());   }
	 else if(step.getText().equals("DECODE"))  { step.setText(cpu.decode());  }
	 else if(step.getText().equals("EXECUTE")) { step.setText(cpu.execute()); }
	 updateALL();
    }

   public void playPressed()
    {
	  play_thread_running = !play_thread_running;
	  if(play_thread_running) play.setIcon(new ImageIcon(DATA_PATH + "icon_stop.png")); else play.setIcon(new ImageIcon(DATA_PATH + "icon_play.png"));
    }

   public void infoPressed()   { informations.updateContent(); informations.setVisible(true); }

   public void sourcePressed() { showAlert("Source code",printProgram()); }
   
   public void loadPressed()   { loadProgram(programs[combo_programs.getSelectedIndex()]); }
   
   public void clearPressed()  { ram.Clear(); }

   public void loadFilePressed()
    {
	 String       source       = "";
	 JFileChooser file_chooser = new JFileChooser(PROGRAMS_PATH);

     file_chooser.setDialogTitle("Load a .rasm sourcefile");
     file_chooser.setFileFilter(new FileNameExtensionFilter("Rasm sourcefile", "rasm"));
     int result = file_chooser.showOpenDialog(null);
     
     if(result == JFileChooser.APPROVE_OPTION)
      {
       File file = file_chooser.getSelectedFile();
       try(BufferedReader reader = new BufferedReader(new FileReader(file))) { String line; int l = 0; while((line = reader.readLine()) != null) { if(l==1) source = line; l++; } } // Source code is in second line
       catch(IOException e)                                                  { JOptionPane.showMessageDialog(null,"Error loading file!"); return;                                 }
       source = cutEnds(source);
       String[] lines = source.split(",");
       for(int i = 0; i < lines.length; i++) { lines[i] = cutEnds(lines[i]); }
       loadProgram(lines);
      }
	}
   
   public void saveFilePressed()
    {
	 String content            = printProgram();
	 JFileChooser file_chooser = new JFileChooser(PROGRAMS_PATH);
	 
	 file_chooser.setDialogTitle("Save a .rasm sourcefile");
	 file_chooser.setFileFilter(new FileNameExtensionFilter("Rasm sourcefile", "rasm"));
	 file_chooser.setSelectedFile(new File(current_save_filename));
	 int result = file_chooser.showSaveDialog(null);
	 
	 if(result == JFileChooser.APPROVE_OPTION)
	  {
       File file = file_chooser.getSelectedFile();
       if(!file.getName().toLowerCase().endsWith(".rasm")) { file = new File(file.getAbsolutePath() + ".rasm"); }
       current_save_filename = file.getName();

       if(file.exists()) {
		                  int response = JOptionPane.showConfirmDialog(null,"File already exists. Do you want to overwrite?","Confirm Overwrite",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                          if(response != JOptionPane.YES_OPTION) { saveFilePressed(); return; }
                         }

       try(FileWriter writer = new FileWriter(file)) { writer.write(content); JOptionPane.showMessageDialog(null,"File saved: " + file.getAbsolutePath()); }
       catch(IOException e)                          {                        JOptionPane.showMessageDialog(null,"Error saving file!"); }
      }
	}
   
   // Frame related functions
   private void updateALL()
    {
	 updateRAM();
	 updateCPU();
	}
    
   private void updateRAM()
    {
	 int pc,mar;
	 
	 pc = cpu.getPC();
	 for(int i = 0; i < ram.bytes; i++)
	  if(i==pc) ram.setTextColors(i,null,cyan);
	  else      ram.setTextColors(i,null,Color.LIGHT_GRAY);

	 for(int i = 0; i < ram.bytes; i++)
	  if(i >= cpu.highlight_index && i <= cpu.highlight_index + cpu.highlight_size) ram.setTextColors(i,Color.WHITE,null);
	  else                                                                          ram.setTextColors(i,Color.BLACK,null);

	 mar = cpu.getMAR();
	 for(int i = 0; i < ram.bytes; i++)
	  if(i == mar) ram.setLabelColors(i,Color.RED,  null);
	  else         ram.setLabelColors(i,Color.BLACK,null);
	}
   
   private void updateCPU()
    {
	 int working_clock = cpu.clock - cpu.idle_clock;
	 cpu.CLOCK.setText("Clock cycles: " + working_clock + "/" + cpu.clock + " (idle " + cpu.idle_clock + ")");
	}
    
   public JTextField createRegister(int x, int y, int w, int h, Font font, boolean editable)
    {
	 JTextField reg  = new JTextField();
	 reg.setBounds(x,y,w,h);
	 reg.setFont(font);
	 reg.setEditable(editable);
	 reg.setHorizontalAlignment(JTextField.CENTER);
	 return reg;
	}

   public JLabel createLabel(String text, int x, int y, int w, int h, Font font)
    {
	 JLabel lbl = new JLabel(text);
	 lbl.setBounds(x,y,w,h);
	 lbl.setFont(font);
	 return lbl;
	}
   
   public String formatAddress(int address, int digits) {
	                                                     String f = "0" + digits;
	                                                     return String.format("#%" + f + "d", address); // "X" for hex format
	                                                    }
   public void loadProgram(String p[])
    {
	 ram.Clear();
	 ram.loadProgram(p);
	}

   public String printProgram()
    {
	 String program = "Sourcecode compatible format:\n{";
	 
	 for(int i = 0; i < ram.bytes - 1; i++) program += "\"" + ram.Read(i) + "\",";
	 program += "\"" + ram.Read(ram.bytes - 1) + "\"}";

	 program += "\n\nUser readable format:";
	 for(int i=0; i<ram.bytes; i++)
	  {
	   String v = ram.Read(i);
	   if(isNumeric(v)) { program += " " + v; continue; }
	   if(isAddress(v)) { program += " " + v; continue; }
	   program += "\n" + v;
      }

	 return program;
	}
	
	public boolean isNumeric(String str) { try { Integer.parseInt(str); return true; } catch (NumberFormatException e) { return false; } }
	
	public boolean isAddress(String str) { if(str.equals("")) return false; if(str.charAt(0) == '#') return true; else return false; }

// JOptionPane.showMessageDialog(null, "Operazione completata.", "Messaggio", JOptionPane.PLAIN_MESSAGE);

   public void showAlert(String title, String text)
    {
     JTextArea textArea = new JTextArea(text);
     textArea.setEditable(false);
     textArea.setLineWrap(true);
     textArea.setFont(font_mono);
     textArea.setWrapStyleWord(true);
     JScrollPane scrollPane = new JScrollPane(textArea);
     scrollPane.setPreferredSize(new java.awt.Dimension(600,320));
     JOptionPane.showMessageDialog(null,scrollPane,title,JOptionPane.INFORMATION_MESSAGE);
    }
   
  // Misc functions
  public static void sleep(int ms)               { try { Thread.sleep(ms); } catch(Exception e) { } }
  
  public static String cutEnds(String str)       { return str.substring(1,str.length()-1); }
  
  public static int getBit(int value, int index) { return (value & (1 << index)) >> index; }

  public static void openURL(String url)         { try { Desktop desk = Desktop.getDesktop(); desk.browse(new URI(url)); } catch(Exception ex) { } }

 }
