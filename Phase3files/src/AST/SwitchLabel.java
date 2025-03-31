package AST;
import Utilities.Visitor;

public class SwitchLabel extends AST {

	private boolean isDefault = false;

	// This is a hack so we can get a hold of the labels during the writing of the lookupswitch
	private SwitchGroup mySwitchGroup = null;

        // This is the label in the lookupswitch instruction when switching on strings.
        public String label;
    

	public SwitchLabel(Expression const_expr, boolean def) {
		super(const_expr);
		nchildren = 1;
		children = new AST[] { const_expr };
		isDefault = def;
	}

	public Expression expr()  { return (Expression)children[0]; }

	public boolean isDefault() {
		return isDefault;
	}

	public void setSwitchGroup(SwitchGroup sg) {
		this.mySwitchGroup = sg;
	}
	public SwitchGroup getSwitchGroup() {
		return this.mySwitchGroup;
	}


	/* *********************************************************** */
	/* **                                                       ** */
	/* ** Generic Visitor Stuff                                 ** */
	/* **                                                       ** */
	/* *********************************************************** */

	public Object visit(Visitor v) {
		return v.visitSwitchLabel(this);
	}

}

