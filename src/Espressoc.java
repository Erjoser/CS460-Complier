import Utilities.Error;
import Scanner.*;
import Parser.*;
import Phases.*;
import Utilities.Settings;
import AST.*;
import CodeGenerator.WriteFiles;

/**
 * The main driver class of the espresso compiler.
 */
public class Espressoc {
    
    /**
     * Prints the options for the compiler
     */
    public static void usage() {
	System.out.println("Usage: java Espressoc [-ref extension] [-I directory] [-L directory] [-EVM] [-Ttoken] [-Ttree] [-Tsymbol] [-Ttype] [-Tmodifier] [-Tcode] -P:(1|2|3|4|5|6) input");
	System.out.println("       -history. Shows the version history.");
	System.out.println("       -version. Shows the version.");
	System.out.println("       -EVM\tGenerate code for the Espresso Virtual Machine (else code for Java is generated)");
	System.out.println("       -ref extension\tSpecify the extension of the generated Jasmin files (default is 'j')");
	System.out.println("       -I dir\tSet the include directory (Default is Include).");
	System.out.println("       -Ttoken\tPrint the tokens generated by the scanner.");
	System.out.println("       -Ttree\tPrint the parse tree.");
	System.out.println("       -Tsymbol\tProduce detailed output from the name checker.");
	System.out.println("       -Ttype\tProduce detailed output from the type checker"); 
	System.out.println("       -Tmodifier\tProduce detailed output from the modifier checker");
	System.out.println("       -Tcode\tProduce detailed output from the code generator.");
	System.out.println("       -P:X\tRun the all phases of the compiler from phase X and down.");
	System.out.println("       -nocomment\tDoes not produce comments in the jasmin file.");
	System.out.println("           \tX can be 1,2,3,4,5 or 6");
	System.out.println("           \t1/2 : scan and parse. No real difference between 1 and 2 "); 
	System.out.println("           \t3 : like 2 plus the name checker.");
	System.out.println("           \t4 : like 3 plus the type checker.");
	System.out.println("           \t5 : like 4 plus the modifier checker.");
	System.out.println("           \t6 : Run the full compiler (Including code generation).");
	System.out.println("           \t7 : Run the optimizer.");
    }

    /**
     * The maximum phase number that is run.
     */
    public static int phase;
    
    /**
     * Runs the compiler on an input file.
     *
     * @param argv   the command line arguments.
     */
    public static void main(String argv[]) {
	if (argv.length == 0) {
	    System.out.println("Espresso Compiler version XX.YY");
	    usage();
	    System.exit(1);
	}
	
	
	int debugLevel = 0;
	for (int i = 0; i < argv.length; i++) {
	    Scanner s = null;
	    parser p = null;
	    try {
		if ( argv[i].equals("-")) {
		    s = new Scanner( System.in );
		} else if (argv[i].matches("-P:\\d")) {
		    Phase.phase = phase = Integer.parseInt(argv[i].substring(3,4));
		    continue;
		} else if (argv[i].equals("-Tsymbol")) {
		    debugLevel |= 0x0004;
		    continue;
		} else if (argv[i].equals("-Ttoken")) {
		    debugLevel |= 0x0001;
		    continue;
		} else if (argv[i].equals("-Ttree")) {
		    debugLevel |= 0x0002;
		    continue;
		} else if (argv[i].equals("-Ttype")) {
		    debugLevel |= 0x0008;
		    continue;
		} else if (argv[i].equals("-Tcode")) {
		    debugLevel |= 0x0020;
		    continue;
		} else if (argv[i].equals("-Tmodifier")) {
		    debugLevel |= 0x0010;
		    continue;
		} else if (argv[i].equals("-Toptimize")) {
		    debugLevel |= 0x0040;
		    continue;
		} else if (argv[i].equals("-ref")) {
		    if (argv[i+1].charAt(0) == '.')
			argv[i+1] = argv[i+1].substring(1, argv[i+1].length());
		    Settings.fileExt = 	"."+argv[i+1];
		    i++;
		    continue;
		} else if (argv[i].equals("-var")) {
			Settings.generateVars = true;	
		    continue;
		} else if (argv[i].equals("-I")) {
		    if (argv[i+1].charAt(argv[i+1].length()-1) == '/')
			argv[i+1] = argv[i+1].substring(0, argv[i+1].length()-1);
		    Settings.includeDir = argv[i+1];
		    i++;
		    continue;
		} else if (argv[i].equals("-EVM")) {
		    Settings.generateEVMCode = true; // Generate code for the Espresso Virtual Machine
		    continue;
		} else if (argv[i].equals("-help")) {
		    usage();
		    System.exit(1);
		    continue;
		} else if (argv[i].equals("-history")) {
		    System.out.println(Utilities.Version.versionHistory());
		    System.exit(1);
		} else if (argv[i].equals("-version")) {
		    System.out.println("espressoc " + Utilities.Version.getVersion() + "\n" + Utilities.Version.changes[Utilities.Version.changes.length-2]);
		    System.exit(1);
		} else if (argv[i].equals("-nocomment")) {
		    Settings.writeCommentsInJasminFile = false;
		    continue;
		} else {
		    Error.setFileName(argv[i]);
		    s = new Scanner( new java.io.FileReader(argv[i]) );
		}
		p = new parser(s);
	    }
	    catch (java.io.FileNotFoundException e) {
		System.out.println("File not found : \""+argv[i]+"\"");
		System.exit(1);
	    }
	    catch (ArrayIndexOutOfBoundsException e) {
		usage();
	    }
	    
	    switch (phase) {
	    case 1: new Phase1().execute(p, debugLevel, 0x0001); break;
		//<--
	    case 2: new Phase2().execute(p, debugLevel, 0x0003); break;
	    case 3: new Phase3().execute(p, debugLevel, 0x0007); break;
	    case 4: new Phase4().execute(p, debugLevel, 0x000F); break;
	    case 5: new Phase5().execute(p, debugLevel, 0x001F); break;
	    case 6: new Phase6().execute(p, debugLevel, 0x003F); break;
		//<<--
	    case 7: new Phase7().execute(p, debugLevel, 0x007F); break;
		//-->>
		//-->
	    default: System.out.println("Phase " + phase + " does not exist.");
	    }
	    
	    if (phase >= 6) {
		boolean writeOptimizedCode = (phase == 7);
		Compilation program = (Compilation)Phase.root;
		for (int j=0; j<program.types().nchildren;j++) {
		    ClassDecl cd = (ClassDecl)program.types().children[j];
		    if (!Utilities.Settings.generateEVMCode) {
			// do not generate code for Runnable, Object or Thread                                                                        
			if (cd.name().equals("java/lang/Runnable") ||
			    cd.name().equals("java/lang/Thread") ||
			    cd.name().equals("java/lang/Object"))
			    continue;
		    }
		    if (cd.generateCode())
			WriteFiles.writeFile(cd, writeOptimizedCode, Utilities.Settings.writeCommentsInJasminFile);
		}
		
	    }
	    System.out.println("============= S = U = C = C = E = S = S =================");
	}
    }
}


