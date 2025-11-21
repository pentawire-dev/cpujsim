package cpusimulator.components;

import java.io.*;



public class Instruction
 {
  public enum Type { LOGIC, REGISTER, MEMORY, JUMP, FUNCTION }

  private String label;
  private int size;
  private int clocks;
  private boolean use_alu;
  private Type type;
  private String info;

  public Instruction(String label, int size, int clocks, boolean use_alu, Type type, String info)
   {
    this.label   = label;
    this.size    = size;
    this.clocks  = clocks;
    this.use_alu = use_alu;
    this.type    = type;
    this.info    = info;
    // this.ZF,this.NF ecc...
   }
  
  public String  getLabel()  { return label;   }

  public int     getSize()   { return size;    }
  
  public boolean getUseALU() { return use_alu; }

  public Type    getType()   { return type;    }

  public String getInfo()    { return info + " (clocks: " + clocks + ")"; }
 }
