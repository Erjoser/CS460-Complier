package AST;
import java.util.*;
import Instruction.Instruction;
/**
 * A ClassBodyDecl is a super class for one of 4 different nodes: {@link FieldDecl}, {@link StaticInitDecl}, {@link MethodDecl}, and {@link ConstructorDecl}.
 */
public abstract class ClassBodyDecl extends AST {

    /**
     * A vector of strings that contain the .var statements.
     */
    public Vector<String> vars = new Vector<String>();
   
    /**
     * A Vector of instructions for this method/constructor/static initializer. 
     * Set in CodeGenerator.java by a call to setCode().
     */
    private Vector<Instruction> code;

    /**
     * The number of 32-bit wide words used by this
     * method/constructor/static initializer.
     */
    public int localsUsed = 1;
    
    /**
     * Mapping between Variable addresses and their VarDecls. Set by
     * AllocateAddresses.java. Not used by the compiler at the moment,
     * but can be useful for generating extra information for an IDE.
     */    
    public Hashtable<Integer, VarDecl> varNames; 

    /**
     * Constructs a ClassBodyDecl node.
     * @param a A parse-tree node.
     */
    public ClassBodyDecl(AST a) {
	super(a);
	varNames = new Hashtable<Integer,VarDecl>();
    }

    /**
     * Returns true if the declaration is static. 
     * @return true is the declaration is static.
     */
    public abstract boolean isStatic() ;

    /**
     * Returns the name of the declaration.
     * @return The name of the declaration.
     */
    public abstract String getname() ;

    /**
     * Sets the code field.
     * @param code A Vector of instructions.
     */
    public void setCode(Vector<Instruction> code) {
	this.code = code;
    }

    /**
     * Returns the Vector of instructions.
     * @return A Vector of instructions.
     */
    public Vector<Instruction> getCode() {
	return code;
    }

}
