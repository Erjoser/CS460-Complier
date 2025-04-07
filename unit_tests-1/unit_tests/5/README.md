Test bed for Phase 5 (Modfiier Checking) of the the CS 460 Compiler.

Modifier checking is a relatively simple phase, especially when compared to type checking. Visitors for Assignment, NameExpr, Super, and This are provided by the handout and can be safely used for "debug printing". 

If in the future, it is discovered that extra tests are needed, feel free to expand this test bed, by following the key system defined below and let the TA or Instructor know to add the new test.

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