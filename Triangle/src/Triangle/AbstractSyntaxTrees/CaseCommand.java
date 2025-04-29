package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import Triangle.AbstractSyntaxTrees.Command;
import java.util.List;

/**
 * Estructura especial del nodo del AST para manejar los CaseCommand del Match.
 * Se trabaja con una lista de terminales en caso de que un CaseCommand tenga
 * varias constantes en un mismo CASE Se guarda tambien su respectivo comando Si
 * solo fuera con un int o un bool en lugar de una lista es mejor usar un mapa
 * de Hash.
 */
public class CaseCommand extends AST {

    public CaseCommand(List<Expression> constants, Command command, SourcePosition thePosition) {
        super(thePosition);
        this.constants = constants;
        this.command = command;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitCase(this, o);
    }

    public final List<Expression> constants;  // Permite que sean o INT o BOOL
    public final Command command;          // Comando a ejecutar si el caso es seleccionado
}
