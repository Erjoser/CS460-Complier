package TypeChecker;

import AST.*;
import Parser.sym;
import Utilities.Error;
import Utilities.SymbolTable;
import Utilities.Visitor;
import java.util.*;
import java.math.*;

/**
 * TypeChecker implements the type checker for the Espresso langauge.
 */
public class TypeChecker extends Visitor {
    
    /**
     * <p>Returns the 'best-fitting' method or constructor from a list 
     * of potential candidates given a set of actual parameters.</p>
     * <p>See Section 7.2.9: Finding the Right Method to Call in Practical Compiler Construction.</p>
     * <p>Remember to visit all the arguments BEFORE calling findMethod. findMethod uses the <i>.type</i> field of the parameters.</p>
     * @param candidateMethods Sequence of methods or constructors. Use {@link AST.ClassDecl#allMethods} or {@link AST.ClassDecl#constructors} from the appropriate {@link AST.ClassDecl}.
     * @param name The name of the method or constructor you are looking for.
     * @param actualParams The sequence of actual parameters being passed to the method invocation or constructor invocation. 
     * @param lookingForMethods <i>true</i> if you pass a sequence of methods ({@link AST.ClassDecl#allMethods}), <i>false</i> if you pass a sequence of constructors ({@link AST.ClassDecl#constructors}).
     * @return The {@link AST.MethodDecl}/{@link AST.ConstructorDecl} found; null if nothing was found.
     */
    public static ClassBodyDecl findMethod(Sequence candidateMethods, String name, Sequence actualParams, 
					   boolean lookingForMethods) {
	
	if (lookingForMethods) {
	    println("+------------- findMethod (Method) ------------");
	    println("| Looking for method: " + name);
	} else {
	    println("+---------- findMethod (Constructor) ----------");
	    println("| Looking for constructor: " + name);
	}
	println("| With parameters:");
	for (int i=0; i<actualParams.nchildren; i++){
	    println("|   " + i + ". " + ((actualParams.children[i] instanceof ParamDecl)?(((ParamDecl)actualParams.children[i]).type()):((Expression)actualParams.children[i]).type));
	}
	// The number of actual parameters in the invocation.
	int count = 0;
	
	// Make an array big enough to hold all the methods if needed
	ClassBodyDecl cds[] = new ClassBodyDecl[candidateMethods.nchildren];
	
	// Initialize the array to point to null
	for(int i=0;i<candidateMethods.nchildren;i++) 
	    cds[i] = null;
	
	Sequence args = actualParams;
	Sequence params;
	
	// Insert all the methods from the symbol table that:
	// 1.) has the right number of parameters
	// 2.) each formal parameter can be assigned its corresponding
	//     actual parameter.
	if (lookingForMethods)
	    println("| Finding methods with the right number of parameters and types");
	else
	    println("| Finding constructors with the right number of parameters and types");
	for (int cnt=0; cnt<candidateMethods.nchildren; cnt++) {
	    ClassBodyDecl cbd = (ClassBodyDecl)candidateMethods.children[cnt];
	    
	    // if the method doesn't have the right name, move on!
	    if (!(cbd.getname().equals(name)))
		continue;
	    
	    // Fill params with the formal parameters.
	    if (cbd instanceof ConstructorDecl) 
		params = ((ConstructorDecl)cbd).params();
	    else if (cbd instanceof MethodDecl)
		params = ((MethodDecl)cbd).params();
	    else
		// we have a static initializer, don't do anything - just skip it.
		continue;
	    
	    print("|   " + name + "(");
	    if (cbd instanceof ConstructorDecl) 
		print(Type.parseSignature(((ConstructorDecl)cbd).paramSignature()));
	    else 
		print(Type.parseSignature(((MethodDecl)cbd).paramSignature()));
	    print(" )  ");
	    
	    if (args.nchildren == params.nchildren) {
		// The have the same number of parameters
		// now check that the formal parameters are
		// assignmentcompatible with respect to the 
		// types of the actual parameters.
		// OBS this assumes the type field of the actual
		// parameters has been set (in Expression.java),
		// so make sure to call visit on the parameters first.
		boolean candidate = true;
		
		for (int i=0;i<args.nchildren; i++) {
		    candidate = candidate &&
			Type.assignmentCompatible(((ParamDecl)params.children[i]).type(),
						  (args.children[i] instanceof Expression) ?
						  ((Expression)args.children[i]).type :
						  ((ParamDecl)args.children[i]).type());
		    
		    if (!candidate) {
			println(" discarded");
			break;
		    }
		}
		if (candidate) {
		    println(" kept");
		    cds[count++] = cbd;
		}
	    }
	    else {
		println(" discarded");
	    }
	    
	}
	// now count == the number of candidates, and cds is the array with them.
	// if there is only one just return it!
	println("| " + count + " candidate(s) were found:");
	for ( int i=0;i<count;i++) {
	    ClassBodyDecl cbd = cds[i];
	    print("|   " + name + "(");
	    if (cbd instanceof ConstructorDecl) 
		print(Type.parseSignature(((ConstructorDecl)cbd).paramSignature()));
	    else 
		print(Type.parseSignature(((MethodDecl)cbd).paramSignature()));
	    println(" )");
	}
	
	if (count == 0) {
	    println("| No candidates were found.");
	    println("+------------- End of findMethod --------------");
	    return null;
	}
	
	if (count == 1) {
	    println("| Only one candidate - thats the one we will call then ;-)");
	    println("+------------- End of findMethod --------------");
	    return cds[0];
	}
	println("| Oh no, more than one candidate, now we must eliminate some >:-}");
	// there were more than one candidate.
	ClassBodyDecl x,y;
	int noCandidates = count;
	
	for (int i=0; i<count; i++) {
	    // take out a candidate
	    x = cds[i];
	    
	    if (x == null)
		continue;		    
	    cds[i] = null; // this way we won't find x in the next loop;
	    
	    // compare to all other candidates y. If any of these
	    // are less specialised, i.e. all types of x are 
	    // assignment compatible with those of y, y can be removed.
	    for (int j=0; j<count; j++) {
		y = cds[j];
		if (y == null) 
		    continue;
		
		boolean candidate = true;
		
		// Grab the parameters out of x and y
		Sequence xParams, yParams;
		if (x instanceof ConstructorDecl) {
		    xParams = ((ConstructorDecl)x).params();
		    yParams = ((ConstructorDecl)y).params();
		} else {
		    xParams = ((MethodDecl)x).params();
		    yParams = ((MethodDecl)y).params();
		}
		
		// now check is y[k] <: x[k] for all k. If it does remove y.
		// i.e. check if y[k] is a superclass of x[k] for all k.
		for (int k=0; k<xParams.nchildren; k++) {
		    candidate = candidate &&
			Type.assignmentCompatible(((ParamDecl)yParams.children[k]).type(),
						  ((ParamDecl)xParams.children[k]).type());
		    
		    if (!candidate)
			break;
		}
		if (candidate) {
		    // x is more specialized than y, so throw y away.
		    print("|   " + name + "(");
		    if (y instanceof ConstructorDecl) 
			print(Type.parseSignature(((ConstructorDecl)y).paramSignature()));
		    else 
			print(Type.parseSignature(((MethodDecl)y).paramSignature()));
		    print(" ) is less specialized than " + name + "(");
		    if (x instanceof ConstructorDecl) 
			print(Type.parseSignature(((ConstructorDecl)x).paramSignature()));
		    else 
			print(Type.parseSignature(((MethodDecl)x).paramSignature()));
		    println(" ) and is thus thrown away!");
		    
		    cds[j] = null;
		    noCandidates--;
		}
	    }
	    // now put x back in to cds
	    cds[i] = x;
	}
	if (noCandidates != 1) {
	    // illegal function call
	    println("| There is more than one candidate left!");
	    println("+------------- End of findMethod --------------");
	    return null;
	}
	
	// just find it and return it.
	println("| We were left with exactly one candidate to call!");
	println("+------------- End of findMethod --------------");
	for (int i=0; i<count; i++)
	    if (cds[i] != null)
		return cds[i];
	
	return null;
    }

    /**
     * Given a list of candiate methods and a name of the method this method prints them all out.
     *
     * @param cd The {@link AST.ClassDecl} for which the methods or constructors are being listed.
     * @param candidateMethods A {@link AST.Sequence} of either {@link AST.MethodDecl}s ({@link AST.ClassDecl#allMethods}) or {@link AST.ConstructorDecl}s ({@link AST.ClassDecl#constructors}). 
     * @param name The name of the method or the constructor for which the candidate list should be produced.
     */
    public String listCandidates(ClassDecl cd, Sequence candidateMethods, String name) {
	String s = "\n";
	for (int cnt=0; cnt<candidateMethods.nchildren; cnt++) {
	    ClassBodyDecl cbd = (ClassBodyDecl)(candidateMethods.children[cnt]);

	    if (cbd.getname().equals(name)) {
		if (cbd instanceof MethodDecl)
		    s += ("  " + name + "(" + Type.parseSignature(((MethodDecl)cbd).paramSignature()) + " )\n");
		else
		    s += ("  " + cd.name() + "(" + Type.parseSignature(((ConstructorDecl)cbd).paramSignature()) + " )\n");
	    }
	}
	return s;
    }
    /**
     * The global class tabel. This should be set in the constructor.
     */
    private SymbolTable   classTable;
    /**
     * The class of which children are currently being visited. This should be updated when visiting a {@link AST.ClassDecl}.
     */
    private ClassDecl     currentClass;     
    /**
     * The particular {@link AST.ClassBodyDecl} (except {@link AST.FieldDecl}) of which children are currently being visited.
     */
    private ClassBodyDecl currentContext;  
    /**
     * The current {@link AST.FieldDecl} of which children are currently being visited (if applicable).
     */
    private FieldDecl     currentFieldDecl; 
    /**
     * Indicates if children being visited are part of a {@link AST.FieldDecl} initializer. (accessible though {@link AST.FieldDecl#var()}). Used for determining forward reference of a non-initialized field. You probably don't want to bother with this one.
     */
    private boolean       inFieldInit;      

    /**
     * Constructs a new type checker.
     * @param classTable The global class table.
     * @param debug determins if debug information should printed out.
     */
    public TypeChecker(SymbolTable classTable, boolean debug) { 
	this.classTable = classTable; 
	this.debug = debug;
    }

    /** 
     * Type checks an ArrayAccessExpr node.
     * @param ae An {@link AST.ArrayAccessExpr} parse tree node.
     * @return Returns the type of the array access expression.
     */
    public Object visitArrayAccessExpr(ArrayAccessExpr ae) {
	println(ae.line + ":\tVisiting ArrayAccessExpr.");
	// YOUR CODE HERE 1
	super.visitArrayAccessExpr(ae);

	return ae.type;
    }

    /** 
     * Type checks an ArrayType node.
     * @param at An {@link AST.ArrayType} parse tree node.
     * @return Returns itself.
     */
    public Object visitArrayType(ArrayType at) {
	println(at.line + ":\tVisiting an ArrayType.");
	println(at.line + ":\tArrayType type is " + at);
	// An ArrayType is already a type, so nothing to do.
	return at;
    }

    /**
     * Type checks a NewArray node.
     * @param ne A {@link NewArray} parse tree node.
     * @return Returns the type of the NewArray node.
     */
    public Object visitNewArray(NewArray ne) {
	println(ne.line + ":\tVisiting a NewArray.");

	// YOUR CODE HERE 2
	super.visitNewArray(ne);

	println(ne.line + ":\tNewArray type is: " + ne.type);
	return ne.type;
    }

    /**
     * arrayAssignmentCompatible: Determines if the expression 'e' can be assigned to the type 't'.
     * @param t A {@link Type} 
     * @param e An {@link Expression}
     * @return Returns true if the the array literal can be assigned to the type.
     * See Section 7.2.6 sub-heading 'Constructed Types'.
     */
    public boolean arrayAssignmentCompatible(Type t, Expression e) {
	if (t instanceof ArrayType && (e instanceof ArrayLiteral)) {
	    ArrayType at = (ArrayType)t;
	    e.type = at; //  we don't know that this is the type - but if we make it through it will be!
	    ArrayLiteral al = (ArrayLiteral)e;
	    
	    // t is an array type i.e. XXXXXX[ ]
	    // e is an array literal, i.e., { }
	    if (al.elements().nchildren == 0) // the array literal is { }
		return true;   // any array variable can hold an empty array
	    // Now check that XXXXXX can hold value of the elements of al
	    // we have to make a new type: either the base type if |dims| = 1
	    boolean b = true;
	    for (int i=0; i<al.elements().nchildren; i++) {
		if (at.getDepth() == 1) 
		    b = b && arrayAssignmentCompatible(at.baseType(), (Expression)al.elements().children[i]);
		else { 
		    ArrayType at1 = new ArrayType(at.baseType(), at.getDepth()-1);
		    b = b  && arrayAssignmentCompatible(at1, (Expression)al.elements().children[i]);
		}
	    }
	    return b;
	} else if (t instanceof ArrayType && !(e instanceof ArrayLiteral)) {
	    Type t1 = (Type)e.visit(this);
	    if (t1 instanceof ArrayType)
		if (!Type.assignmentCompatible(t,t1))
		    Error.error("Incompatible type in array assignment.");
		else
		    return true;
	    Error.error(t, "Error:\tcannot assign non array to array type '" + t.typeName() + "'.");	    
	}
	else if (!(t instanceof ArrayType) && (e instanceof ArrayLiteral)) {
	    Error.error(t, "Error:\tcannot assign value '" + ((ArrayLiteral)e).toString() + "' to type '" + t.typeName() + "'.");
	}
	return Type.assignmentCompatible(t,(Type)e.visit(this));
    }

    /** 
     * An ArrayLiteral cannot appear with a 'new' keyword.
     * @param al An {@link ArrayLiteral} parse tree node.
     * @return Always returns null (never returns!)
     */
    public Object visitArrayLiteral(ArrayLiteral al) {
	// Espresso does not allow array literals without the 'new <type>' part.
	Error.error(al, "Array literal must be preceeded by a 'new <type>'.");
	return null;
    }
    
    /** 
     * Type checks an Assignment node.
     * @param as An {@link Assignment} parse tree node.
     * @return Returns the type of the Assignment node.
     */
    public Object visitAssignment(Assignment as) {
	println(as.line + ":\tVisiting an Assignment.");

	// get the types of the LHS (v) and the RHS(e)
	Type vType = (Type) as.left().visit(this);
	Type eType = (Type) as.right().visit(this);

	/** Note: as.left() should be of NameExpr or FieldRef class! */

	if (!vType.assignable())          
	    Error.error(as,"Left-hand side of assignment not assignable.");

	// Cannot assign to a classname
	if (as.left() instanceof NameExpr && (((NameExpr)as.left()).myDecl instanceof ClassDecl))
	    Error.error(as,"Left-hand side of assignment not assignable.");

	// RHS cannot be void
	if (as.right().type.isVoidType())
	    Error.error(as,"void type cannot be used here.");

	
	// Now switch on the operator
	switch (as.op().kind) {  //ERIC
	case AssignmentOp.EQ :{
	    // Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}

	// YOUR CODE HERE
	case AssignmentOp.MULTEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.DIVEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.MODEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.PLUSEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.MINUSEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.LSHIFTEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.RSHIFTEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.RRSHIFTEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.ANDEQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.OREQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}
	case AssignmentOp.XOREQ :{
		// Check if the right hand side is a constant.	    
	    // if we don't do this the following is illegal: byte b; b = 4; because 4 is an int!
	    if (as.right().isConstant()) {
		if (vType.isShortType() && Literal.isShortValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
		if (vType.isByteType() && Literal.isByteValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;		
		if (vType.isCharType() && Literal.isCharValue(((BigDecimal)as.right().constantValue()).longValue()))
		    break;
	    }
		     
	    // Now just check for assignment compatability
	    if (!Type.assignmentCompatible(vType,eType))
		Error.error(as,"Cannot assign value of type " + eType.typeName() + " to variable of type " + vType.typeName() + ".");
	    break;
	}

	}
	// The overall type is always that of the LHS.
	as.type = vType;
	println(as.line + ":\tAssignment has type: " + as.type);

	return vType;
    }

    /** BINARY EXPRESSION */
    public Object visitBinaryExpr(BinaryExpr be) {
	println(be.line + ":\tVisiting a Binary Expression.");

	// YOUR CODE HERE 3
	super.visitBinaryExpr(be);

	println(be.line + ":\tBinary Expression has type: " + be.type);
	return be.type;
    }

    /** CAST EXPRESSION */
    public Object visitCastExpr(CastExpr ce) { //NICK
	println(ce.line + ":\tVisiting a cast expression.");

	// We have two different types of casts:
	// Numeric: any numeric type can be cast to any other numeric type.
	// Class: (A)e, where Type(e) is B. This is legal ONLY if A :> B or B :> A
	//        that is either A has to be above B in the class hierarchy or 
	//        B has to be above A.
	// Do note that if the cast type and the expression type are identical,
	// then the cast is fine: for example (String)"Hello" or (Boolean)true.
	//
	// One small caveat: (A)a is not legal is 'a' is a class name. So if the 
	// expression is a NameExpr then it cannot be the name of a class - that is,
	// its myDecl cannot be a ClassDecl.
	// YOUR CODE HERE

	// The overall type of a cast expression is always the cast type.

	println(ce.line + ":\tCast Expression has type: " + ce.type);
	return ce.type;
    }

    /** CLASSTYPE */
    public Object visitClassType(ClassType ct) {
	println(ct.line + ":\tVisiting a ClassType.");
	// A class type is alreayd a type, so nothing to do.
	println(ct.line + ":\tClassType has type: " + ct);
	return ct;
    }

    /** CONSTRUCTOR (EXPLICIT) INVOCATION */
    public Object visitCInvocation(CInvocation ci) {
	println(ci.line + ":\tVisiting an explicit constructor invocation.");

	// An explicit constructor invocation takes one of two forms:
	// this ( ... )  -- this calls a constructor in the same class (currentClass)
	// super ( ... ) -- this calls a constructor in the super class (of currentClass)

	// YOUR CODE HERE 4


	return null;
    }

    /** CLASS DECLARATION */
    public Object visitClassDecl(ClassDecl cd) {
	println(cd.line + ":\tVisiting a ClassDecl(" + cd.name() + ")");

	// Update the current class.
	currentClass = cd;
	// YOUR CODE HERE 5
	super.visitClassDecl(cd);

	return null;
    }

    /** CONSTRUCTOR DECLARATION */
    public Object visitConstructorDecl(ConstructorDecl cd) {
	println(cd.line + ":\tVisiting a ConstructorDecl.");

	// Update the current context
	currentContext = cd;

	// YOUR CODE HERE 6
	super.visitConstructorDecl(cd);

	return null;
    }

    /** DO STATEMENT */
    public Object visitDoStat(DoStat ds) {
	println(ds.line + ":\tVisiting a DoStat.");

	// YOUR CODE HERE 7
	super.visitDoStat(ds);

	return null;
    }

    /** FIELD DECLARATION */
    public Object visitFieldDecl(FieldDecl fd) {
	println(fd.line + ":\tVisiting a FieldDecl.");

	// Update the current context
	currentContext = fd;
	// set inFieldInit to true as we are about to visit the field initializer.
	// (happens from visitVar() if it isn't null.
	inFieldInit = true;
	// Set the current field.
	currentFieldDecl = fd;
	// Visit the var
	if (fd.var().init() != null)
	    fd.var().visit(this);
	// Set current field back to null (fields cannot be nested, so this is OK)
	currentFieldDecl = null;
	// set ifFieldInit back to false as we are done with the initializer.
	inFieldInit = false;

	return fd.type();
    }

    /** FIELD REFERENCE */
    public Object visitFieldRef(FieldRef fr) {
	println(fr.line + ":\tVisiting a FieldRef.");

	Type targetType = (Type) fr.target().visit(this);
	String field    = fr.fieldName().getname();
	// Changed June 22 2012 ARRAY
	if (fr.fieldName().getname().equals("length")) {
	    if (targetType.isArrayType()) {
		fr.type = new PrimitiveType(PrimitiveType.IntKind);
		println(fr.line + ":\tField Reference was a an Array.length reference, and it has type: " + fr.type);
		fr.targetType = targetType;
		return fr.type;
	    }
	}

	if (targetType.isClassType()) {
	    ClassType c = (ClassType)targetType;
	    ClassDecl cd = c.myDecl;
	    fr.targetType = targetType;

	    println(fr.line + ":\tLooking up symbol '" + field + "' in fieldTable of class '" + 
		    c.typeName() + "'.");

	    // Lookup field in the field table of the class associated with the target.
	    FieldDecl lookup = (FieldDecl) NameChecker.NameChecker.getField(field, cd);

	    // Field not found in class.
	    if (lookup == null)
		Error.error(fr,"Field '" + field + "' not found in class '" + cd.name() + "'.");
	    else {
		fr.myDecl = lookup;
		fr.type = lookup.type();
	    }
	} else 
	    Error.error(fr,"Attempt to access field '" + field + "' in something not of class type.");
	println(fr.line + ":\tField Reference has type: " + fr.type);

	/*if (inFieldInit && currentFieldDecl.fieldNumber <= fr.myDecl.fieldNumber && currentClass.name().equals(   (((ClassType)fr.targetType).myDecl).name()))
	    Error.error(fr,"Illegal forward reference of non-initialized field.");
	*/
	return fr.type;
    }

    /** FOR STATEMENT */
    public Object visitForStat(ForStat fs) {
	println(fs.line + ":\tVisiting a ForStat.");

	// YOUR CODE HERE 8
	super.visitForStat(fs);

	return null;
    }

    /** IF STATEMENT */
    public Object visitIfStat(IfStat is) {
	println(is.line + ":\tVisiting an IfStat");

	// YOUR CODE HERE 9
	super.visitIfStat(is);

	return null;
    }

    /** INVOCATION */
    public Object visitInvocation(Invocation in) {
	println(in.line + ":\tVisiting an Invocation.");

	// YOUR CODE HERE 10
	super.visitInvocation(in);

	println(in.line + ":\tInvocation has type: " + in.type);
	return in.type;
    }

    /** LITERAL */
    public Object visitLiteral(Literal li) {
	println(li.line + ":\tVisiting a literal (" + li.getText() + ").");

	// YOUR CODE HERE 11 ERIC HERE AND UP
	super.visitLiteral(li);

	println(li.line + ":\tLiteral has type: " + li.type);
	return li.type;
    }

    /** METHOD DECLARATION */
    public Object visitMethodDecl(MethodDecl md) {
	println(md.line + ":\tVisiting a MethodDecl.");
	currentContext = md;

	// YOUR CODE HERE 12 NICK AND DOWN
//that one line there should just be somthing that visits the children
super.visitMethodDecl(md);

	return null;
    }

    /** NAME EXPRESSION */
    public Object visitNameExpr(NameExpr ne) {
	println(ne.line + ":\tVisiting a NameExpr.");

	// YOUR CODE HERE 13
super.visitNameExpr(ne); //guessing for now will need to be changed

	println(ne.line + ":\tName Expression has type: " + ne.type);
	return ne.type;
    }

    /** NEW */
    public Object visitNew(New ne) {
	println(ne.line + ":\tVisiting a New.");

	// YOUR CODE HERE 14
//same as invocation
//target.name(p,a,r,a,m,s)
//if null target class = current class
//if not null ->  visit it ;  must be of class type, its mydecle is the target class 
// visit params in.params().visit(..)
//call findmethod(target class.allmethods, method name, in.params(),true)
// if we get null back then no meathod found


	println(ne.line + ":\tNew has type: " + ne.type);
	return ne.type;
    }


    /** RETURN STATEMENT */
    public Object visitReturnStat(ReturnStat rs) {
	println(rs.line + ":\tVisiting a ReturnStat.");
	Type returnType;

	if (currentContext instanceof MethodDecl)
	    returnType = ((MethodDecl)currentContext).returnType();
	else
	    returnType = null;

	// Check is there is a return in a Static Initializer
	if (currentContext instanceof StaticInitDecl) 
	    Error.error(rs,"return outside method.");

	// Check if a void method is returning something.
	if (returnType == null || returnType.isVoidType()) {
	    if (rs.expr() != null)
		Error.error(rs, "Return statement of a void function cannot return a value.");
	    return null;
	}

	// Check if a non void method is returning without a proper value.
	if (rs.expr() == null && returnType != null)
	    Error.error(rs, "Non void function must return a value.");

	Type returnValueType = (Type) rs.expr().visit(this);	
	if (rs.expr().isConstant()) {
	    if (returnType.isShortType() && Literal.isShortValue(((BigDecimal)rs.expr().constantValue()).longValue()))
		;// is ok break;                                                                                                    
	    else if (returnType.isByteType() && Literal.isByteValue(((BigDecimal)rs.expr().constantValue()).longValue()))
		; // is ok break;                                                                                                   
	    else if (returnType.isCharType() && Literal.isCharValue(((BigDecimal)rs.expr().constantValue()).longValue()))
		; // break;
	    else if (!Type.assignmentCompatible(returnType,returnValueType))
		Error.error(rs, "Illegal value of type " + returnValueType.typeName() + 
			    " in method expecting value of type " + returnType.typeName() + ".");
	} else if (!Type.assignmentCompatible(returnType,returnValueType))
	    Error.error(rs, "Illegal value of type " + returnValueType.typeName() + 
			" in method expecting value of type " + returnType.typeName() + ".");
	rs.setType(returnType);
	return null;
    }

    /** STATIC INITIALIZER */
    public Object visitStaticInitDecl(StaticInitDecl si) {
	println(si.line + ":\tVisiting a StaticInitDecl.");

	// YOUR CODE HERE 15
//set current context and vidit the children
	currentContext = si;
super.visitStaticInitDecl(si);


	return null;
    }

    /** SUPER */
    public Object visitSuper(Super su) {
	println(su.line + ":\tVisiting a Super.");

	// YOUR CODE HERE 16
//get super class of current class, if no super class give error
if (su == null){
Error.error(su,"no superclass found");
}
	return su.type;
    }

    /** SWITCH STATEMENT */
    public Object visitSwitchStat(SwitchStat ss) {
	println(ss.line + ":\tVisiting a SwitchStat.");
//espresso+
super.visitSwitchStat(ss);
	// YOUR CODE HERE 17

	return null;
    }
    // YOUR CODE HERE  18
    /** TERNARY EXPRESSION */
    public Object visitTernary(Ternary te) {
	println(te.line + ":\tVisiting a Ternary.");
	super.visitTernary(te);
//espresso+
	// YOUR CODE HERE 19
	println(te.line + ":\tTernary has type: " + te.type);
	return te.type;
    }

    /** THIS */
    public Object visitThis(This th) {
	println(th.line + ":\tVisiting a This.");

	th.type = th.type();

	println(th.line + ":\tThis has type: " + th.type);
	return th.type;
    }

    /** UNARY POST EXPRESSION */
    public Object visitUnaryPostExpr(UnaryPostExpr up) {
	println(up.line + ":\tVisiting a UnaryPostExpr.");
	// YOUR CODE HERE 20
// exp++ / exp--
//be,type = type of expression
//type of expression must be numeric

//check if exp is numberic, if not we dont care about ++/--
if(((Type)(AST)up.op()).isNumericType() != true ){ //100% AN ERROR, BUT KEEPS COMPILER HAPPY
	Error.error(up, " UNARY POST EXPRESSION expression is not a number");
	}

//now to check if what the front was
if((up.op().getKind()) == 1){} //plus plus
	else if(up.op().getKind() == 2){} //minusminus
		else if(up.op().getKind() == 3){	println(up.line + "HOW DID YOU GET A 3???????") ;} //efficency is for the poor!!!!!
			else{Error.error(up, "UNARY POST EXPRESSION was not ++ or --");} //error is here
			
			
			
	println(up.line + ":\tUnary Post Expression has type: " + up.type); // this line was here bore me so it stays
	return up.type; // \*-*/
    }

    /** UNARY PRE EXPRESSION */
    public Object visitUnaryPreExpr(UnaryPreExpr up) {
	println(up.line + ":\tVisiting a UnaryPreExpr.");
	int Donne = 0;
// ++/-- same as ubove
//+ / - : numeric
// ! : bool
//~ : intigral
// byte,char,short => int
	// YOUR CODE HERE 21

/*
 * 
 	public static final int PLUSPLUS   = 1; // ++
	public static final int MINUSMINUS = 2; // --
	public static final int PLUS       = 3; // +
	public static final int MINUS      = 4; // -
	public static final int COMP       = 5; // ~
	public static final int NOT        = 6; // !
 */


//++ /-- stolen from above

//check if exp is numberic, if not we dont care about ++/--
if(((Type)(AST)up.op()).isNumericType() != true ){ //100% AN ERROR, BUT KEEPS COMPILER HAPPY
	Error.error(up, " UNARY PRE EXPRESSION expression is not a number");
	}

//now to check if what the back  was
if((up.op().getKind()) == 1 && Donne == 0){Donne = 1;} //plus plus
	else if(up.op().getKind() == 2){} //minusminus
		else if(up.op().getKind() == 3){}//plus
			else if(up.op().getKind() == 4){} //minus
				else if(up.op().getKind() == 5){} //comp
					else if(up.op().getKind() == 6){} //not
						else{Error.error(up, "UNARY PRE EXPRESSION was not accepted");} //error is here



	println(up.line + ":\tUnary Pre Expression has type: " + up.type);
	return up.type;
    }

    /** VAR */
    public Object visitVar(Var va) {
	println(va.line + ":\tVisiting a Var.");
//type x = expr 
//the type "type" must be able to hold the value in expr
//    public static boolean assignmentCompatible(Type var, Type val) {
if(va.init() != null){
 if(Type.assignmentCompatible((Type)va.myDecl.type() , (Type)va.init().visit(this)) == false){
	 Error.error(va, "type mismatch");
	 }
	// YOUR CODE HERE 22
	//return null;

    }
    	return null;

}

    /** WHILE STATEMENT */
    public Object visitWhileStat(WhileStat ws) {
	println(ws.line + ":\tVisiting a WhileStat."); 
	if(((Type)(AST)ws.expr()).isBooleanType() == true){}
	else{Error.error(ws, "UNARY PRE EXPRESSION was not accepted");}
	// YOUR CODE HERE 23

	return null;
    }

}

