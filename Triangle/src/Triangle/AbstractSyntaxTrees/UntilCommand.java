package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class UntilCommand extends Command {
    public Command C;
    public Expression E;

  public UntilCommand(Command cAST, Expression eAST, SourcePosition thePosition) {
    super(thePosition);
    C = cAST;
    E = eAST;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitUntilCommand(this, o);
  }

}
