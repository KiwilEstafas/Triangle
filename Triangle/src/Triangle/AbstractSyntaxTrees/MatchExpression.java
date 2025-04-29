package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.List;

public class MatchExpression extends Expression {
    
    // Constructor para crear un nodo del AST para una expresión 'match'.
    public MatchExpression(Expression eAST, List<CaseExpression> cases, Expression otherwiseExpression, SourcePosition thePosition) {
        super(thePosition);
        E = eAST;
        this.cases = cases;
        this.EOther = otherwiseExpression;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitMatchExpression(this, o);
    }
    
    public Expression E;
    public List<CaseExpression> cases;
    public Expression EOther;
}
