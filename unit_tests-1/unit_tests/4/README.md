Test bed for Phase 4 (Type Checking) of the the CS 460 Compiler.

In effect, is a test bed for all variants of phase 4, that is near 400 tests. With foresight, I thought 100 tests per visitor method would be enough... However, visitAssignment and visitBinaryExpr saw fit to test that upper limit. 
In truth, this testbed need not be so large. If the student uses the proper helper methods when performing type checking, then the test bed could easily be closer 100 or 200 tests.

File name format:
├── Espresso
│   ├── BadTests
│   └── GoodTests
├── Espresso_Plus
│   ├── BadTests
│   └── GoodTests
├── Espresso_Star
│   ├── BadTests
│   └── GoodTests
      *) xxyy_method_feature
        x = method key
        y = test number

        method = {FOR_STAT, NAME_EXPR, CONSTRUCTOR_DECL, etc...}

        feature = (if included) This will just be some string that is descriptive of the feature being tested 

-BZ Jan '23
