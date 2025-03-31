package AST;
import Utilities.Visitor;
import Utilities.SymbolTable;

public class SwitchStat extends Statement {

    /**
     * The symbol table opened by a switch statement
     */
    public SymbolTable mySymbolTable =	null;
    
    public SwitchStat(Expression expr,
		      Sequence /* SwitchGroup */ switchBlocks) {
	super(expr);
	nchildren = 2;
	children = new AST[] { expr, switchBlocks };
    }
    
    public Expression expr()       { return (Expression)children[0]; }
    public Sequence switchBlocks() { return (Sequence)children[1]; }
    
    /* *********************************************************** */
    /* **                                                       ** */
    /* ** Generic Visitor Stuff                                 ** */
    /* **                                                       ** */
    /* *********************************************************** */
    
    public Object visit(Visitor v) {
	return v.visitSwitchStat(this);
    }   
}

