/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.List;

/**
 *
 * @author p3kvist0
 * EStructura especial de los casos para las expresiones
 * Representa la relacion entre las constantes y la expresion a devolver de cada una
 */
public class CaseExpression extends AST {
    public CaseExpression(List<Expression> constExpressions, Expression resultExpression, SourcePosition thePosition) {
        super(thePosition);
        this.constExpressions = constExpressions;
        this.resultExpression = resultExpression;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitCaseExpression(this, o);
    }
    
    public List<Expression> constExpressions;   
    public Expression resultExpression;            
}
