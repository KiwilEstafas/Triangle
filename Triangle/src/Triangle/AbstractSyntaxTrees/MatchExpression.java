package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.LinkedHashMap;

public class MatchExpression extends Expression {
    
    public MatchExpression (Expression eAST, LinkedHashMap<Expression, Expression> EList, Expression e2AST, SourcePosition thePosition) {
        super (thePosition);
        E1 = eAST;
        this.EList = EList;
        E2 = e2AST;
    } 
    
    public Object visit (Visitor v, Object o) {
        return v.visitMatchExpression(this, o);
    }
    
    public Expression E1;
    public LinkedHashMap<Expression, Expression> EList;
    public Expression E2;
    

}
