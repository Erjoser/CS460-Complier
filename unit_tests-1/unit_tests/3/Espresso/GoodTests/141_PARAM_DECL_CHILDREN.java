/* 
 * I'm in doubt as to the need for a super visit on the param decl.
 *
 * In fact, I there is nothing for the visit to target because the 
 * children of a param decl is a Type and a Name.
*/

class PARAM_DECL_CHILDREN {
    PARAM_DECL_CHILDREN(int i /* Something needs to go here, but nothing can */) {}
}