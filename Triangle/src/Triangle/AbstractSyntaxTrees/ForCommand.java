package Triangle.AbstractSyntaxTrees;

/**
 * Estrucutra del nodo del AST para el For
 */
import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForCommand extends Command {
    
    // Constructor para crear un nodo del AST para el comando Repeat.
    public ForCommand(Vname Vast, Expression e1AST, Expression e2AST,
            Command cAST, boolean isDownto, SourcePosition thePosition) {
        super(thePosition);
        V = Vast;
        E1 = e1AST;
        E2 = e2AST;
        C = cAST;
        IsDownto = isDownto;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitForCommand(this, o);
    }

    public Vname V;
    public Expression E1;
    public Expression E2;
    public Command C;
    public boolean IsDownto;
}
