package cpusimulator.components;

import cpusimulator.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class CPU
 {
  public JTextField PC,IR,IRP,PSW;
  public JTextField MAR,MDR;
  public JTextField RA,RB,ALU,RX,RY;
  
  public JLabel LABEL,BUS,CLOCK;
  
  private RAM ram;
  public Vector<Instruction> instructions;
  public  int clock,idle_clock;
  private boolean fetch_parameter;
  
  // Hilight current instruction in RAM
  public int highlight_index;
  public int highlight_size;

  public CPU(RAM ram)
   {
    clock           = 0;
    idle_clock      = 0;
    fetch_parameter = false;
    this.ram        = ram;

    highlight_index = -1;
    highlight_size  = 0;
   }

  public void Attach(FrameMain frame)
   {
    initInstructionSet();
    
    // Add registers
    PC  = frame.createRegister(872,   48, 220, 42, frame.font, false); frame.add(PC);
    IR  = frame.createRegister(872,  108, 110, 42, frame.font, false); frame.add(IR);
    IRP = frame.createRegister(982,  108, 110, 42, frame.font, false); frame.add(IRP); // IR Parameter
    PSW = frame.createRegister(1124, 108, 140, 42, frame.font, false); frame.add(PSW);
    MAR = frame.createRegister(644,  354, 118, 42, frame.font, false); frame.add(MAR);
    MDR = frame.createRegister(644,  469, 118, 42, frame.font, false); frame.add(MDR);
    RA  = frame.createRegister(815,  207, 130, 42, frame.font, false); frame.add(RA);
    RB  = frame.createRegister(1017, 207, 130, 42, frame.font, false); frame.add(RB);
    ALU = frame.createRegister(919,  340, 126, 42, frame.font, false); frame.add(ALU);
    RX  = frame.createRegister(1110, 445, 130, 36, frame.font, false); frame.add(RX);  // Pointer registers
    RY  = frame.createRegister(1110, 501, 130, 36, frame.font, false); frame.add(RY);

    // Add labels
    LABEL = frame.createLabel("",                          1140,398,120,42,frame.font);       frame.add(LABEL);
    BUS   = frame.createLabel("READ",                       508, 30,120,42,frame.font);       frame.add(BUS);
    CLOCK = frame.createLabel("Clock cycles:\n0/0 (idle 0)",760,522,296,42,frame.font_small); frame.add(CLOCK);
   }
   
   public void reset()
    {
     PC.   setText("0");
     IR.   setText("");  IRP.setText("");  PSW.setText("000");
	 MAR.  setText("");  MDR.setText("");
	 RA.   setText("0"); RB. setText("0"); ALU.setText(""); RX.setText("0"); RY.setText("0");
	 LABEL.setText("");
     clock           = 0;
     idle_clock      = 0;
     fetch_parameter = false;

     highlight_index = -1;
     highlight_size  = 0;
	}

   void initInstructionSet()
    {
	 instructions = new Vector<>();

	 instructions.add(new Instruction("SetA",    2,4,false,Instruction.Type.REGISTER,"SetA n: RA = n "));
	 instructions.add(new Instruction("SetB",    2,4,false,Instruction.Type.REGISTER,"SetB n: RB = n "));
	 instructions.add(new Instruction("SetX",    2,4,false,Instruction.Type.REGISTER,"SetX n: RX = n "));
	 instructions.add(new Instruction("SetY",    2,4,false,Instruction.Type.REGISTER,"SetY n: RY = n "));
	 instructions.add(new Instruction("MovAX",   1,3,false,Instruction.Type.REGISTER,"MovAX:  RX = RA"));
	 instructions.add(new Instruction("MovBX",   1,3,false,Instruction.Type.REGISTER,"MovBX:  RX = RB"));
	 instructions.add(new Instruction("MovAY",   1,3,false,Instruction.Type.REGISTER,"MovAY:  RY = RA"));
	 instructions.add(new Instruction("MovBY",   1,3,false,Instruction.Type.REGISTER,"MovBY:  RY = RB"));
	 instructions.add(new Instruction("MovXA",   1,3,false,Instruction.Type.REGISTER,"MovXA:  RA = RX"));
	 instructions.add(new Instruction("MovXB",   1,3,false,Instruction.Type.REGISTER,"MovXB:  RB = RX"));
	 instructions.add(new Instruction("MovYA",   1,3,false,Instruction.Type.REGISTER,"MovYA:  RA = RY"));
	 instructions.add(new Instruction("MovYB",   1,3,false,Instruction.Type.REGISTER,"MovYB:  RB = RY"));
	 instructions.add(new Instruction("MovAB",   1,3,false,Instruction.Type.REGISTER,"MovAB:  RB = RA"));
	 instructions.add(new Instruction("MovBA",   1,3,false,Instruction.Type.REGISTER,"MovBA:  RA = RB"));

	 instructions.add(new Instruction("IncA",    1,3,true,Instruction.Type.LOGIC,"IncA:  RA = RA + 1 "));
	 instructions.add(new Instruction("IncB",    1,3,true,Instruction.Type.LOGIC,"IncB:  RB = RB + 1 "));
	 instructions.add(new Instruction("DecA",    1,3,true,Instruction.Type.LOGIC,"DecA:  RA = RA - 1 "));
	 instructions.add(new Instruction("DecB",    1,3,true,Instruction.Type.LOGIC,"DecB:  RB = RB - 1 "));
	 instructions.add(new Instruction("IncX",    1,3,true,Instruction.Type.LOGIC,"IncX:  RX = RX + 1 "));
	 instructions.add(new Instruction("IncY",    1,3,true,Instruction.Type.LOGIC,"IncY:  RY = RY + 1 "));
	 instructions.add(new Instruction("DecX",    1,3,true,Instruction.Type.LOGIC,"DecX:  RX = RX - 1 "));
	 instructions.add(new Instruction("DecY",    1,3,true,Instruction.Type.LOGIC,"DecY:  RY = RY - 1 "));
	 instructions.add(new Instruction("ADD",     1,3,true,Instruction.Type.LOGIC,"ADD:   RA = RA + RB"));
	 instructions.add(new Instruction("SUB",     1,3,true,Instruction.Type.LOGIC,"SUB:   RA = RA - RB"));
	 instructions.add(new Instruction("AND",     1,3,true,Instruction.Type.LOGIC,"AND:   RA = RA & RB"));
	 instructions.add(new Instruction("OR",      1,3,true,Instruction.Type.LOGIC,"OR:    RA = RA | RB"));
	 instructions.add(new Instruction("XOR",     1,3,true,Instruction.Type.LOGIC,"XOR:   RA = RA ^ RB"));
	 instructions.add(new Instruction("NotA",    1,3,true,Instruction.Type.LOGIC,"NotA:  RA = ~RA    "));
	 instructions.add(new Instruction("NotB",    1,3,true,Instruction.Type.LOGIC,"NotB:  RB = ~RB    "));
	 instructions.add(new Instruction(">>A",     2,4,true,Instruction.Type.LOGIC,"&gt;&gt;A n: RA = RA &gt;&gt; n"));
	 instructions.add(new Instruction(">>B",     2,4,true,Instruction.Type.LOGIC,"&gt;&gt;B n: RB = RB &gt;&gt; n"));
	 instructions.add(new Instruction("<<A",     2,4,true,Instruction.Type.LOGIC,"&lt;&lt;A n: RA = RA &lt;&lt; n"));
	 instructions.add(new Instruction("<<B",     2,4,true,Instruction.Type.LOGIC,"&lt;&lt;B n: RB = RB &lt;&lt; n"));

	 instructions.add(new Instruction("ReadA",   2,4,false,Instruction.Type.MEMORY,"ReadA  #n: RA = RAM[n] "));
	 instructions.add(new Instruction("ReadB",   2,4,false,Instruction.Type.MEMORY,"ReadB  #n: RB = RAM[n] "));
	 instructions.add(new Instruction("ReadAB",  1,3,false,Instruction.Type.MEMORY,"ReadAB:    RA = RAM[RB]"));
	 instructions.add(new Instruction("ReadAX",  1,3,false,Instruction.Type.MEMORY,"ReadAX:    RA = RAM[RX]"));
	 instructions.add(new Instruction("ReadAY",  1,3,false,Instruction.Type.MEMORY,"ReadAY:    RA = RAM[RY]"));
	 instructions.add(new Instruction("ReadBX",  1,3,false,Instruction.Type.MEMORY,"ReadBX:    RB = RAM[RX]"));
	 instructions.add(new Instruction("ReadBY",  1,3,false,Instruction.Type.MEMORY,"ReadBY:    RB = RAM[RY]"));
	 instructions.add(new Instruction("WriteA",  2,4,false,Instruction.Type.MEMORY,"WriteA #n: RAM[n]  = RA"));
	 instructions.add(new Instruction("WriteB",  2,4,false,Instruction.Type.MEMORY,"WriteB #n: RAM[n]  = RB"));
	 instructions.add(new Instruction("WriteAB", 1,3,false,Instruction.Type.MEMORY,"WriteAB:   RAM[RB] = RA"));
	 instructions.add(new Instruction("WriteAX", 1,3,false,Instruction.Type.MEMORY,"WriteAX:   RAM[RX] = RA"));
	 instructions.add(new Instruction("WriteAY", 1,3,false,Instruction.Type.MEMORY,"WriteAY:   RAM[RY] = RA"));
	 instructions.add(new Instruction("WriteBX", 1,3,false,Instruction.Type.MEMORY,"WriteBX:   RAM[RX] = RB"));
	 instructions.add(new Instruction("WriteBY", 1,3,false,Instruction.Type.MEMORY,"WriteBY:   RAM[RY] = RB"));

	 instructions.add(new Instruction("JMP",     2,4,false,Instruction.Type.JUMP,"JMP #n: PC = n          "));
	 instructions.add(new Instruction("JZ",      2,4,false,Instruction.Type.JUMP,"JZ  #n: PC = n (if ZF=1)"));
	 instructions.add(new Instruction("JNZ",     2,4,false,Instruction.Type.JUMP,"JNZ #n: PC = n (if ZF=0)"));
	 instructions.add(new Instruction("JS",      2,4,false,Instruction.Type.JUMP,"JS  #n: PC = n (if SF=1)"));
	 instructions.add(new Instruction("JNS",     2,4,false,Instruction.Type.JUMP,"JNS #n: PC = n (if SF=0)"));
	 
	 instructions.add(new Instruction("CALL",    2,4,false,Instruction.Type.FUNCTION,"CALL #n: RY = RY - 1  | RAM[RY] = PC | PC = n"));
	 instructions.add(new Instruction("RET",     1,3,false,Instruction.Type.FUNCTION,"RET:     PC = RAM[RY] | RY = RY + 1          "));
	 instructions.add(new Instruction("PushA",   1,3,false,Instruction.Type.FUNCTION,"PushA:   RY = RY - 1  | RAM[RY] = RA         "));
	 instructions.add(new Instruction("PushB",   1,3,false,Instruction.Type.FUNCTION,"PushB:   RY = RY - 1  | RAM[RY] = RB         "));
	 instructions.add(new Instruction("PushX",   1,3,false,Instruction.Type.FUNCTION,"PushX:   RY = RY - 1  | RAM[RY] = RX         "));
	 instructions.add(new Instruction("PopA",    1,3,false,Instruction.Type.FUNCTION,"PopA:    RA = RAM[RY] | RY = RY + 1          "));
	 instructions.add(new Instruction("PopB",    1,3,false,Instruction.Type.FUNCTION,"PopB:    RB = RAM[RY] | RY = RY + 1          "));
	 instructions.add(new Instruction("PopX",    1,3,false,Instruction.Type.FUNCTION,"PopX:    RX = RAM[RY] | RY = RY + 1          "));
	}
	
   // CPU operations
   public String idle() { String next_status = "IDLE"; clock++; idle_clock++; return next_status; }
   
   public String fetch()
    {
     String	next_status = "FETCH"; if(ram.Read(getPC()).equals("")) { next_status = "IDLE"; highlight_index = -1; highlight_size = 0; return next_status; }

	 clock++; BUS.setText("READ");
	 for(int i = 0; i < instructions.size(); i++)
	  {
	   Instruction inst = instructions.get(i);
	   if(ram.Read(getPC()).equals(inst.getLabel()))
	    {
		 if(!fetch_parameter) {
	                           highlight_index = getPC();
	                           highlight_size  = inst.getSize() - 1;
	                           
		                       MAR.setText("" +     getPC() );
	                           MDR.setText(ram.Read(getPC()));
	                           IR.setText (ram.Read(getPC())); IRP.setText("");
	                           if(inst.getSize() == 2) fetch_parameter = true; else { PC.setText("" + (getPC() + inst.getSize())); next_status = "DECODE"; }
	                          }
	     else          	      {
		                       MAR.setText("" +    (getPC() + 1));
	                           MDR.setText(ram.Read(getPC() + 1));
	                           IRP.setText(ram.Read(getPC() + 1));
		                       fetch_parameter = false;
		                       next_status     = "DECODE";
		                       PC.setText("" + (getPC() + inst.getSize()));
		                      }
		 break;
		}	 
	  }
	 return next_status;
	}

   public String decode()
    {
     String	next_status = "DECODE";
     String op          = IR. getText();
     String par         = IRP.getText();
     
     clock++;
     for(int i = 0; i < instructions.size(); i++)
	  {
	   Instruction inst = instructions.get(i);
	   if(op.equals(inst.getLabel()))
	    {
		 ALU.  setText("");
		 LABEL.setText("");
		 if(inst.getUseALU()) ALU.setText(op); else LABEL.setText(op);
		 if(op.equals("ReadA"))   { MAR.setText("" + addressToInt(par)); MDR.setText(ram.Read(getMAR()));                       }
         if(op.equals("ReadB"))   { MAR.setText("" + addressToInt(par)); MDR.setText(ram.Read(getMAR()));                       }
         if(op.equals("ReadAB"))  { MAR.setText(RB.getText());           MDR.setText(ram.Read(getMAR()));                       }
         if(op.equals("ReadAX"))  { MAR.setText(RX.getText());           MDR.setText(ram.Read(getMAR()));                       }
         if(op.equals("ReadAY"))  { MAR.setText(RY.getText());           MDR.setText(ram.Read(getMAR()));                       }
         if(op.equals("ReadBX"))  { MAR.setText(RX.getText());           MDR.setText(ram.Read(getMAR()));                       }
         if(op.equals("ReadBY"))  { MAR.setText(RY.getText());           MDR.setText(ram.Read(getMAR()));                       }
		 if(op.equals("WriteA"))  { MAR.setText("" + addressToInt(par)); MDR.setText(RA.getText());       BUS.setText("WRITE"); }
         if(op.equals("WriteB"))  { MAR.setText("" + addressToInt(par)); MDR.setText(RB.getText());       BUS.setText("WRITE"); }
         if(op.equals("WriteAB")) { MAR.setText(RB.getText());           MDR.setText(RA.getText());       BUS.setText("WRITE"); }
         if(op.equals("WriteAX")) { MAR.setText(RX.getText());           MDR.setText(RA.getText());       BUS.setText("WRITE"); }
         if(op.equals("WriteAY")) { MAR.setText(RY.getText());           MDR.setText(RA.getText());       BUS.setText("WRITE"); }
         if(op.equals("WriteBX")) { MAR.setText(RX.getText());           MDR.setText(RB.getText());       BUS.setText("WRITE"); }
         if(op.equals("WriteBY")) { MAR.setText(RY.getText());           MDR.setText(RB.getText());       BUS.setText("WRITE"); }
		 next_status = "EXECUTE";
		 break;
        }
      }
	 return next_status;
	}

   public String execute()
    {
	 String	next_status = "EXECUTE";
     String op          = IR. getText();
     String par         = IRP.getText();
	 boolean zf         = false;
	 boolean cf         = false;
	 boolean sf         = false;

	 clock++;
     for(int i = 0; i < instructions.size(); i++)
	  {
	   Instruction inst = instructions.get(i);
	   if(op.equals(inst.getLabel()))
	    {
		 if(op.equals("SetA"))  { RA.setText("" + getIRP()); }
		 if(op.equals("SetB"))  { RB.setText("" + getIRP()); }
		 if(op.equals("SetX"))  { RX.setText("" + getIRP()); }
		 if(op.equals("SetY"))  { RY.setText("" + getIRP()); }
		 if(op.equals("MovAX")) { RX.setText("" + getRA());  }
		 if(op.equals("MovBX")) { RX.setText("" + getRB());  }
		 if(op.equals("MovAY")) { RY.setText("" + getRA());  }
		 if(op.equals("MovBY")) { RY.setText("" + getRB());  }
		 if(op.equals("MovXA")) { RA.setText("" + getRX());  }
		 if(op.equals("MovXB")) { RB.setText("" + getRX());  }
		 if(op.equals("MovYA")) { RA.setText("" + getRY());  }
		 if(op.equals("MovYB")) { RB.setText("" + getRY());  }
		 if(op.equals("MovAB")) { RB.setText("" + getRA());  }
		 if(op.equals("MovBA")) { RA.setText("" + getRB());  }

		 if(op.equals("IncA")) { int v = getRA() + 1;         if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
		 if(op.equals("IncB")) { int v = getRB() + 1;         if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }
		 if(op.equals("DecA")) { int v = getRA() - 1;         if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
		 if(op.equals("DecB")) { int v = getRB() - 1;         if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }
		 if(op.equals("IncX")) { int v = getRX() + 1;         if(v == 0) zf = true; if(v < 0) sf = true; RX.setText("" + v); }
		 if(op.equals("IncY")) { int v = getRY() + 1;         if(v == 0) zf = true; if(v < 0) sf = true; RY.setText("" + v); }
		 if(op.equals("DecX")) { int v = getRX() - 1;         if(v == 0) zf = true; if(v < 0) sf = true; RX.setText("" + v); }
		 if(op.equals("DecY")) { int v = getRY() - 1;         if(v == 0) zf = true; if(v < 0) sf = true; RY.setText("" + v); }
		 if(op.equals("ADD"))  { int v = getRA() + getRB();   if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
		 if(op.equals("SUB"))  { int v = getRA() - getRB();   if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("AND"))  { int v = getRA() & getRB();   if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("OR"))   { int v = getRA() | getRB();   if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("XOR"))  { int v = getRA() ^ getRB();   if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("NotA")) { int v = ~getRA();            if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("NotB")) { int v = ~getRB();            if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }
         if(op.equals(">>A"))  { int v = getRA() >> getIRP(); if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }//TODO CARRY?
         if(op.equals(">>B"))  { int v = getRB() >> getIRP(); if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }//TODO CARRY?
         if(op.equals("<<A"))  { int v = getRA() << getIRP(); if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }//TODO CARRY?
         if(op.equals("<<B"))  { int v = getRB() << getIRP(); if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }//TODO CARRY?

         if(op.equals("ReadA"))   { int v = getMDR();        if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("ReadB"))   { int v = getMDR();        if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }
         if(op.equals("ReadAB"))  { int v = getMDR();        if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("ReadAX"))  { int v = getMDR();        if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("ReadAY"))  { int v = getMDR();        if(v == 0) zf = true; if(v < 0) sf = true; RA.setText("" + v); }
         if(op.equals("ReadBX"))  { int v = getMDR();        if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }
         if(op.equals("ReadBY"))  { int v = getMDR();        if(v == 0) zf = true; if(v < 0) sf = true; RB.setText("" + v); }
         if(op.equals("WriteA"))  { if(isAddress(ram.Read(getMAR()))) ram.Write(getMAR(), "#" + MDR.getText()); else ram.Write(getMAR(), MDR.getText()); }
         if(op.equals("WriteB"))  { if(isAddress(ram.Read(getMAR()))) ram.Write(getMAR(), "#" + MDR.getText()); else ram.Write(getMAR(), MDR.getText()); }
         if(op.equals("WriteAB")) { if(isAddress(ram.Read(getMAR()))) ram.Write(getMAR(), "#" + MDR.getText()); else ram.Write(getMAR(), MDR.getText()); }
         if(op.equals("WriteAX")) { if(isAddress(ram.Read(getMAR()))) ram.Write(getMAR(), "#" + MDR.getText()); else ram.Write(getMAR(), MDR.getText()); }
         if(op.equals("WriteAY")) { if(isAddress(ram.Read(getMAR()))) ram.Write(getMAR(), "#" + MDR.getText()); else ram.Write(getMAR(), MDR.getText()); }
         if(op.equals("WriteBX")) { if(isAddress(ram.Read(getMAR()))) ram.Write(getMAR(), "#" + MDR.getText()); else ram.Write(getMAR(), MDR.getText()); }
         if(op.equals("WriteBY")) { if(isAddress(ram.Read(getMAR()))) ram.Write(getMAR(), "#" + MDR.getText()); else ram.Write(getMAR(), MDR.getText()); }

         if(op.equals("JMP"))  {              PC.setText("" + addressToInt(MDR.getText())); }
         if(op.equals("JZ"))   { if(getZF())  PC.setText("" + addressToInt(MDR.getText())); }
         if(op.equals("JNZ"))  { if(!getZF()) PC.setText("" + addressToInt(MDR.getText())); }
         if(op.equals("JS"))   { if(getSF())  PC.setText("" + addressToInt(MDR.getText())); }
         if(op.equals("JNS"))  { if(!getSF()) PC.setText("" + addressToInt(MDR.getText())); }

         if(op.equals("CALL"))  {
			                     ram.Write(getRY(), PC.getText());
			                     PC.setText("" + addressToInt(IRP.getText()));
			                     int v = getRY() - 1; RY.setText("" + v); if(v == 0) zf = true; if(v < 0) sf = true;
			                    }
         if(op.equals("RET"))   {
			                     int v = getRY() + 1; RY.setText("" + v); if(v == 0) zf = true; if(v < 0) sf = true;
			                     PC.setText(ram.Read(getRY()));
			                    }
         if(op.equals("PushA")) { ram.Write(getRY(), RA.getText()); int v = getRY() - 1; RY.setText("" + v); if(v == 0) zf = true; if(v < 0) sf = true; }
         if(op.equals("PushB")) { ram.Write(getRY(), RB.getText()); int v = getRY() - 1; RY.setText("" + v); if(v == 0) zf = true; if(v < 0) sf = true; }
         if(op.equals("PushX")) { ram.Write(getRY(), RX.getText()); int v = getRY() - 1; RY.setText("" + v); if(v == 0) zf = true; if(v < 0) sf = true; }
         if(op.equals("PopA"))  { int v = getRY() + 1; RY.setText("" + v); RA.setText(ram.Read(getRY()));    if(v == 0) zf = true; if(v < 0) sf = true; }
         if(op.equals("PopB"))  { int v = getRY() + 1; RY.setText("" + v); RB.setText(ram.Read(getRY()));    if(v == 0) zf = true; if(v < 0) sf = true; }
         if(op.equals("PopX"))  { int v = getRY() + 1; RY.setText("" + v); RX.setText(ram.Read(getRY()));    if(v == 0) zf = true; if(v < 0) sf = true; }

	     if(zf) setZF(); else unsetZF();
	     if(cf) setCF(); else unsetCF();
	     if(sf) setSF(); else unsetSF();
		 next_status = "FETCH";
		 break;
        }
      }
	 return next_status;
	}

   public String instructionsInfos()
    {
	 String info = " Registers instructions:\n";                        for(int i = 0; i < instructions.size(); i++) if(instructions.get(i).getType() == Instruction.Type.REGISTER) info += "  " + instructions.get(i).getInfo() + "\n";
	 info += "\n Aritmetic/logic instructions:\n";                      for(int i = 0; i < instructions.size(); i++) if(instructions.get(i).getType() == Instruction.Type.LOGIC)    info += "  " + instructions.get(i).getInfo() + "\n";
	 info += "\n Memory instructions:\n";                               for(int i = 0; i < instructions.size(); i++) if(instructions.get(i).getType() == Instruction.Type.MEMORY)   info += "  " + instructions.get(i).getInfo() + "\n";
	 info += "\n Jump instructions:\n";                                 for(int i = 0; i < instructions.size(); i++) if(instructions.get(i).getType() == Instruction.Type.JUMP)     info += "  " + instructions.get(i).getInfo() + "\n";
	 info += "\n Functions instructions: (RY used as stack pointer)\n"; for(int i = 0; i < instructions.size(); i++) if(instructions.get(i).getType() == Instruction.Type.FUNCTION) info += "  " + instructions.get(i).getInfo() + "\n";
	 return info;
	}
   
   public int getRA()      { int ra;  try { ra  = Integer.parseInt(RA.getText());  } catch(Exception e) { ra  = 0;  } return ra;  }
   
   public int getRB()      { int rb;  try { rb  = Integer.parseInt(RB.getText());  } catch(Exception e) { rb  = 0;  } return rb;  }
   
   public int getRX()      { int rx;  try { rx  = Integer.parseInt(RX.getText());  } catch(Exception e) { rx  = 0;  } return rx;  }
   
   public int getRY()      { int ry;  try { ry  = Integer.parseInt(RY.getText());  } catch(Exception e) { ry  = 0;  } return ry;  }

   public int getIRP()     { int irp; try { irp = Integer.parseInt(IRP.getText()); } catch(Exception e) { irp = 0;  } return irp; }

   public int getPC()      { int pc;  try { pc  = Integer.parseInt(PC.getText());  } catch(Exception e) { pc  = -1; } return pc;  }
   
   public int getMAR()     { int mar; try { mar = Integer.parseInt(MAR.getText()); } catch(Exception e) { mar = -1; } return mar; }
   
   //public int getMDR()     { int mdr; try { mdr = Integer.parseInt(MDR.getText()); } catch(Exception e) { mdr = -1; } return mdr; }
   public int getMDR()     { 
	                        String content = MDR.getText();
	                        if(isAddress(content)) content = "" + addressToInt(content);
	                        int mdr;
	                        try { mdr = Integer.parseInt(content); } catch(Exception e) { mdr = -1; }
	                        return mdr;
	                       }
   
   private boolean getCF() { if(PSW.getText().charAt(0) == '1') return true; return false; }
   
   private boolean getZF() { if(PSW.getText().charAt(1) == '1') return true; return false; }
   
   private boolean getSF() { if(PSW.getText().charAt(2) == '1') return true; return false; }
   
   private void setCF()    { StringBuilder sb = new StringBuilder(PSW.getText()); sb.setCharAt(0,'1'); PSW.setText(sb.toString()); }
   
   private void setZF()    { StringBuilder sb = new StringBuilder(PSW.getText()); sb.setCharAt(1,'1'); PSW.setText(sb.toString()); }
   
   private void setSF()    { StringBuilder sb = new StringBuilder(PSW.getText()); sb.setCharAt(2,'1'); PSW.setText(sb.toString()); }

   private void unsetCF()  { StringBuilder sb = new StringBuilder(PSW.getText()); sb.setCharAt(0,'0'); PSW.setText(sb.toString()); }
   
   private void unsetZF()  { StringBuilder sb = new StringBuilder(PSW.getText()); sb.setCharAt(1,'0'); PSW.setText(sb.toString()); }
   
   private void unsetSF()  { StringBuilder sb = new StringBuilder(PSW.getText()); sb.setCharAt(2,'0'); PSW.setText(sb.toString()); }
   
   private boolean isAddress(String value) { if(value.equals("")) return false; if(value.charAt(0) == '#') return true; else return false; }
   
   private int    addressToInt(String adr  ) { return Integer.parseInt(adr.substring(1)); }
   
   private String intToAddress(int    value) { return "#" + value; }

 }
