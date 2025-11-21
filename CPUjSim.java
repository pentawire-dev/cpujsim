// CPUj Sim
//
// https://pentawire.altervista.org/
// https://www.youtube.com/@penta_video
//

import cpusimulator.*;

class CPUjSim
 {
  public static void main(String args[])
   {
	// To change style: try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { } and put: import javax.swing.*;
	String[] labels = {"Sum++", "Loop50", "Multiplication (increments)", "Multiplication (sums)", "Positive square", "Read chars", "All chars", "Print Hello!"};
	String[][] programs = {
		                   /* Sum++                       */ {"SetA","10","SetB","20","ADD","IncA","WriteA","#10"},
		                   /* Loop50                      */ {"ReadA","#8","DecA","JZ","#7","JMP","#2","","50"},
		                   /* Multiplication (increments) */ {"ReadA","#29","JZ","#26","ReadA","#30","JZ","#26","ReadB","#29","DecB","JZ","#23","WriteB","#29","ReadB","#30","IncA","DecB","JNZ","#17","JMP","#8","WriteA","#31","","WriteA","#31","","6","3"},
		                   /* Multiplication (sums)       */ {"ReadA","#32","JZ","#26","ReadB","#33","JZ","#23","DecB","JZ","#20","WriteB","#33","ReadB","#32","ADD","JMP","#4","","","WriteA","#34","","WriteB","#34","","","","","","","","6","3"},
		                   /* Positive square             */ {"ReadA","#32","JZ","#26","ReadB","#32","DecB","JZ","#27","WriteA","#24","ReadB","#24","DecB","JZ","#27","WriteB","#24","ReadB","#32","ADD","JMP","#11","","","","IncA","WriteA","#33","","","","5"},
		                   /* Read chars                  */ {"ReadA","#102","JZ","#0","WriteB","#102","WriteA","#104","JMP","#0","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","0","65"},
		                   /* All Chars                   */ {"ReadA","#30","WriteA","#104","IncA","WriteA","#30","JMP","#0","","","","","","","","","","","","","","","","","","","","","","33","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","65"},
		                   /* Print Hello!                */ {"ReadA","#26","ReadB","#27","WriteA","#104","WriteB","#105","ReadA","#28","ReadB","#29","WriteA","#106","WriteB","#107","ReadA","#30","ReadB","#31","WriteA","#108","WriteB","#109","","","72","101","108","108","111","33","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","65"}
		                  };
    FrameMain frame = new FrameMain("CPUj Sim " + FrameMain.VER,1280,720,labels,programs);
    frame.loadProgram(programs[0]);
   }
 }
