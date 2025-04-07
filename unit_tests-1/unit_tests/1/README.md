Test bed for Phase 1 (Scanning and Parsing) of the the CS 460 Compiler.

This test bed is short and sweet, as there are very few permutations needed to check for a working scanner and parser.
However, it should be noted, that a near infinite number of syntactically incorrect programs do exist; But I don't get paid to write an infinite number of tests :)

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
        *) xxy_method_feature
          xx = method key (decimal)
          y  = test number (decimal)

          method = {FOR_STAT, NAME_EXPR, CONSTRUCTOR_DECL, etc...}
          
          feature = (if included) This will just be some string that is descriptive of the feature being tested

-BZ Jan '23
