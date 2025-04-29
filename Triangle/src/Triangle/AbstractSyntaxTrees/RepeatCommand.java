package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatCommand extends Command {
    public final Command C; // Comando a repetir hasta que la condición sea verdadera.
    public final Expression E; // Expresión booleana que controla la terminación del bucle.

public RepeatCommand(Command cAST, Expression eAST, SourcePosition thePosition) {
    super(thePosition);
    C = cAST;
    E = eAST;
  }

public Object visit(Visitor v, Object o) {
    return v.visitRepeatCommand(this, o);
  }

}