package Instruction;
/**
 * Used for loading float values:
 * 
 * ldc, ldc_w
 * 
 */
public class LdcFloatInstruction extends LdcInstruction {
    private float value;
    private String original_lexeme = ""; // save the original number for precision - though it seems Jasmin can't handle it.

    public LdcFloatInstruction(int opCode, float floatOperand) {
	super(opCode);
	value = floatOperand;
    }

    public LdcFloatInstruction(int opCode, float floatOperand, String original_lexeme) {
	super(opCode);
	value = floatOperand;
	this.original_lexeme = original_lexeme;
    }
    
    public float getValue() {
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
