interface METHOD_DECL_INTERFACE_ABSTRACT_METHOD {
  abstract void foo();

  // the compiler should redefine these as abstract
  int foo_i();  
  double foo_d();
}