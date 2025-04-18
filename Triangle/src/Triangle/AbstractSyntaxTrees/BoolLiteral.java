/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 *
 * @author pkvist03
 */
public class BoolLiteral extends Terminal  {

    public BoolLiteral (String theSpelling, SourcePosition thePosition) {
    super (theSpelling, thePosition);
  }

  public Object visit (Visitor v, Object o) {
    return v.visitBoolLiteral(this, o);
  }
}
