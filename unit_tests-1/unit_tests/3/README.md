Test bed for Phase 3 (Name Checking) of the CS 460 Compiler Project

The Name Checker phase is one of the simpler phases and doesn't require an excessive amount of testing.

The only caveat to the prior statement, is testing the 4 auxillary methods needed for populating the symbol tables and building/traversing of the heirarchies. In light of this, I've reduced and segmented the test bed to solely avoid those methods, unless absolutely necessary.

If in the future, it is discovered that extra tests are needed, feel free to expand this test bed, by following the key system defined below and let the TA or Instructor know to add the new test.

File name format:
├── Espresso
│   ├── BadTests
│   └── GoodTests

      *) xyz_method_feature
        x = file {ClassAndMemberFinder.java, Namechecker.java}
        y = method key
        z = test number

        method = {FOR_STAT, NAME_EXPR, CONSTRUCTOR_DECL, etc...}

        feature = (if included) This will just be some string that is descriptive of the feature being tested 

└── Espresso_Plus
    ├── BadTests
    └── GoodTests
    
      *) yz_method_feature
        y = method key
        z = test number

        method = {SWITCH_STAT}

        feature = This will just be some string that is descriptive of the feature being tested 

It is important to note, there are no Espress_Star tests. This is because you cannot perform name checking on arrays.
 
-BZ Jan '23