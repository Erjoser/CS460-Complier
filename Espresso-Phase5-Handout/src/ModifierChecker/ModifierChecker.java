package ModifierChecker;

import AST.*;
import Utilities.*;
import NameChecker.*;
import TypeChecker.*;
import Utilities.Error;
import java.util.*;

public class ModifierChecker extends Visitor {

	private SymbolTable classTable;
	private ClassDecl currentClass;
	private ClassBodyDecl currentContext;
	private boolean leftHandSide = false;
        

	public ModifierChecker(SymbolTable classTable, boolean debug) {
		this.classTable = classTable;
		this.debug = debug;
	}
    // YOUR CODE HERE 1 //nick

    /** 
     * Uses the M algorithm from the Book to check that all abstract classes are 
     * implemented correctly in the class hierarchy. If not an error message is produced.
     *
     * @param cd A {@link ClassDecl ClassDecl} object.
     * @param methods The sequence of all methods of cd.
     */   
    public void checkImplementationOfAbstractClasses(ClassDecl cd) {
	// YOUR CODE HERE 2
    }




    
	/** Assignment */
	public Object visitAssignment(Assignment as) {
	    println(as.line + ":\tVisiting an assignment (Operator: " + as.op()+ ").");

		boolean oldLeftHandSide = leftHandSide;

		leftHandSide = true;
		as.left().visit(this);

		// Added 06/28/2012 - no assigning to the 'length' field of an array type
		if (as.left() instanceof FieldRef) {
			FieldRef fr = (FieldRef)as.left();
			if (fr.target().type.isArrayType() && fr.fieldName().getname().equals("length"))
				Error.error(fr,"Cannot assign a value to final variable length.");
		}
		leftHandSide = oldLeftHandSide;
		as.right().visit(this);

		return null;
	}

	/** CInvocation */
	public Object visitCInvocation(CInvocation ci) {
	    println(ci.line + ":\tVisiting an explicit constructor invocation (" + (ci.superConstructorCall() ? "super" : "this") + ").");

		// YOUR CODE HERE 3

		return null;
	}

	/** ClassDecl */
	public Object visitClassDecl(ClassDecl cd) {
		println(cd.line + ":\tVisiting a class declaration for class '" + cd.name() + "'.");

		currentClass = cd;

		// If this class has not yet been declared public make it so.
		if (!cd.modifiers.isPublic())
			cd.modifiers.set(true, false, new Modifier(Modifier.Public));

		// If this is an interface declare it abstract!
		if (cd.isInterface() && !cd.modifiers.isAbstract())
			cd.modifiers.set(false, false, new Modifier(Modifier.Abstract));

		// If this class extends another class then make sure it wasn't declared
		// final.
		if (cd.superClass() != null)
			if (cd.superClass().myDecl.modifiers.isFinal())
				Error.error(cd, "Class '" + cd.name()
						+ "' cannot inherit from final class '"
						+ cd.superClass().typeName() + "'.");

		// YOUR CODE HERE 4 //eric
	
		return null;
		

	}

	/** FieldDecl */
	public Object visitFieldDecl(FieldDecl fd) {
	    println(fd.line + ":\tVisiting a field declaration for field '" +fd.var().name() + "'.");

		// If field is not private and hasn't been declared public make it so.
		if (!fd.modifiers.isPrivate() && !fd.modifiers.isPublic())
			fd.modifiers.set(false, false, new Modifier(Modifier.Public));

		// YOUR CODE HERE 5 //together

		return null;
	}

	/** FieldRef */
    public Object visitFieldRef(FieldRef fr) {
	println(fr.line + ":\tVisiting a field reference '" + fr.fieldName() + "'.");
	
	// YOUR CODE HERE 6
	
	return null;
    }       

	/** MethodDecl */
    public Object visitMethodDecl(MethodDecl md) {
	println(md.line + ":\tVisiting a method declaration for method '" + md.name() + "'.");
	
	// YOUR CODE HERE 7
	
	return null;
    }
    
	/** Invocation */
	public Object visitInvocation(Invocation in) {
	    println(in.line + ":\tVisiting an invocation of method '" + in.methodName() + "'.");
	    // YOUR CODE HERE 8 //eric and above
	    
	    return null;
	}
    

	public Object visitNameExpr(NameExpr ne) {
	    println(ne.line + ":\tVisiting a name expression '" + ne.name() + "'. (Nothing to do!)");
	    return null;
	}

	/** ConstructorDecl */
	public Object visitConstructorDecl(ConstructorDecl cd) {
	    println(cd.line + ":\tVisiting a constructor declaration for class '" + cd.name() + "'.");
	      
		// YOUR CODE HERE 9 //5 nick down

		return null;
	}

	/** New */
	public Object visitNew(New ne) {
	    println(ne.line + ":\tVisiting a new '" + ne.type().myDecl.name() + "'.");

		// YOUR CODE HERE 10 //4

		return null;
	}

	/** StaticInit */
	public Object visitStaticInitDecl(StaticInitDecl si) {
		println(si.line + ":\tVisiting a static initializer.");

		// YOUR CODE HERE 11 //3

		return null;
	}

	/** Super */
	public Object visitSuper(Super su) {
		println(su.line + ":\tVisiting a super.");

		if (currentContext.isStatic())
			Error.error(su,
					"non-static variable super cannot be referenced from a static context.");

		return null;
	}

	/** This */
    public Object visitThis(This th) {
	println(th.line + ":\tVisiting a this.");
	
	if (currentContext.isStatic())
	    Error.error(th,	"non-static variable this cannot be referenced from a static context.");
	
	return null;
    }
    
    /** UnaryPostExpression */
    public Object visitUnaryPostExpr(UnaryPostExpr up) {
	println(up.line + ":\tVisiting a unary post expression with operator '" + up.op() + "'.");
	
	// YOUR CODE HERE 12 // 2
	return null;
    }
    
    /** UnaryPreExpr */
    public Object visitUnaryPreExpr(UnaryPreExpr up) {
	println(up.line + ":\tVisiting a unary pre expression with operator '" + up.op() + "'.");
	
	// YOUR CODE HERE 13 //
	return null; 
    }
}
