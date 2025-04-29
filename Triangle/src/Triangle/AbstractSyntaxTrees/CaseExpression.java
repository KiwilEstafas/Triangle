package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.List;

/**
 * Estructura especial de los casos para las expresiones Representa la relacion
 * entre las constantes y la expresion a devolver de cada una
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
