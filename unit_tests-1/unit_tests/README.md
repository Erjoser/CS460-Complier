Welcome! To the new unit test bed (as of January 2023) for the CS 460 Espresso Compiler.

The inspiration for this test bed was quality of life features necessary for the grader and Dr.Pedersen's repeated "suggestions" of a "proper test bed". 

It should be stated, the prior test bed is still good. 
In fact, for some students I do think that the original test beds are still adequate, as they are intended to stoke creativity and show what the compiler is truly capable of.

However, from a graders perspective, these tests were inadequate and many aspects could be improved.

* Descriptive file names
* Focused Testing and Minimized Output
* Expanded Testing and Missing Functionality Checks

----------------------------------------------------------------------------------------------------------------------------------------
1) Descriptive file names
    The original file naming convention (particularly with bad tests) was sporadic and standardless; with the most consistent naming convention being "bad_test*.java". I can't tell you how many times in my grading processes, I had to reopen a test file just to figure out what it was testing for. To be fair, there is a block comment at the begining of every one of those files that described the error and a comment on the line of failure.
    However, I distinctly remember in Dr. Pedersen's class, and I quote, "Why is there a file called BinExpr1.java that doesn't contain any binary expressions? Who knows?" :') As a grader these tests are frustrating to work with and takes away valuable time that I could otherwise spend drinking coffee or making paper airplanes.
    
    Therefore, going into this project, I decided on a file naming scheme (shown below) and class naming standard (shown in 2) that would allow the user to quickly know which visitor method was being evaluated and what feature was of interest.

    xxyy_visitor_name_feature_tested
      x*y*) This could be xxyy xyy xxy or anything that matches the klenee clojure. 
            x* is a decimal value keyed to indicate the visitor method.
            y* is a decimal value keyed to indicate the feature tested.
      visitor_name is a string separated by _'s to indicate the visitor method.  
      feature_name is a string separated by _'s to indicate the feature tested.
    
    I found the numeric encoding, while difficult to expand upon or change without scripting tools, a nice quality of life addition. 
    Aside from reducing the amount of key strokes necessary to enter a test file you could easily target a specific visitor method using the find command. 
    
      E.g. Phase 4, test all features related to visitVar

          find -iwholename "./Espresso/*Tests/04*" -exec sh -c 'path/to/phase4/espressocr $0' {} \;
    
    Since I made sure to apply the naming convention consistently across good and bad tests this command will run specifically all the bad tests then the good tests related to visitVar. As a user this should help immensely; as all you have to do is make this find command a function in your bash profile (or related bash_methods file) and add arguments for the visitor id and path to espressocr. 
    Or you can just scroll through your history, I guess...


2) Focused Testing and Minimized Output
    The original test bed included a non-insignificant amount of tests that were combinations of a multitude of visitors. 
    E.g. A file that declares multiple classes and multiple complex methods and fields per class only to test a for loop or a ternary. 

    This is just a fundamental (flaw? feature?) of Java and most linear programming languages that we have to live with. Therefore, I reasoned "The smaller the program the easier to debug" was the best way to evaluate the compiler (as a grader). Thus I decided to form the test bed as a set of unit tests. 

    Therefore I had to ask myself, "What does the smallest Java program look like?".
    Well that's easily an empty class, but you can't do much with that other than field or method declarations. 
    So what then? Well, add a method.

    public class visitor_name_feature_tested {
      public static void Main(String[] args){

      }
    }

    This basic template seems to be the most logical choice, as it is the basis for any program we write as programmers. However, we aren't developing executable programs until phase 6; and this template heavily relies on method and parameter declaration visits and has unnecessary modifiers (bloat).

    Therefore, I decided on a basic template that would serve me well through phase 5.

    class visitor_name_feature_tested {
      static {

      }
    }

    Using this template program, allowed me to isolate features like loops, conditionals, expressions, and references to a much higher degree than previously possible and produces minimal output. However, I guess it's important to note that this method also has a dependency problem, related to staticInitDecl. But, that is one of the easiest visitors to implement for every phase and is relatively easier for the grader to fix than the methodDecl and paramDecl methods. Remember, that good science demands your only change one variable at a time :)
    
    Minimal output was also desired, as many previous tests would generate huge data dumps that would require the user to evaluate.

3) Expanded testing and Missing Functionality Checks
    Expanded testing wasn't actually a problem I had, until I started developing phase 4. 
    I elaborate on my methodology regarding test creation in the README of 4/ ; but to sum up, students often make poor coding choices and thus require the grader to test their code as though they hadn't used the pre-implemented helper functions. Thus the number of tests in phase 4 alone eclipses almost 400 tests.

    Missing checks was something I had a sneaking suspicion about regarding Espresso* in particular. It struck me as strange that the original test bed didn't have any bad tests for Espresso* and the only good tests were these tremendously large java files (obviously designed for phase 6) and were notoriously difficult to evaluate.


This pretty much wraps up everything I wanted to accomplish with this test bed.

Q: Is it perfect?    A: Probably not
Q: Is it incomplete? A: Probably so

But, It's a lot simpler and verbose than any test bed that came before it ( in my opinion ;) ).

Should the reference compiler ever change, hopefully the current setup will serve as a strong foundation to expand upon.
If you think there are missing checks in the current edition of the testbed, then please email the TA or Dr. Pedersen so we may update it.

-BZ Jan '23
