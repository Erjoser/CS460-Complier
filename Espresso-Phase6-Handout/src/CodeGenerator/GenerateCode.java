package CodeGenerator;

import AST.*;
import Utilities.Error;
import Utilities.Visitor;
import Utilities.SymbolTable;
import Utilities.Settings;
import java.util.*;

import Instruction.*;
import Jasmin.*;

class GenerateCode extends Visitor {
    
    private Generator gen;
    private ClassDecl currentClass;
    private boolean insideLoop = false;
    private boolean insideSwitch = false;
    private ClassFile classFile;

    private boolean generateDup = true; // set this false when dealing with a ExprStat.
    private ClassBodyDecl currentContext = null;

    private boolean StringBuilderCreated = false;


    public void makeStringBuilder() {
	classFile.addInstruction(new ClassRefInstruction(RuntimeConstants.opc_new,
				 "java/lang/StringBuilder"));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
	classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokespecial,
				 "java/lang/StringBuilder", "<init>", "()V"));
    }

    public Instruction makeLoadStoreInstruction(Type type, int address, boolean load, boolean isArrayInstruction) {
	if (load) {
	    if (!isArrayInstruction) {
		if (address < 4)
		    return new Instruction(Generator.getLoadInstruction(type, address, false));
		else
		    return new SimpleInstruction(Generator.getLoadInstruction(type, address, isArrayInstruction), address);
	    } else {
		return new Instruction(Generator.getLoadInstruction(type, address, true));
	    }
	} else {
	    if (!isArrayInstruction) {	    
		if (address < 4)
		    return new Instruction(Generator.getStoreInstruction(type, address, false));
		else
		    return new SimpleInstruction(Generator.getStoreInstruction(type, address, isArrayInstruction), address);
	    } else {
		return new Instruction(Generator.getStoreInstruction(type, address, true));
	    }
	}
    }

    public GenerateCode(Generator g, boolean debug) {
	gen = g;
	this.debug = debug;
	classFile = gen.getClassFile();
    }

    public void setCurrentClass(ClassDecl cd) {
	this.currentClass = cd;
    }

    /**Experimental 11/28/2023: for generating .var lines we need two labels and then dump the table
     * L1:
     * .var
     * ...
     * code for the block
     * L2
     */    
    public void createDotVars(SymbolTable st, String varsTopLabel, String varsBottomLabel) {
	if (st.entries.size() != 0) {
            classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, varsTopLabel));
            Enumeration<String> e = st.entries.keys();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
		VarDecl vd = (VarDecl)st.get(key);
                currentContext.vars.add(".var " + vd.address() + " is " + key + " " + vd.type().signature() + " from " + varsTopLabel + " to " +varsBottomLabel);
            }
        }
    }
    
    
    // ARRAY VISITORS START HERE

    /** ArrayAccessExpr */
    public Object visitArrayAccessExpr(ArrayAccessExpr ae) {
	println(ae.line + ": Visiting ArrayAccessExpr");
	classFile.addComment(ae, "ArrayAccessExpr");
	// YOUR CODE HERE
	// Expresso*
	classFile.addComment(ae,"End ArrayAccessExpr");
	return null;
    }

    /** ArrayLiteral */
    public Object visitArrayLiteral(ArrayLiteral al) {
	println(al.line + ": Visiting an ArrayLiteral ");
	// YOUR CODE HERE
	// Expresso*
	return null;
    }

    /** NewArray */
    public Object visitNewArray(NewArray ne) {
	println(ne.line + ": NewArray:\t Creating new array of type " + ne.type.typeName());
	// YOUR CODE HERE
	// Expresso*
	return null;
    }
    // END OF ARRAY VISITORS

    // ASSIGNMENT
    public Object visitAssignment(Assignment as) {
	println(as.line + ": Assignment:\tGenerating code for an Assignment.");
	
	classFile.addComment(as, "Assignment");
	
	// special case for <lhs> += <expr> where <lhs>.type = String
	if (as.left().type.isStringType() && as.op().kind == AssignmentOp.PLUSEQ) {
	    if (as.left() instanceof FieldRef) {
		/* Field:
		   Static:
		   -------
		   code for as.left().target()    ; [ref] 
		   if static: pop
		   getstatic                      ; val
		   new StringBuilder              ; val sb
		   dup                            ; val sb sb
		   invokespecial java/lang/StringBuilder()V ; val sb
		   swap                           ; sb val
		   invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuidler; ; sb
		   code for as.right()            ; sb val'
		   invokevirtual java/lang/StringBuilder/append(e.type)Ljava/lang/StringBuidler;   ; sb
		   invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;   ; st
		   putstatic .....
		   
		   Non-static:
		   -----------
		   code for as.left().target()    ; ref
		   dup                            ; ref ref
		   getfield                       ; ref val
		   new StringBuilder              ; ref val sb 
		   dup                            ; ref val sb sb
		   invokespecial java/lang/StringBuilder/<init>()V ; ref val sb
		   swap                           ; ref sb val
		   invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuidler; ; ref sb
		   code for as.right()            ; ref sb val'
		   invokevirtual java/lang/StringBuilder/append(e.type)Ljava/lang/StringBuidler;   ; ref sb
		   invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;   ; ref st
		   putfield
		*/
		
		if (as.left() instanceof FieldRef) {
		    // code for as.left().target()       ; [ref]                                                                             
		    FieldRef fr = (FieldRef)as.left();
		    fr.target().visit(this);
		    if (fr.myDecl.isStatic()) {
			if (fr.target() instanceof NameExpr && !((NameExpr)fr.target()).isClassName())
			    // remove the reference - it is not needed for static fields.                                                 
			    classFile.addInstruction(new Instruction(RuntimeConstants.opc_pop));
			// getstatic ....             ; val                                                                               
			classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getstatic,
									 fr.targetType.typeName(),
									 fr.fieldName().getname(),
									 fr.type.signature()));
		    } else {
			// dup                            ; ref ref                                                                       
			classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
			// getfield ....                  ; ref val                                                                       
			classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getfield,
									 fr.targetType.typeName(),
									 fr.fieldName().getname(),
									 fr.type.signature()));
		    }
		    makeStringBuilder();
		    // swap     ; sb val                                                                                              
		    classFile.addInstruction(new Instruction(RuntimeConstants.opc_swap));
		    // append the value to the string builder: sb                                                                     
		    classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
									     "java/lang/StringBuilder", "append",
									     "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
		    // generate code for as.right(): sb val                                                                           
		    String paramSignature = "";
		    as.right().visit(this);
		    if (as.right().type.isClassType() || as.right().type.isArrayType())
			paramSignature = "Ljava/lang/Object;";
		    else if (as.right().type instanceof PrimitiveType) {
			PrimitiveType pt = (PrimitiveType)as.right().type;
			if (pt.isShortType() || pt.isByteType())
			    paramSignature = "I";
			else
			    paramSignature = pt.signature();
		    } else
			Error.error(as,"No idea what type this is??? (stringBuilder.append) left operand");
		    // generate append call.
		    classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
									     "java/lang/StringBuilder", "append",
									     "("+paramSignature+")Ljava/lang/StringBuilder;"));
		    // now call toString
		    classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
									     "java/lang/StringBuilder", "toString", "()Ljava/lang/String;"));
		    if (fr.myDecl.isStatic()) {
			// put static ....
			classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putstatic,
									 fr.targetType.typeName(),
									 fr.fieldName().getname(),
									 fr.type.signature()));
		    } else {
			// putfield ....                                                                                                  
			classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putfield,
									 fr.targetType.typeName(),
									 fr.fieldName().getname(),
									 fr.type.signature()));
		    }
		} 
	    } else if (as.left() instanceof ArrayAccessExpr) {
		/*
		  --------------------------------
		  array access expression a[i]
		  
		  code for target (a)             ; a 
		  code for index (i)              ; a i
		  dup2                            ; a i a i
		  new StringBuilder               ; a i a i sb
		  dup                             ; a i a i sb sb
		  invokespecial java/lang/StringBuilder/<init>()V   ; a i a i sb
		  dup_x2                          ; a i sb a i sb
		  pop                             ; a i sb a i 
		  aaload                          ; a i sb str
		  invokevirtual java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuidler; ; a i sb 
		  code for as.right()             ; a i sb e   
		  invokevirtual java/lang/StringBuilder/append(e.type)Ljava/lang/StringBuidler;   ; a i sb
		  invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;              ; a i st
		  Xxstore                         ; 
		*/
		
		ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
		// visit target:            aref
		ae.target().visit(this);
		// visit index:             aref idx
		ae.index().visit(this);
		// duplicate target ref and index:     aref idx aref idx
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup2));
		// new StringBuilder        aref idx aref idx stb
		makeStringBuilder();
		// dup the string builder:  aref idx stb aref idx stb
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup_x2));
		// remove sb: aref idx stb aref idx
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_pop));
		// get the value out of the array: aref idx stb str
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_aaload));
		// append string: aref idx stb
   	        classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
                                         "java/lang/StringBuilder", "append",
                                         "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));
		// code for the right part:  aref idx stb val
		as.right().visit(this);
		String paramSignature = "";
		if (as.right().type.isClassType() || as.right().type.isArrayType())
		    paramSignature = "Ljava/lang/Object;";
		else if (as.right().type instanceof PrimitiveType) {
		    PrimitiveType pt = (PrimitiveType)as.right().type;
		    if (pt.isShortType() || pt.isByteType())
			paramSignature = "I";
		    else
			paramSignature = pt.signature();
		} else
		    Error.error(as,"No idea what type this is??? (stringBuilder.append) left operand");
		// generate append call: aref idx stb
		classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
				         "java/lang/StringBuilder", "append",
                                         "("+paramSignature+")Ljava/lang/StringBuilder;"));
		// now call toString: aref idx str
		classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
					     "java/lang/StringBuilder", "toString", "()Ljava/lang/String;"));
		// save the value back into the array
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_aastore));
	    } else {
		// Local or Parameter
		makeStringBuilder();
		// load the left-hand side
		NameExpr left = (NameExpr)as.left();
		int address = ((VarDecl)left.myDecl).address();	    
		classFile.addInstruction(makeLoadStoreInstruction(((VarDecl)left.myDecl).type(), address, true /*load*/, false));
		// add the left-hand side to the string builder
		classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
									 "java/lang/StringBuilder", "append",
									 "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));	     
		// visit right-hand side
		String paramSignature = "";
		as.right().visit(this);
		
		if (as.right().type.isClassType() || as.right().type.isArrayType())
		    paramSignature = "Ljava/lang/Object;";
		else if (as.right().type instanceof PrimitiveType) {
		    PrimitiveType pt = (PrimitiveType)as.right().type;
		    if (pt.isShortType() || pt.isByteType())
			paramSignature = "I";
		    else
			paramSignature = pt.signature();
		} else
		    Error.error(as,"No idea what type this is??? (stringBuilder.append) left operand");
		// generate append call.                                                                      
		classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
									 "java/lang/StringBuilder", "append",
									 "("+paramSignature+")Ljava/lang/StringBuilder;"));
		
		// now call toString
		classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
									 "java/lang/StringBuilder", "toString", "()Ljava/lang/String;"));
		// and store it back into the local
		classFile.addInstruction(makeLoadStoreInstruction(((VarDecl)left.myDecl).type(), address, false /*store*/, false));
	    }
	    classFile.addComment(as, "End Assignment");    
	    return null;
	} // end of code for handling <lhs> += <e> where Type(<lhs>) == String	        
	
	/* If a reference is needed then compute it
	   (If array type then generate reference to the	target & index)
	   - a reference is never needed if as.left() is an instance of a NameExpr
	   - a reference can be computed for a FieldRef by visiting the target
	   - a reference can be computed for an ArrayAccessExpr by visiting its target 
	*/
	if (as.left() instanceof FieldRef) {
	    println(as.line + ": Generating reference for FieldRef target ");
	    FieldRef fr= (FieldRef)as.left();
	    fr.target().visit(this);		
	    // if the target is a New and the field is static, then the reference isn't needed, so pop it! 
	    if (fr.myDecl.isStatic()) // && fr.target() instanceof New) // 3/10/2017 - temporarily commented out
		// issue pop if target is NOT a class name.
		if (fr.target() instanceof NameExpr && (((NameExpr)fr.target()).myDecl instanceof ClassDecl))
		    ;
		else
		    classFile.addInstruction(new Instruction(RuntimeConstants.opc_pop));			
	} else if (as.left() instanceof ArrayAccessExpr) {
	    println(as.line + ": Generating reference for Array Access target");
	    ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
	    classFile.addComment(as, "ArrayAccessExpr target");
	    ae.target().visit(this);
	    classFile.addComment(as, "ArrayAccessExpr index");
	    ae.index().visit(this);
	}

	/* If the assignment operator is <op>= then
	   -- If the left hand side is a non-static field (non array): dup (object ref) + getfield
	   -- If the left hand side is a static field (non array): getstatic   
	   -- If the left hand side is an array reference: dup2 +	Xaload 
	   -- If the left hand side is a local (non array): generate code for it: Xload Y 
	*/		
	if (as.op().kind != AssignmentOp.EQ) {
	    if (as.left() instanceof FieldRef) {
		println(as.line + ": Duplicating reference and getting value for LHS (FieldRef/<op>=)");
		FieldRef fr = (FieldRef)as.left();
		if (!fr.myDecl.isStatic()) {
		    classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
		    classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getfield, fr.targetType.typeName(),
								     fr.fieldName().getname(), fr.type.signature()));
		} else 
		    classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getstatic, fr.targetType.typeName(),
								     fr.fieldName().getname(), fr.type.signature()));
	    } else if (as.left() instanceof ArrayAccessExpr) {
		println(as.line + ": Duplicating reference and getting value for LHS (ArrayAccessRef/<op>=)");
		ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup2));
		classFile.addInstruction(makeLoadStoreInstruction(ae.type, -1, true /*load*/, true /*array*/));
	    } else { // NameExpr
		println(as.line + ": Getting value for LHS (NameExpr/<op>=)");
		NameExpr ne = (NameExpr)as.left();
		int address = ((VarDecl)ne.myDecl).address();

		classFile.addInstruction(makeLoadStoreInstruction(((VarDecl)ne.myDecl).type(), address, true /*load*/, false /*not array*/));
	    }
	}

	/* Visit the right hand side (RHS) */
	boolean oldGenerateDup = generateDup;
	generateDup = true;
	as.right().visit(this);
	generateDup = oldGenerateDup;
	
	/* Convert the right hand sides type to that of the entire assignment */
	if (as.op().kind != AssignmentOp.LSHIFTEQ &&
	    as.op().kind != AssignmentOp.RSHIFTEQ &&
	    as.op().kind != AssignmentOp.RRSHIFTEQ)
	    gen.dataConvert(as.right().type, as.type);

	/* If the assignment operator is <op>= then
	   - Execute the operator
	*/
	if (as.op().kind != AssignmentOp.EQ)
	    classFile.addInstruction(new Instruction(Generator.getBinaryAssignmentOpInstruction(as.op(), as.type)));

	/* If we are the right hand side of an assignment
	   -- If the left hand side is a non-static field (non array): dup_x1/dup2_x1
	   -- If the left hand side is a static field (non array): dup/dup2
	   -- If the left hand side is an array reference: dup_x2/dup2_x2 
	   -- If the left hand side is a local (non array): dup/dup2 
	*/    
	if (generateDup) {
	    String dupInstString = "";
	    if (as.left() instanceof FieldRef) {
		FieldRef fr = (FieldRef)as.left();
		if (!fr.myDecl.isStatic())  
		    dupInstString = "dup" + (fr.type.width() == 2 ? "2" : "") + "_x1";
		else 
		    dupInstString = "dup" + (fr.type.width() == 2 ? "2" : "");
	    } else if (as.left() instanceof ArrayAccessExpr) {
		ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
		dupInstString = "dup" + (ae.type.width() == 2 ? "2" : "") + "_x2";
	    } else { // NameExpr
		NameExpr ne = (NameExpr)as.left();
		dupInstString = "dup" + (ne.type.width() == 2 ? "2" : "");
	    }
	    classFile.addInstruction(new Instruction(Generator.getOpCodeFromString(dupInstString)));
	}

	/* Store
	   -- If LHS is a field: putfield/putstatic
	   -- if LHS is an array reference: Xastore 
	   -- if LHS is a local: Xstore Y
	*/
	if (as.left() instanceof FieldRef) {
	    FieldRef fr = (FieldRef)as.left();
	    if (!fr.myDecl.isStatic()) 
		classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putfield,
								 fr.targetType.typeName(), fr.fieldName().getname(), fr.type.signature()));
	    else 
		classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putstatic,
								 fr.targetType.typeName(), fr.fieldName().getname(), fr.type.signature()));
	} else if (as.left() instanceof ArrayAccessExpr) {
	    ArrayAccessExpr ae = (ArrayAccessExpr)as.left();
	    classFile.addInstruction(makeLoadStoreInstruction(ae.type, -1, false /*store*/, true /*array*/));
	} else { // NameExpr				
	    NameExpr ne = (NameExpr)as.left();
	    int address = ((VarDecl)ne.myDecl).address() ;

	    classFile.addInstruction(makeLoadStoreInstruction(((VarDecl)ne.myDecl).type(), address, false /*store*/, false /*array*/));
	}
	classFile.addComment(as, "End Assignment");
	return null;
    }


    // EXPERIMENTAL
    // only blocks where currentMethod is set should be visited.
    public Object visitBlock(Block bl) {

	// Handle the .var stuff
	String varsTopLabel = "", varsBottomLabel = "";
	if (Settings.generateVars && bl.mySymbolTable.entries.size() > 0)
	    createDotVars(bl.mySymbolTable, varsTopLabel = "Lv"+gen.getLabel(), varsBottomLabel = "Lv"+gen.getLabel());
		
	if (currentContext != null)
	    super.visitBlock(bl);

	// Handle the bottom label for the .vars for this scope
	if (Settings.generateVars && bl.mySymbolTable.entries.size() != 0) {
	    classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, varsBottomLabel));
	}

	return null;
    }
    
    // BINARY EXPRESSION
    public Object visitBinaryExpr(BinaryExpr be) {
	println(be.line + ": BinaryExpr:\tGenerating code for " + be.op().operator() + " :  " + be.left().type.typeName() + " -> " + be.right().type.typeName() + " -> " + be.type.typeName() + ".");
	classFile.addComment(be, "Binary Expression");

	// YOUR CODE HERE
	//taken from typechecker
	//Type lType = (Type) be.left().visit(this);
	//Type rType = (Type) be.right().visit(this);
	//String op = be.op().operator();
	//NameExpr left = (NameExpr)be.left();
	//NameExpr right = (NameExpr)be.right();

	//int address = ((VarDecl)left.myDecl).address();
	
	PrimitiveType newType = (PrimitiveType) be.left().visit(this);
	//sometimes errors here
	gen.dataConvert(be.left().type, newType.ceilingType((PrimitiveType) be.left().type, (PrimitiveType) be.right().type));
	gen.dataConvert(be.right().type, newType.ceilingType((PrimitiveType) be.left().type, (PrimitiveType) be.right().type));

	String opS;
	boolean jumpOnTrue = true;
	//classFile.addInstruction(makeLoadStoreInstruction(((VarDecl)left.myDecl).type(), address, true, false));

	//address = ((VarDecl)right.myDecl).address();	    
	//classFile.addInstruction(makeLoadStoreInstruction(((VarDecl)right.myDecl).type(), address, true, false));
	if(be.op().kind == BinOp.PLUS){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"add")));}
	if(be.op().kind == BinOp.MINUS){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"sub")));}
	if(be.op().kind == BinOp.MULT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"mul")));}
	if(be.op().kind == BinOp.DIV){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"div")));}
	if(be.op().kind == BinOp.MOD){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"rem")));}
	if(be.op().kind == BinOp.AND){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"and")));}
	if(be.op().kind == BinOp.OR){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"or")));}
	if(be.op().kind == BinOp.XOR){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"xor")));}
	if(be.op().kind == BinOp.LSHIFT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"shl")));}
	if(be.op().kind == BinOp.RSHIFT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"shr")));}
	if(be.op().kind == BinOp.RRSHIFT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"ushr")));}
	if(be.op().kind == BinOp.ANDAND){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"and")));}
	if(be.op().kind == BinOp.OROR){classFile.addInstruction(new Instruction(gen.getOpCodeFromString(be.type.getTypePrefix()+"or")));}
	if(be.op().kind == BinOp.INSTANCEOF){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("instanceof")));}	

	//checks if compare
	if(be.op().kind == BinOp.EQEQ || be.op().kind == BinOp.NOTEQ || be.op().kind == BinOp.LT || be.op().kind == BinOp.LTEQ ||
		be.op().kind == BinOp.GT || be.op().kind == BinOp.GTEQ){
			//if(jumpOnTrue){
			//	opS = be.op() + " " + gen.getLabel();
			//}
			//else{
			//	opS = be.op().neg + " " + gen.getLabel();
			//}
			//simliar to book 
			if(be.left().type.isIntegerType()){
				if(be.op().kind == BinOp.EQEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_icmpeq")));}
				if(be.op().kind == BinOp.NOTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_icmpne")));}
				if(be.op().kind == BinOp.LT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_icmplt")));}
				if(be.op().kind == BinOp.GT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_icmpgt")));}
				if(be.op().kind == BinOp.LTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_icmple")));}
				if(be.op().kind == BinOp.GTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_icmpge")));}
			}
			else if(be.left().type.isLongType()){
				classFile.addInstruction(new Instruction(gen.getOpCodeFromString("lcmp")));
				if(be.op().kind == BinOp.EQEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifeq")));}
				if(be.op().kind == BinOp.NOTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifne")));}
				if(be.op().kind == BinOp.LT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("iflt")));}
				if(be.op().kind == BinOp.GT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifgt")));}
				if(be.op().kind == BinOp.LTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifle")));}
				if(be.op().kind == BinOp.GTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifge")));}
			}
			else if(be.left().type.isFloatType()){
				classFile.addInstruction(new Instruction(gen.getOpCodeFromString("fcmpg")));
				if(be.op().kind == BinOp.EQEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifeq")));}
				if(be.op().kind == BinOp.NOTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifne")));}
				if(be.op().kind == BinOp.LT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("iflt")));}
				if(be.op().kind == BinOp.GT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifgt")));}
				if(be.op().kind == BinOp.LTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifle")));}
				if(be.op().kind == BinOp.GTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifge")));}
			}
			else if(be.left().type.isDoubleType()){
				classFile.addInstruction(new Instruction(gen.getOpCodeFromString("dcmpg")));
				if(be.op().kind == BinOp.EQEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifeq")));}
				if(be.op().kind == BinOp.NOTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifne")));}
				if(be.op().kind == BinOp.LT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("iflt")));}
				if(be.op().kind == BinOp.GT){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifgt")));}
				if(be.op().kind == BinOp.LTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifle")));}
				if(be.op().kind == BinOp.GTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("ifge")));}
			}
			else{
				if(be.op().kind == BinOp.EQEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_acmpeq")));}
				if(be.op().kind == BinOp.NOTEQ){classFile.addInstruction(new Instruction(gen.getOpCodeFromString("if_acmpne")));}
			}
	}
	

	//classFile.addInstruction(new Instruction(Generator.getBinaryAssignmentOpInstruction(be.op(), be.type)));

	//
	/*
	Local or Parameter
		makeStringBuilder();
		// load the left-hand side
		NameExpr left = (NameExpr)as.left();
		int address = ((VarDecl)left.myDecl).address();	    
		classFile.addInstruction(makeLoadStoreInstruction(((VarDecl)left.myDecl).type(), address, true /load\, false));
		// add the left-hand side to the string builder
		classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokevirtual,
									 "java/lang/StringBuilder", "append",
									 "(Ljava/lang/String;)Ljava/lang/StringBuilder;"));	     


	    classFile.addInstruction(new Instruction(Generator.getBinaryAssignmentOpInstruction(as.op(), as.type)));

	 */	

	classFile.addComment(be, "End BinaryExpr");
	return null;
    }

    // BREAK STATEMENT
    public Object visitBreakStat(BreakStat br) {
	println(br.line + ": BreakStat:\tGenerating code.");
	classFile.addComment(br, "Break Statement");

	// YOUR CODE HERE

	classFile.addComment(br, "End BreakStat");
	return null;
    }

    // CAST EXPRESSION
    public Object visitCastExpr(CastExpr ce) {
	println(ce.line + ": CastExpr:\tGenerating code for a Cast Expression.");
	classFile.addComment(ce, "Cast Expression");
	//ce.expr().visit(this);
	Type exprType = (Type)ce.expr().visit(this);
	Type castType = ce.type();

	if(exprType != null){
		// Numeric to numeric is always OK.
		if (exprType.isNumericType() && castType.isNumericType()) {
			//ce.type = castType;
			//println(ce.line + ":\tCast Expression has type: " + ce.type);
			classFile.addInstruction(new ClassRefInstruction(RuntimeConstants.opc_checkcast, castType.typeName()));
			//return castType;
		} 
		
		if (exprType.identical(castType)){
			classFile.addInstruction(new ClassRefInstruction(RuntimeConstants.opc_checkcast, castType.typeName()));
		}
		if (exprType.isClassType() && castType.isClassType())
		if (Type.isSuper((ClassType)exprType, (ClassType)castType) ||
			Type.isSuper((ClassType)castType, (ClassType)exprType)) {
			//ce.type = castType;
			//println(ce.line + ":\tCast Expression has type: " + ce.type);
			//return castType;
			classFile.addInstruction(new ClassRefInstruction(RuntimeConstants.opc_checkcast, castType.typeName()));
		}
	}
	// YOUR CODE HERE
	classFile.addComment(ce, "End CastExpr");
	return null;
    }
    
    // CONSTRUCTOR INVOCATION (EXPLICIT)
    public Object visitCInvocation(CInvocation ci) {
	println(ci.line + ": CInvocation:\tGenerating code for Explicit Constructor Invocation.");     
	classFile.addComment(ci, "Explicit Constructor Invocation");

/*
	// YOUR CODE HERE
	if(ci.superConstructorCall()){
		visitConstructorDecl(ci.constructor);
	}
*/

classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0)); //send "this" to stack
		//super.visitCInvocation(ci); //visit self
		//MethodInvocationInstruction                                 (int opCode            , String className   ,String methodName, String signature

    for (int i = 0; i < ci.args().nchildren; i++) {
        ((Expression) ci.args().children[i]).visit(this);
    }

classFile.addInstruction(new MethodInvocationInstruction(RuntimeConstants.opc_invokespecial,ci.targetClass.name(), "<init> ", "()V"));


	classFile.addComment(ci, "End CInvocation");
	return null;
    }






     public Object visitClassDecl(ClassDecl cd) {
	println(cd.line + ": ClassDecl:\tGenerating code for class '" + cd.name() + "'.");

	// YOUR CODE HERE
	// We need to set this here so we can retrieve it when we generate
	// field initializers for an existing constructor.
	currentClass = cd;

    boolean f1 = false; //check if sid
    boolean f2 = false;
    
    	for (int i = 0; i < (cd.body()).nchildren; i++) {
			
		if(((ClassBodyDecl) cd.body().children[i]) instanceof StaticInitDecl){
			f1 = true; //first we need to check if child is part of SID
			//if so its not useful
		}
		
		if(((ClassBodyDecl) cd.body().children[i]) instanceof FieldDecl){
			//aight erhe is a chance
			if(((FieldDecl)((ClassBodyDecl) cd.body().children[i])).getModifiers().isStatic() && (((FieldDecl)((ClassBodyDecl) cd.body().children[i])).var().init() == null) == false){
				//now we check if modifiers are static, and there is somthing inside
			f2 = true;
			//in busness
		}
		}
			
	}
  

    //variables first,
	for (int i = 0; i < (cd.body()).nchildren; i++) {
		if(((ClassBodyDecl) cd.body().children[i]) instanceof FieldDecl){
		((ClassBodyDecl) cd.body().children[i]).visit(this);
	}
	}
	
	//aight read both checks if not in SID and is partr feild decl + other thing
	  if(f1 == false && f2 == true){
	println(cd.line + ": Inserting empty StaticInit into parse tree."); //output to cli
	//todo
	  }
	  
	  
//everything else after
	for (int i = 0; i < (cd.body()).nchildren; i++) {
	if((((ClassBodyDecl) cd.body().children[i]) instanceof FieldDecl) == false){ // == false is better than ! , fight me
		((ClassBodyDecl) cd.body().children[i]).visit(this);
	}	
	}

	return null;
    }
    
    // CONSTRUCTOR DECLARATION //tall //eric
    public Object visitConstructorDecl(ConstructorDecl cd) {
	println(cd.line + ": ConstructorDecl: Generating Code for constructor for class " + cd.name().getname());
	classFile.startMethod(cd);
	classFile.addComment(cd, "Constructor Declaration");
	currentContext = cd;
	cd.params().visit(this);
	// 12/05/13 = removed if (just in case this ever breaks ;-) )
	cd.cinvocation().visit(this);

	// YOUR CODE HERE/////////////////////////----------------------------------
	
	/*
	// explict construct
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_invokespecial));
	// field initalization
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_invokestatic));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_putfield));
	// user code?
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_putfield));
	// auto gen return
	*/

    for (int i = 0; i < currentClass.body().nchildren; i++) {
        if (currentClass.body().children[i] instanceof FieldDecl) { //chercks childrem for feild decl
            if (!((FieldDecl) currentClass.body().children[i]).getModifiers().isStatic() && ((FieldDecl) currentClass.body().children[i]).var().init() != null) {
                println(((FieldDecl) currentClass.body().children[i]).line + ": FieldDecl:\tGenerating init code for non-static field '" + ((FieldDecl) currentClass.body().children[i]).name() + "'.");
                classFile.addComment(((FieldDecl) currentClass.body().children[i]), "Non‑static field '" + ((FieldDecl) currentClass.body().children[i]).name() + "' initializer");
                classFile.addInstruction(new Instruction(RuntimeConstants.opc_aload_0));
                ((FieldDecl) currentClass.body().children[i]).var().init().visit(this);

 classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putfield,((FieldDecl) currentClass.body().children[i]).name(),((FieldDecl) currentClass.body().children[i]).name(),((FieldDecl) currentClass.body().children[i]).type().signature()));
 
            }
        }
    }
    
    
    
 classFile.addComment(cd, "Field Init Generation Start");   
    

    if (cd.body() != null){ //check if body has stuff
        cd.body().visit(this); //if so look at stuff
}

 classFile.addComment(cd, "Field Init Generation End");
	//------------------------------------------------------
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_return));
	// We are done generating code for this method, so transfer it to the classDecl.
	cd.setCode(classFile.getCurrentMethodCode()); // THIS MIGHT BE IMPORTANT TO UNDERSTAND -_-
	classFile.endMethod();
	
	
//println(cd.line + ": ConstructorDecl: Done " + cd.name().getname());
	currentContext = null;
	return null;
    }


    // CONTINUE STATEMENT
    public Object visitContinueStat(ContinueStat cs) {
	println(cs.line + ": ContinueStat:\tGenerating code.");
	classFile.addComment(cs, "Continue Statement");

	// YOUR CODE HERE
	/*
	String topLabel = "L"+gen.getLabel();
	String endLabel = "L"+gen.getLabel();
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
*/

	classFile.addComment(cs, "End ContinueStat");
	return null;
    }

    // DO STATEMENT
    public Object visitDoStat(DoStat ds) {
	println(ds.line + ": DoStat:\tGenerating code.");
	classFile.addComment(ds, "Do Statement");

	// YOUR CODE HERE
	/*
	String topLabel = "L"+gen.getLabel();
	String endLabel = "L"+gen.getLabel();
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
*/
	classFile.addComment(ds, "End DoStat");
	return null; 
    }


    // EXPRESSION STATEMENT
    public Object visitExprStat(ExprStat es) {	
	println(es.line + ": ExprStat:\tVisiting an Expression Statement.");
	classFile.addComment(es, "Expression Statement");

	boolean oldGenerateDup = generateDup;
	// if es.expr() is an assignment make generateDup false, else true
	generateDup = !(es.expression() instanceof Assignment);
       
	es.expression().visit(this);
	if (es.expression() instanceof Invocation) {
	    Invocation in = (Invocation)es.expression();

	    if (in.targetType.isStringType() && in.methodName().getname().equals("length")) {
		println(es.line + ": ExprStat:\tInvocation of method length, return value not uses.");
		gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
	    } else if (in.targetType.isStringType() && in.methodName().getname().equals("charAt")) {
		println(es.line + ": ExprStat:\tInvocation of method charAt, return value not uses.");
		gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
	    } else if (in.targetMethod.returnType().isVoidType())
		println(es.line + ": ExprStat:\tInvocation of Void method where return value is not used anyways (no POP needed)."); 
	    else {
		println(es.line + ": ExprStat:\tPOP added to remove non used return value for a '" + es.expression().getClass().getName() + "'.");
		gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
	    }
	}
	else 
	    if (!(es.expression() instanceof Assignment)) {
		gen.dup(es.expression().type, RuntimeConstants.opc_pop, RuntimeConstants.opc_pop2);
		println(es.line + ": ExprStat:\tPOP added to remove unused value left on stack for a '" + es.expression().getClass().getName() + "'.");
	    }
	classFile.addComment(es, "End ExprStat");
	generateDup = oldGenerateDup;
	return null;
    }

    // FIELD DECLARATION
    public Object visitFieldDecl(FieldDecl fd) {
	println(fd.line + ": FieldDecl:\tGenerating code.");

	classFile.addField(fd);

	return null;
    }

    // FIELD REFERENCE
    public Object visitFieldRef(FieldRef fr) {
	println(fr.line + ": FieldRef:\tGenerating code (getfield code only!).");

	// Changed June 22 2012 Array
	// If we have and field reference with the name 'length' and an array target type
	if (fr.myDecl == null) { // We had a array.length reference. Not the nicest way to check!!
	    classFile.addComment(fr, "Array length");
	    fr.target().visit(this);
	    classFile.addInstruction(new Instruction(RuntimeConstants.opc_arraylength));
	    return null;
	}

	classFile.addComment(fr,  "Field Reference");

	// Note when visiting this node we assume that the field reference
	// is not a left hand side, i.e. we always generate 'getfield' code.

	// Generate code for the target. This leaves a reference on the 
	// stack. pop if the field is static!
	fr.target().visit(this);
	if (!fr.myDecl.modifiers.isStatic()) 
	    classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getfield, 
							     fr.targetType.typeName(), fr.fieldName().getname(), fr.type.signature()));
	else {
	    // If the target is that name of a class and the field is static, then we don't need a pop; else we do:
	    if (!(fr.target() instanceof NameExpr && (((NameExpr)fr.target()).myDecl instanceof ClassDecl))) 
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_pop));
	    classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_getstatic,
							     fr.targetType.typeName(), fr.fieldName().getname(),  fr.type.signature()));
	}
	classFile.addComment(fr, "End FieldRef");
	return null;
    }


    // FOR STATEMENT
    public Object visitForStat(ForStat fs) {
	println(fs.line + ": ForStat:\tGenerating code.");
	classFile.addComment(fs, "For Statement");

/*
	// your code here  //eric here and up
	String topLabel = "L"+gen.getLabel();
	String endLabel = "L"+gen.getLabel();
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));

	classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifeq, topLabel));
*/

	classFile.addComment(fs, "End ForStat");
	

	return null;
    }

    // IF STATEMENT ================================================================
    public Object visitIfStat(IfStat is) {
	println(is.line + ": IfStat:\tGenerating code.");
	classFile.addComment(is, "If Statement");
	
/*	
	String varsTopLabel = "", varsBottomLabel = "";
	String topLabel = "L"+gen.getLabel();
	String endLabel = "L"+gen.getLabel();
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
*/
	
Type eType = (Type) is.expr().visit(this);
	
	if (is.thenpart() != null){ 
	    is.thenpart().visit(this);
	}
	if (is.elsepart() != null) {
	    is.elsepart().visit(this);
}

/*
//taken from book
// Save current Lt and Lf
String oldLt = Lt;
String oldLf = Lf;
Lt = newLabel();
Lf = newLabel();
String Ldone = newLabel();
ifstat.getExpression().visit(this);
gen.add(new LabelInstruction(RTC.opc_label, Lt));
ifstat.getThenStat().visit(this);
if (ifstat.getElseStat() != null){
	gen.add(new JumpInstructions(RTC.opc_goto, Ldone));
}
	gen.add(new LabelInstruction(RTC.opc_label, Lf));

if (ifstat.getElseStat() != null){
	ifstat.getElseStat().visit(this);
	gen.add(new LabelInstruction(RTC.opc_label, Ldone));
}

Lt = oldLt;
Lf = oldLf;
*/
	// YOUR CODE HERE //nick here and down ---------------------------------------------------------------------------------------------------------------------
	classFile.addComment(is,  "End IfStat");
	return null;
    }


    // INVOCATION -----------------------------------------------------------------------------D
    public Object visitInvocation(Invocation in) {
	println(in.line + ": Invocation:\tGenerating code for invoking method '" + in.methodName().getname() + "' in class '" + in.targetType.typeName() + "'.");
	classFile.addComment(in, "Invocation");
	// YOUR CODE HERE

	if (in.targetType.isStringType()) {
		in.params().visit(this); // still need to visit the params if they exist.
	}
	if (in.target() != null){
		in.target().visit(this);
	}
//	if (in.target() != null && in.target().isClassName()){
//	}
	classFile.addComment(in, "End Invocation");
	return null;
    }

    // LITERAL
    public Object visitLiteral(Literal li) {
	println(li.line + ": Literal:\tGenerating code for Literal '" + li.getText() + "'.");
	classFile.addComment(li, "Literal");

	switch (li.getKind()) {
	case Literal.ByteKind:
	case Literal.CharKind:
	case Literal.ShortKind:
	case Literal.IntKind:
	    gen.loadInt(li.getText());
	    break;
	case Literal.NullKind:
	    classFile.addInstruction(new Instruction(RuntimeConstants.opc_aconst_null));
	    break;
	case Literal.BooleanKind:
	    if (li.getText().equals("true")) 
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
	    else
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_0));
	    break;
	case Literal.FloatKind:
	    gen.loadFloat(li.getText());
	    break;
	case Literal.DoubleKind:
	    gen.loadDouble(li.getText());
	    break;
	case Literal.StringKind:
	    gen.loadString(li.getText());
	    break;
	case Literal.LongKind:
	    gen.loadLong(li.getText());
	    break;	    
	}
	classFile.addComment(li,  "End Literal");
	return null;
    }

    // LOCAL VARIABLE DECLARATION -------------------------------------------------------D
    public Object visitLocalDecl(LocalDecl ld) {
	println(ld.line + ": LocalDecl:\tGenerating code for LocalDecl");
	  
	if (ld.var().init() != null) {
	    println(ld.line + ": LocalDecl:\tGenerating code for the initializer for variable '" + 
		    ld.var().name().getname() + "'.");
	    classFile.addComment(ld, "Local Variable Declaration");				

	    // YOUR CODE HERE 
		ld.var().init().visit(this); //visit the things
		classFile.addInstruction(makeLoadStoreInstruction(ld.type(), 1, false /*store*/, false /*array*/));
		classFile.addComment(ld, "End LocalDecl");
	}
	else
	    println(ld.line + ": LocalDecl:\tVisiting local variable declaration for variable '" + ld.var().name().getname() + "'.");
	    
	return null;
    }
    
    // METHOD DECLARATION
    public Object visitMethodDecl(MethodDecl md) {
	println(md.line + ": MethodDecl:\tGenerating code for method '" + md.name().getname() + "'.");	
	classFile.startMethod(md); //sets class for new thehod to be addedd
	currentContext = md;
	classFile.addComment(md, "Method Declaration (" + md.name() + ")");
	
	
	md.params().visit(this); //check all perams
	if (md.block() !=null)  //checks if empty
	    md.block().visit(this); //sees whats inside
	    
	    
	if (md.returnType().isVoidType()){ //check return type if void add to classfile
    classFile.addInstruction(new Instruction(RuntimeConstants.opc_return)); 
}  

	md.setCode(classFile.getCurrentMethodCode()); //needed for 300 & 301
	classFile.endMethod(); //method done, close up shop
	currentContext = null;  //nuke context
	return null; //return = 1; :3
    }


    // NAME EXPRESSION -----------------------------------------------d
    public Object visitNameExpr(NameExpr ne) {
	classFile.addComment(ne, "Name Expression --");

	// ADDED 22 June 2012 
	if (ne.myDecl instanceof ClassDecl) {
	    println(ne.line + ": NameExpr:\tWas a class name - skip it :" + ne.name().getname());
	    classFile.addComment(ne, "End NameExpr");
	    return null;
	}
	
	



	
	
//class taken care of, check local and param
 	if (ne.myDecl instanceof LocalDecl || ne.myDecl instanceof ParamDecl) {
	      println(ne.line + ": NameExpr:\tGenerating code for a local var/param (access) for '" + ne.name() + "'.");
	      //check if local
	      if (ne.myDecl instanceof LocalDecl) { //11
            // Handle loading local variable
			int address = ((VarDecl)ne.myDecl).address() ;
           classFile.addInstruction(makeLoadStoreInstruction((((VarDecl)ne.myDecl).type()), address, true, false));
		  }//11
	}//if





/*
 	if (ne.myDecl instanceof LocalDecl || ne.myDecl instanceof ParamDecl) {
	    ne.type = ((VarDecl)ne.myDecl).type(); 
	}
	else if (ne.myDecl instanceof ClassDecl) {
	    // it wasn't a field - so it must be a class.
	    // if it weren't a class it would have been caught in the 
	    // name resolution phase
	    ne.type = new ClassType(ne.name());
	    // or how about just new ClassType(ne.name()) Einstein!!!
	    ((ClassType)ne.type).myDecl = (ClassDecl)ne.myDecl;
	}
 */


	// YOUR CODE HERE

	classFile.addComment(ne, "End NameExpr");
	return null;
    }

    // NEW  //tall //nick----------------------------------------------------D
    public Object visitNew(New ne) {
	println(ne.line + ": New:\tGenerating code");
	classFile.addComment(ne, "New");
	boolean OldStringBuilderCreated = StringBuilderCreated;
	StringBuilderCreated = false;

	// YOUR CODE HERE
//	public ClassRefInstruction(int opCode, String className) {
classFile.addInstruction(new ClassRefInstruction(RuntimeConstants.opc_new, ne.type.typeName()));
classFile.addInstruction(new Instruction(RuntimeConstants.opc_dup));
	classFile.addComment(ne, "End New");
	StringBuilderCreated = OldStringBuilderCreated;

	return null;
    }

    // RETURN STATEMENT -------------------------------------------------------------------------D
    public Object visitReturnStat(ReturnStat rs) {
	println(rs.line + ": ReturnStat:\tGenerating code.");
	classFile.addComment(rs, "Return Statement");
	//String topLabel = "L"+gen.getLabel();
	//String endLabel = "L"+gen.getLabel();
	//classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
	
	// YOUR CODE HERE
	Type returnValueType = (Type) rs.expr().visit(this);	    
	classFile.addInstruction(new Instruction(gen.getOpCodeFromString(returnValueType.getTypePrefix()+"return")));
	
	classFile.addComment(rs, "End ReturnStat");
	return null;
    }

    // STATIC INITIALIZER //tall //nick -------------------------------------------------------D
    
    public Object visitStaticInitDecl(StaticInitDecl si) {
	println(si.line + ": StaticInit:\tGenerating code for a Static initializer.");	
	classFile.startMethod(si);
	classFile.addComment(si, "Static Initializer");
	currentContext = si;
    classFile.addComment(si, "Field Init Generation Start");
    	
for (int i = 0; i < currentClass.body().nchildren; i++) {
    if ((ClassBodyDecl)currentClass.body().children[i] instanceof FieldDecl) {
        if (((FieldDecl) currentClass.body().children[i]).getModifiers().isStatic() && ((FieldDecl) currentClass.body().children[i]).var().init() != null) {
            println(((FieldDecl)  currentClass.body().children[i]).line + ": FieldDecl:\tGenerating init code for static field '" +((FieldDecl)  currentClass.body().children[i]).name() + "'.");
            ((FieldDecl) currentClass.body().children[i]).var().init().visit(this);
         classFile.addInstruction(new FieldRefInstruction(RuntimeConstants.opc_putstatic, currentClass.name(),((FieldDecl) currentClass.body().children[i]).name(),((FieldDecl) currentClass.body().children[i]).type().signature()));
 
            
        }
//if nonstatic this wont work, need to shove that in the method before here
}


}
    classFile.addComment(si, "Field Init Generation End");
    if (si.initializer() != null){    //00001 Visiting local variable declaration for variable            
        si.initializer().visit(this);
}
    classFile.addInstruction(new Instruction(RuntimeConstants.opc_return));
    gen.setAddress(si.localsUsed);
	si.setCode(classFile.getCurrentMethodCode());
	classFile.endMethod();
	currentContext = null;
	return null;
}


    // SUPER -------------------------------------------------------------------------------D
    public Object visitSuper(Super su) {
	println(su.line + ": Super:\tGenerating code (access).");	
	classFile.addComment(su, "Super");
	//String topLabel = "L"+gen.getLabel();
	//String endLabel = "L"+gen.getLabel();
	//classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
	// YOUR CODE HERE
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iload_0));
	classFile.addComment(su, "End Super");
	return null;
    }

    // SWITCH STATEMENT-----------------------------------------------------------------------+
    public Object visitSwitchStat(SwitchStat ss) {
	println(ss.line + ": Switch Statement:\tGenerating code for Switch Statement.");
	/*
	String topLabel = "L"+gen.getLabel();
	String endLabel = "L"+gen.getLabel();
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
	*/
	
	// YOUR CODE HERE
	//Expresso+
	classFile.addComment(ss, "End SwitchStat");
	return null;
    }

    // TERNARY EXPRESSION  //tall //eric ----------------------------------------------------+
    public Object visitTernary(Ternary te) {
	println(te.line + ": Ternary:\tGenerating code.");
	classFile.addComment(te, "Ternary Statement");

	boolean OldStringBuilderCreated = StringBuilderCreated;
	StringBuilderCreated = false;

	// YOUR CODE HERE
	// Expresso+

	classFile.addComment(te, "Ternary");
	StringBuilderCreated = OldStringBuilderCreated;
	return null;
    }

    // THIS -----------------------------------------------------------------------------D
    public Object visitThis(This th) {
	println(th.line + ": This:\tGenerating code (access).");       
	classFile.addComment(th, "This");
	//String topLabel = "L"+gen.getLabel();
	//String endLabel = "L"+gen.getLabel();
	//classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
	// YOUR CODE HERE
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iload_0));
	classFile.addComment(th, "End This");
	return null;
    }

    // UNARY POST EXPRESSION ---------------------------------------------------------D
    public Object visitUnaryPostExpr(UnaryPostExpr up) {
	println(up.line + ": UnaryPostExpr:\tGenerating code.");
	classFile.addComment(up, "Unary Post Expression");
	String topLabel = "L"+gen.getLabel();
	String endLabel = "L"+gen.getLabel();
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
	// YOUR CODE HERE

/*
	boolean oldLeftHandSide = leftHandSide;
	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
	up.expr().visit(this);
	leftHandSide = oldLeftHandSide;
*/


	Type eType = null;
	eType = (Type) up.expr().visit(this);
if(eType.isShortType()){
	
	}

if(eType.isIntegerType()){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_isub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iadd));
}
	}

if(eType.isByteType()){
	
	}

if(eType.isCharType()){
	
	}

if(eType.isBooleanType()){
	
	}

if(eType.isFloatType()){
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_fload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fsub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fadd));
}
	}

if(eType.isLongType()){
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_lload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_lsub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_ladd));
}
	}

if(eType.isDoubleType()){
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_dload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dsub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dadd));
}
	}

	classFile.addComment(up, "End UnaryPostExpr");
	return null;
    }

    // UNARY PRE EXPRESSION ---------------------------------------------------------------------------------------------------------------D
    public Object visitUnaryPreExpr(UnaryPreExpr up) {
	println(up.line + ": UnaryPreExpr:\tGenerating code for " + up.op().operator() + " : " + up.expr().type.typeName() + " -> " + up.expr().type.typeName() + ".");
	classFile.addComment(up,"Unary Pre Expression");
	//string topLabel = "L"+gen.getLabel();
	//string endLabel = "L"+gen.getLabel();
	//classFile.addInstruction(new LabelInstruction(topLabel));
	// YOUR CODE HERE
	Type eType = null;
	eType = (Type) up.expr().visit(this);
if(eType.isShortType()){
	
	}

if(eType.isIntegerType()){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_isub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_iadd));
}
	}

if(eType.isByteType()){
	
	}

if(eType.isCharType()){
	
	}

if(eType.isBooleanType()){
	
	}

if(eType.isFloatType()){
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_fload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fsub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_fadd));
}
	}

if(eType.isLongType()){
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_lload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_lsub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_lconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_ladd));
}
	}

if(eType.isDoubleType()){
		classFile.addInstruction(new Instruction(RuntimeConstants.opc_dload_0));
	//	leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
if(up.op().getKind() == PreOp.MINUSMINUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dsub));
}
if(up.op().getKind() == PreOp.PLUSPLUS){
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dconst_1));
	classFile.addInstruction(new Instruction(RuntimeConstants.opc_dadd));
}
	}




	//boolean oldLeftHandSide = leftHandSide;
	//leftHandSide = (up.op().getKind() == PreOp.MINUSMINUS || up.op().getKind() == PreOp.PLUSPLUS);
	//up.expr().visit(this);
	//leftHandSide = oldLeftHandSide;




	classFile.addComment(up, "End UnaryPreExpr");
	return null;
    }

    // WHILE STATEMENT --------------------------------------------------------------------------------------------------D
    public Object visitWhileStat(WhileStat ws) {
	println(ws.line + ": While Stat:\tGenerating Code.");

	// YOUR CODE HERE
	
	String topLabel = "L"+gen.getLabel();
	String endLabel = "L"+gen.getLabel();
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
	//ws.expr().visit();
	classFile.addInstruction(new JumpInstruction(RuntimeConstants.opc_ifeq, endLabel));
	if(ws.stat() != null){
		//ws.stat().visit();
	}
	//classFile.addInstruction(new Instruction(RuntimeConstants.opc_goto, topLabel));
	//classFile.addInstruction(new Instruction(RuntimeConstants.opc_goto, endLabel));
    classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, topLabel));
	classFile.addInstruction(new LabelInstruction(RuntimeConstants.opc_label, endLabel));


	classFile.addComment(ws, "While Statement");


	classFile.addComment(ws, "End WhileStat");	
	return null;
    }
}

