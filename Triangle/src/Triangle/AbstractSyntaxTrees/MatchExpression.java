/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.List;

/**
 *
 * @author pkvist03
 * Estructura del Match, pero como expresion.
 * Siempre va a devolver un valor por lo cual el otherwise es obligatorio a
 * diferencia del comando
 */
public class MatchExpression extends Expression {
    
    public MatchExpression(Expression eAST, List<CaseExpression> cases, Expression otherwiseExpression, SourcePosition thePosition) {
        super(thePosition);
        E = eAST;
        this.cases = cases;
        this.EOther = otherwiseExpression;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitMatchExpression(this, o);
    }
    
    public Expression E;                  // Expresion que se evalua en el match
    public List<CaseExpression> cases;    // Lista de casos
    public Expression EOther;             // Otherwise obligatorio
    
}
