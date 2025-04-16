package Instruction;

/**
 * Used for loading double values
 * 
 * ldc2, ldc2_w
 * 
 */
public class LdcDoubleInstruction extends LdcInstruction {
    private double value;
    private String original_lexeme = "";
    
    public LdcDoubleInstruction(int opCode, double doubleOperand) {
	super(opCode);
	value = doubleOperand;
    }

    public LdcDoubleInstruction(int opCode, double doubleOperand, String original_lexeme) {
        super(opCode);
        value = doubleOperand;
	this.original_lexeme = original_lexeme;
    }
    
    public double getValue() {
	return value;
    }
    
    public String toString() {
	if (original_lexeme.equals(""))
	    return super.toString() + " " + value;
	else {
	    if (original_lexeme.contains("."))
		return super.toString() + " " + original_lexeme;
	    else
		return super.toString() + " " + original_lexeme + ".0";
	}
    }
}
