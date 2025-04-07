/* Depends on visitConstructorDecl */

class A {}

class CLASS_TYPE_CLASS_LOOKUP extends A { }

// interface A {}
// Yields identical code => Not a part of Espresso+
// Will generate error in Phase 4
