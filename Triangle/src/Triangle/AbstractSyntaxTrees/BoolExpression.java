/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;
import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 *
 * @author estudiante
 */
public class BoolExpression extends Expression {

    public BoolLiteral BL;

    public BoolExpression(BoolLiteral blAST, SourcePosition thePosition) {
        super(thePosition);
        this.BL = blAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitBoolExpression(this, o);
    }
}
