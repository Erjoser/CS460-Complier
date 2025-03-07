package AST;
import Utilities.Visitor;
import java.math.BigDecimal;

public class Ternary extends Expression {

	public Ternary(Expression expr, Expression trueBranch, Expression falseBranch) {
		super(expr);
		nchildren = 3;
		children = new AST[] { expr, trueBranch, falseBranch };
	}

	public Expression expr()        { return (Expression)children[0]; }
	public Expression trueBranch()  { return (Expression)children[1]; }
	public Expression falseBranch() { return (Expression)children[2]; }

	public boolean isConstant() {
		return expr().isConstant() &&
				trueBranch().isConstant() &&
				falseBranch().isConstant();
	}

	public Object constantValue() {
	    int i = ((BigDecimal)expr().constantValue()).intValue();
	    if (i==1)
		return trueBranch().constantValue();
	    return falseBranch().constantValue();
	}

	/* *********************************************************** */
	/* **                                                       ** */
	/* ** Generic Visitor Stuff                                 ** */
	/* **                                                       ** */
	/* *********************************************************** */

	public Object visit(Visitor v) {
		return v.visitTernary(this);
	}

}
