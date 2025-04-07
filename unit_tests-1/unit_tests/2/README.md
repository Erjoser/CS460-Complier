Test bed for Phase 2 (Parsing) of the the CS 460 Compiler.

This test bed is identical to that of phase 1. The reason being is that the phases both check for the same things.
However, the output from phase 1 to phase 2 will differ. Generaly, this isn't a call to make a deep copy of files, but the way I've written my automated grading systems doesn't accomodate the alternative.

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
