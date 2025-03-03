package NameChecker;

import AST.*;
import Utilities.Error;
import Utilities.SymbolTable;
import Utilities.Visitor;
import Utilities.Rewrite;
import java.util.*;
import Parser.*;;

/**
 * Performs name resultion on all name uses. Defines locals and
 * parameters in symbol tables as well.
 */
public class NameChecker extends Visitor {

    /**
     * Traverses the class hierarchy to look for a method of name
     * 'methodName'. We return a Symboltable if we find any method with the
     * correct name. Since we don't have types yet we cannot look at
     * the signature of the method, so all we do for now is look if
     * any method is defined. The search is as follows:<br><br>
     *
     *  1) look in the current class (the parameter cd).<br>
     *  2) look in its super class.<br>
     *  3) look in all the interfaces.<br>
     * 
     * An entry in the methodTable is a symbol table itself. It holds
     * all entries of the same name, but with different signatures.
     *
     * @param methodName The name of the method for which we are searching.
     * @param cd The class in which we start the search for the
     * method.
     * @return A symbol table with all methods called 'methodName' or null.
     */    
    public static SymbolTable getMethod(String methodName, ClassDecl cd) {	
	// YOUR CODE 
    //in the SymbolTable class, the get function recurses to get parents
        if(cd.methodTable.get(methodName) != null){     
            return cd.methodTable;
        }
	    return null;
    }
    
    /** 
     * Traverses the class hierarchy starting in the class cd looking
     * for a field called 'fieldName'. The search approach is the same
     * getMethod just for fields instead. See {@link
     * #getMethod(String,ClassDecl) findMethod}.
     * @param fieldName The name of the field for which we are searching.
     * @param cd The class where the search starts.
     * @return A FieldDecl if the find was found, null otherwise.
     */
    public static FieldDecl getField(String fieldName, ClassDecl cd) {	
	// YOUR CODE HERE
        if(cd.fieldTable.get(fieldName) != null){     
            return cd.fieldTable.get(fieldName);
        }
	    return null;
    }
    
    /** 
     * Traverses all the classes and interfaces and builds a sequence
     * of the methods of the class hierarchy. Constructors are not
     * included, nor are static initializers.
     *
     * @param cd The ClassDecl from where the travesal starts.
     * @param lst The Sequence to which we add all methods from the
     *        classDecl cd that are of instanceof of class MethodDecl.
     *        (The easiest approach is simply to for-loop through the
     *        body of cd and add all the ClassBodyDecls that are
     *        instanceof of MethodDecl.)
     * @param seenClasses A hash set to hold the names of all the
     *               classes we have seen so far.  This set is used in
     *               the following way: when entering the method check
     *               if the name of cd is already in the set -- if it
     *               is then it is cause we have a circular
     *               inheritance like A :&gt; B :&gt; A -- this is
     *               illegal.  Before we leave the method we remove
     *               the name of cd again.
     */
    public void getClassHierarchyMethods(ClassDecl cd, Sequence lst, HashSet<String> seenClasses) {
	// YOUR CODE HERE
        for(i = 0; i < len(cd.body); i++){
            if(cd.body[i] != seenClasses){
                lst.append(cd.body[i]);
            }
        }
    }
    
    /**
     * For each method (not constructors) in the lst list, check that
     * if it exists more than once with the same parameter signature
     * that they all return something of the same type.
     * @param lst A sequence of MethodDecls.<br><br> 
     *
     * The easiest way to do this is simiply to double-for loop though
     * lst.  A better way is to use a HashTable and use the method
     * name+signature as the key and the return type signature of the
     * method as the value.
    */
    public void checkReturnTypesOfIdenticalMethods(Sequence lst) {
	// YOUR CODE HERE
        for(i = 0; i < len(lst) - 1; i++){
            for(j = i + 1; j < len(lst) - 1; j++){
                if(lst[i] == lst[j]){
                    Error.error(lst[i] + " matches with " + lst[j]);
                }
            }
        }
    }

    /**
     * Checks that a class hierarchy does not contain the same field twice.
     * @param fields A hash set of the fields we have seen so far. Should start out empty from the caller.
     * @param cd The ClassDecl we are currently working with.
     * @param seenClasses: A hash set of all the classes we have already visited. We should not re-visit
     *              a class we have already visited cause any of its fields will cause an error as
     *              they will already be in the fields set. This set also start out empty from the caller.<br><br>
     * There is no need to visit classes that have already been visited cause they have the <b>same</b>
     * fields as already collected. Besides, this can only ever happen for interfaces, and fields
     * in interfaces are final anyways (at least in Espresso). We could never encounter a
     * class again as this would indicate a circular inheritance situation, and we already checked
     * for that.
     */
    public void checkUniqueFields(HashSet<String> fields, ClassDecl cd, HashSet<String> seenClasses) {
	// YOUR CODE HERE
        fields = cd.fieldTable.entries;
        seenClasses.extend(fields);
        checkUniqueFields(fields, cd.children, seenClasses);
    }
    
    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    
    /**
     * Points to the current scope.
     */
    private SymbolTable currentScope;

    /**
     * The global class table. This is set in the constructor.
     */
    private SymbolTable classTable;

    /**
     * The current class in which we are working. This is set in visitClassDecl().
     */
    private ClassDecl   currentClass;

    /**
     * Constructs a NameChecker visitor object.
     * @param classTable The (global) table of classes (stored in {@link Phases.Phase#classTable Phases/Phase.ClassTable}).
     * @param debug Determine if this visitor should produce output.
     */
    public NameChecker(SymbolTable classTable, boolean debug) { 
	this.classTable = classTable; 
	this.debug = debug;
    }
    
    /**
     * Visits a {@link Block}.
     * @param bl A {@link Block} object.
     * <ul>
     * <li> Open a scope.</li>
     * <li> Visit the children.</li>
     * <li> Close the scope.</li>
     *</ul>
     * @return null
     */
    public Object visitBlock(Block bl) {
	println(bl.line + ":\tVisiting a Block.");
	println(bl.line + ":\tCreating new scope for Block.");
	currentScope = currentScope.newScope();
	bl.mySymbolTable = currentScope; // Save the symboltable for producing .var lines
	super.visitBlock(bl);
	currentScope = currentScope.closeScope(); 
	return null;
    }
    
    /** 
     * Visits a {@link ClassDecl}.
     * @param cd A {@link ClassDecl} object.
     * <ul>
     * <li> Set the current Scope to be the field table of cd (this is not necessary but may save a little look-up time).</li>
     * <li> Set the current class to cd.</li>
     * <li> If the class has a super class, check that it is a class and not an interface. (test file: NC5.java)</li>
     * <li> Check that the class does not extend itself (test file: NC6.java).</li>
     * <li> If the class has a super class which has a <b>private</b> default constructor, then the super class cannot be extended (test file: NC7.java).</li>
     * <li> Check that all the implemented interfaces are interfaces and not classes (test file: NC8.java).</li>
     * <li> Visit the children.</li>
     * <li> Call getClassHierarchyMethods(), checkReturnTypesOfIdenticalMethods().</li>
     * <li> Call checkUniqueFields(). </li>
     * <li> Update cd.allMethod to the method sequence computed in getClassHierarchyMethods().</li>
     * <li> Fill cd.constructors with the ConstructorDecls from this class (cd).</li>
     * <li> Call the {@link Utilities.Rewrite} re-writer. This rewriter transforms all name expressions that are really field references into proper FieldRef nodes.</li>
     * </ul>
     * @return null
     */
    public Object visitClassDecl(ClassDecl cd) {
	println(cd.line + ":\tVisiting a ClassDecl.");
	println(cd.line + ":\tVisiting class '"+cd.name()+"'.");
	
	// If we use the field table here as the top scope, then we do not
	// need to look in the field table when we resolve NameExpr. Note,
	// at this time we have not yet rewritten NameExprs which are really
	// FieldRefs with a null target as we have not resolved anything yet.
	currentScope = cd.fieldTable;
	currentClass = cd;
	
	HashSet<String> seenClasses = new HashSet<String>();
	
	// Check that the superclass is a class.
	if (cd.superClass() != null)  {
	    if (cd.superClass().myDecl.isInterface())
		// NC5.java
		Error.error(cd,"Class '" + cd.name() + "' cannot inherit from interface '" +
			    cd.superClass().myDecl.name() + "'.");
	    
	}

	/* This should be moved to Modifier Checker.
	if (cd.superClass() != null) {
	    if (cd.name().equals(cd.superClass().typeName()))
		// NC6.java
		Error.error(cd, "Class '" + cd.name() + "' cannot extend itself.");
	    // If a superclass has a private default constructor, the 
	    // class cannot be extended.
	    ClassDecl superClass = (ClassDecl)classTable.get(cd.superClass().typeName());
	    SymbolTable st = (SymbolTable)superClass.methodTable.get("<init>");
	    ConstructorDecl ccd = (ConstructorDecl)st.get("");
	    if (ccd != null && ccd.getModifiers().isPrivate())
		// NC7.java
		Error.error(cd, "Class '" + superClass.className().getname() + "' cannot be extended because it has a private default constructor.");
	}
	*/
	
	// Check that the interfaces implemented are interfaces.
	for (int i=0; i<cd.interfaces().nchildren; i++) {
	    ClassType ct = (ClassType)cd.interfaces().children[i];
	    if (ct.myDecl.isClass())
		// NC8.java
		Error.error(cd,"Class '" + cd.name() + "' cannot implement class '" + ct.name() + "'.");
	}

	// Visit the children
	super.visitClassDecl(cd);
	
	currentScope = null;
	Sequence methods = new Sequence();
	
	getClassHierarchyMethods(cd, methods, seenClasses);
	checkReturnTypesOfIdenticalMethods(methods);
	
	// All field names can only be used once in a class hierarchy
	seenClasses = new HashSet<String>();
	checkUniqueFields(new HashSet<String>(), cd, seenClasses);
	
	cd.allMethods = methods; // now contains only MethodDecls
	
	// Fill cd.constructors.
	SymbolTable st = (SymbolTable)cd.methodTable.get("<init>");
	ConstructorDecl cod;
	if (st != null) {
	    for (Enumeration<Object> e = st.entries.elements() ; 
		 e.hasMoreElements(); ) {
		cod = (ConstructorDecl)e.nextElement();
		cd.constructors.append(cod);
	    }
	}
	
	// needed for rewriting the tree to replace field references
	// represented by NameExpr.
	println(cd.line + ":\tPerforming tree Rewrite on '" + cd.name() + "'.");
	new Rewrite().go(cd, cd);
	
	return null;
    }


    /** 
     * Visits a {@link ClassType}.
     * @param ct a {@link ClassType} object.
     * <ul>
     * <li> A class types name must be that of an existing class. If no such class exists an error is signalled (test file: NC9.java).</li>
     * <li> Set the myDecl of ct.</li>
     * </ul>
     * @return null
     */
    public Object visitClassType(ClassType ct) {
	println(ct.line + ":\tVisiting a ClassType.");
	// YOUR CODE HERE
    //A class that can be found in the global class table. (classTable.get())
	//ClassDecl superClass = (ClassDecl)classTable.get(cd.superClass().typeName());
	//Error.error(cd,"Class '" + cd.name() + "' cannot implement class '" + ct.name() + "'.");
	String name = ct.getname();
	ClassDecl classcheck = (ClassDecl)classTable.get(name);
	if (classcheck == null){
	Error.error(ct,"Class '" + name + "not found"); //such class exists an error is signalled
	}
	else
	{
	ct.myDecl = classcheck; //Set the myDecl of ct
	}
	return null;
    }
    
    /** 
     * Visits a {@link FieldRef}.
     * @param fr A {@link FieldRef} object.
     * <ul>
     * <li> For null and this targets, call getField with the current
     * class. If no field of the appropriate name is found signal an
     * error (test file: NC10.java).</li>
     * <li> If the target is anything but null or this, simply move on.</li>
     * </ul>
     * @return null
     */
    public Object visitFieldRef(FieldRef fr) {
	println(fr.line + ":\tVisiting a FieldRef.");
	// YOUR CODE HERE
	    String name = fr.getname();
	    ClassDecl class1 = (ClassDecl)classTable.get(name); // check for null
	    if (fr == null || fr == This){
            if(class1 == null){
                Error.error(fr,"Class '" + name + "not found");
            }
            return getField(name, class1);                               //(String fieldName, ClassDecl cd)
	    }
	    return null;
    }

    /**
     * Visits a {@link ForStat}.
     * @param fs A {@link ForStat} object.
     * <ul>
     * <li> A for statement opens a scope that any variable declared in the init part lives in.</li>
     * <li> Visit the children.</li>
     * <li> Close the scope.</li>
     * </ul>
     * @return null
     */
    public Object visitForStat(ForStat fs) {
	println(fs.line + ":\tVisiting a ForStat.");
	// YOUR CODE HERE
	currentScope = currentScope.newScope();
	super.visitForStat(fs);
	currentScope = currentScope.closeScope();
	return null;
    }

    /**
     * Visits a {@link LocalDecl}.
     * @param ld A {@link LocalDecl} object.
     * <ul>
     * <li> Inserts the local decl (ld) into the current scope.</li>
     * </ul>
     * @return null
     */
    public Object visitLocalDecl(LocalDecl ld) {
	println(ld.line + ":\tVisiting a LocalDecl.");
	// YOUR CODE HERE
	string name = ld.getName();
	currentScope.put(name, ld);
	return null;    
    }
    
    /**
     * Visits a {@link MethodDecl}.
     * @param md A {@link MethodDecl} object.  
     * Espresso differs from Java in that it allows parameters and
     * locals to be named the same. For example, the following code is
     * legal in Espresso, but not in Java:
     * <pre>
     *   void f(int x) {
     *     int x;
     *   }</pre>
     * therefore, the parameters must live in their own scope. Note, the { } of a method declaration is a {@link Block} which already opens and closes a scope for the locals.
     * <ul>
     * <li> Open a new scope.</li>
     * <li> Visit the children (the parameters will be visted first and inserted into the newly created scope).</li>
     * <li> Close the scope.</li>
     * </ul>
     * @return null
     */
    public Object visitMethodDecl(MethodDecl md) {
	println(md.line + ":\tVisiting a MethodDecl.");
	// YOUR CODE HERE
	//Open a new scope
	currentScope = currentScope.newScope();
	//Visit the children (the parameters will be visted first and inserted into the newly created scope).
	super.visitMethodDecl(md);
	//Close the scope
	currentScope = currentScope.closeScope();
	
	return null;
    }
    
    /**
     * Visits a {@link ConstructorDecl}.
     * @param cd A {@link ConstructorDecl} object.
     * Like methods in Espresso allows parameters and locals to be named
     * the same, so do constructors. However, the { } representing the body
     * of a constructor is <b>not</b> a {@link Block}, so we must manually
     * open a scope for the locals.
     * <ul>
     * <li> Open a scope (for the parameters).</li>
     * <li> Visit the parameters.</li>
     * <li> Open a scope (for the locals).</li>
     * <li> Visit the explicite constructe invocation (if not null) and the body.</li>
     * <li> Close the scope twice.</li>
     * <li> If the explicite constructor invocation is null and 'cd' has a superclass create a new {@link CInvocation} for the call 'super()' and place it in cd.children[3].</li>
     * </ul>
     * @return null
     */
    public Object visitConstructorDecl(ConstructorDecl cd) {
	println(cd.line + ":\tVisiting a ConstructorDecl.");
	// YOUR CODE HERE
	//Open a new scope
	currentScope = currentScope.newScope();
	//<li> Visit the parameters.</li>
	super.visitConstructorDecl(cd);
	// <li> Open a scope (for the locals).</li>
	currentScope = currentScope.newScope();
	// <li> Visit the explicite constructe invocation (if not null) and the body.</li>
	if(cd.cinvocation() != null){
		cd.cinvocation().visit(this);
	}
	cd.body().visit(this);
	//<li> Close the scope twice.</li>
	currentScope = currentScope.closeScope();
	currentScope = currentScope.closeScope();

	//If the explicite constructor invocation is null and 'cd' has a superclass
	if(cd.cinvocation() == null && cd.superClass() != null){
	//create a new {@link CInvocation} for the call 'super()' and place it in cd.children[3]	
	// public CInvocation(Token cl, Sequence /* of Expression */ args)	
		cd.children[3] = new CInvocation( SUPER ,new Sequence());
	}
}
    
    /**
     * Visits a {@link NameExpr}.
     * @param ne A {@link NameExpr} object.
     * <ul>
     * <li> A name expression can be one of three things:
     *  <ul>
     *  <li> A local or parameter that lives in the scope chan (currentScope.get()).</li>
     *  <li> A field that that can be found in the class hierarchy (getField()).</li>
     *  <li> A class that can be found in the global class table. (classTable.get()).</li>
     *  </ul></li>
     * <li> If the name expression is not found in either of the three places an error should be signalled (test file:NC11.java).</li>
     * <li> Set the myDecl of ne to what was looked up.</li>
     * </ul>
     * @return null
     */
    public Object visitNameExpr(NameExpr ne) {
	println(ne.line + ":\tVisiting NameExpr.");
	// YOUR CODE HERE
	
	//A local or parameter that lives in the scope chan (currentScope.get()).</li>
	if(currentScope.get(ne.getname()) != null){
	//Set the myDecl of ne to what was looked up.
	ne.myDecl = currentScope.get(ne.getname());
	}
	
	//<li> A field that that can be found in the class hierarchy (getField()).</li>
	else if(getField.get(ne.getname()) != null){
	//Set the myDecl of ne to what was looked up.
	ne.myDecl = getField(ne.getname());
	}
	
	// <li> A class that can be found in the global class table. (classTable.get()).</li>
	else if(getField.get(ne.getname()) != null){
	//Set the myDecl of ne to what was looked up.
	ne.myDecl = classTable.get(ne.getname());
	}
	
	else{
	// <li> If the name expression is not found in either of the three places an error should be signalled (test file:NC11.java).</li>
	Error.error("name expression is not found in either of the three places.");
		}
	return null;
    }
    
    /**
     * Visits a {@link Invocation}
     * @param in An {@link Invocation} object.
     * <ul>
     * <li> For null and this targets, call getMethod with the current class. If no method of the appropriate name is found signal an error (test file: NC12.java).</li>
     * <li> For a super target, call getMethod with the current class's superclass's myDecl. if no method if the appropriate name is found signal an error (test file: NC13.java).</li>
     * <li> If the target is anything but null, this, or super, simply move on.</li>
     * </ul>
     * @return null
     */
    public Object visitInvocation(Invocation in) {
	println(in.line + ":\tVisiting an Invocation.");
	// YOUR CODE HERE
	
	String name = in.getname();
	//For null and this targets, call getMethod with the current class.
	if(in.target() == null || in.target == THIS){
		if(getMethod(name, currentClass)==null){
	// If no method of the appropriate name is found signal an error (test file: NC12.java).
	Error.error("no method of the appropriate name is found");
		}
	}

	// For a super target, call getMethod with the current class's superclass's myDecl.
	// if no method if the appropriate name is found signal an error (test file: NC13.java).</li>

	if(in.target() == SUPER){
		if(getMethod(name, currentClass.superClass())==null){
	// If no method of the appropriate name is found signal an error (test file: NC12.java).
	Error.error("no method of the appropriate name is found");
		}
	}
	
	// If the target is anything but null, this, or super, simply move on.</li>
	return null;
    }
    
    /** 
     * Visits a {@link ParamDecl}.
     * @param pd A {@link ParamDecl} object.
     * <ul>
     * <li> Inserts the param decl (pd) into the current scope.</li>
     * </ul>
     * @return null
     */
    public Object visitParamDecl(ParamDecl pd) {
	println(pd.line + ":\tVisiting a ParamDecl.");
	// YOUR CODE HERE
	string name = pd.getName();
	//<li> Inserts the param decl (pd) into the current scope.</li>
	//	put(java.lang.String name, java.lang.Object entry)
	currentScope.put(name, pd);

	return null;
    }

    /**
     * Visits a {@link SwitchStat}.
     * @param st A {@link SwitchStat} object.
     * <ul>
     * <li> Open a scope.</li>
     * <li> Visits the children.</li>
     * <li> Closes the scope.</li>
     * </ul>
     * @return null
     */
    public Object visitSwitchStat(SwitchStat st) {
	println(st.line + ":\tVisiting a SwitchStat.");
	// YOUR CODE HERE
	//Open a new scope
	currentScope = currentScope.newScope();
	//Visit the children
	super.visitSwitchStat(st);
	//Close the scope
	currentScope = currentScope.closeScope();
	
	return null;
    }
    
    /** 
     * Visits a {@link This} object.
     * @param th A {@link This} object.
     * Creates a new {@link ClassType} with the name of the current class. 
     * <ul>
     * <li>Sets the myDecl of thi newly created classType to the current class.</li>
     * </ul>
     * @return null
     */
    public Object visitThis(This th) {      
	println(th.line + ":\tVisiting a This.");      
	ClassType ct = new ClassType(new Name(new Token(16,currentClass.name(),0,0,0)));
	ct.myDecl = currentClass;
	th.type = ct;
	return null;
    }

}

