package CodeGenerator;

import AST.*;
import Utilities.Visitor;

class AllocateAddresses extends Visitor {
    
    private Generator gen;
    private ClassDecl currentClass;
    private ClassBodyDecl currentBodyDecl;
    
    AllocateAddresses(Generator g, ClassDecl currentClass, boolean debug) {
	this.debug = debug;
	gen = g;
	this.currentClass = currentClass;
    }

    // BLOCK
    public Object visitBlock(Block bl) {
	// YOUR CODE HERE  //eric
	// from the code in video?
	int tempAddress = gen.getAddress();
	bl.visitChildren(this);
	gen.setAddress(tempAddress);
	return null;   
    }
    
    
    // LOCAL VARIABLE DECLARATION
    public Object visitLocalDecl(LocalDecl ld) {
		
	
	// YOUR CODE HERE //eric
	
	//println(ld.line + ": genaddress:" + gen.getAddress() ); //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& COMMENT OUT LINE LATER
	//assign field based on available address
	ld.address = gen.getAddress();
	//gen.setAddress(gen.getAddress());
		
	//increment counter in the generator by 1 (2 if double or long)
	if(ld.type().isDoubleType() || ld.type().isLongType()){
		gen.inc2Address();
	}
	
	else{
		gen.incAddress();
	}
	
	//ld.localsUsed = gen.getLocalsUsed();
	
//lol the bug was this needed to be called at the end XD
println(ld.line + ": LocalDecl:\tAssigning address:  " + ld.address + " to local variable '" + ld.var().name().getname() + "'.");

	return null;
    }



    // FOR STATEMENT
    public Object visitForStat(ForStat fs) {
	int tempAddress = gen.getAddress();
	fs.visitChildren(this);
	gen.setAddress(tempAddress);
	return null;
    }
    
    // PARAMETER DECLARATION
	// works the same as local
    public Object visitParamDecl(ParamDecl pd) {
	// YOUR CODE HERE //nick and down
	
	
	//println(pd.line + ": ParamDecl:\tAssigning address:  " + pd.address + " to parameter '" + pd.paramName().getname() + "'.");
	//int tempAddress = gen.getAddress();
	//println(pd.line + ": genaddress:" + gen.getAddress() + "   tempAddress:" + tempAddress ); //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& COMMENT OUT LINE LATER
	//pd.visitChildren(this);
	//gen.setAddress(tempAddress);
	
	
		pd.address = gen.getAddress();
	//gen.setAddress(gen.getAddress());
		
	//increment counter in the generator by 1 (2 if double or long)
	if(pd.type().isDoubleType() || pd.type().isLongType()){
		gen.inc2Address();
	}
	
	else{
		gen.incAddress();
	}
	
	//ld.localsUsed = gen.getLocalsUsed();
	
//lol the bug was this needed to be called at the end XD
println(pd.line + ": ParamDecl:\tAssigning address:  " + pd.address + " to parameter '" + pd.paramName().getname() + "'.");

	
	
	return null;
    }
    
    // METHOD DECLARATION
    public Object visitMethodDecl(MethodDecl md) {
	println(md.line + ": MethodDecl:\tResetting address counter for method '" + md.name().getname() + "'.");
	// YOUR CODE HERE
	// set this to 0 if static, 1 if not (0 is this)	
	//if(md.getModifiers().isStatic()){
		//md.address = gen.setAddress(0);
	//	gen.setAddress(0);
	//}
	//else{
		//md.address = gen.setAddress(1);
	//	gen.setAddress(1);
	//}
	int tempAddress = gen.getAddress();
	gen.resetAddress();
	if(md.getModifiers().isStatic()){
	gen.setAddress(0);
	}
	md.visitChildren(this);



	md.localsUsed = gen.getLocalsUsed();
	println("Locals Used: " + gen.getLocalsUsed());	
	println(md.line + ": End MethodDecl");	
	gen.resetAddress();
	return null;
    }
    
    // CONSTRUCTOR DECLARATION
    public Object visitConstructorDecl(ConstructorDecl cd) {	
	println(cd.line + ": ConstructorDecl:\tResetting address counter for constructor '" + cd.name().getname() + "'.");
	gen.resetAddress();
	gen.setAddress(1);
	currentBodyDecl = cd;

	// EXPERIMENTAL: we need to visit the  common constructor here
	// but we can't possibly know what the addresses are gonna be
	// for every visit it completely depends on the number of parameters.
	// This is a problem... Possible Solutions: clone ??
		
	super.visitConstructorDecl(cd);
	cd.localsUsed = gen.getLocalsUsed();
	//System.out.println("Locals Used: " + cd.localsUsed);
	
	
	
	gen.resetAddress();
	println(cd.line + ": End ConstructorDecl");
	return null;
    }
    
    	//println(si.line + ": genaddress:" + gen.getAddress() + "   tempAddress:" + tempAddress ); //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& COMMENT OUT LINE LATER

    
    // STATIC INITIALIZER
    public Object visitStaticInitDecl(StaticInitDecl si) {
	println(si.line + ": StaticInit:\tResetting address counter for static initializer for class '" + currentClass.name() + "'.");
	// YOUR CODE HERE
	int tempAddress = gen.getAddress();
	gen.setAddress(0); //cuz reset address is different
	si.visitChildren(this);
	gen.setAddress(tempAddress);
	//gen.resetAddress();
	println(si.line + ": End StaticInit");
	return null;
    }
}



