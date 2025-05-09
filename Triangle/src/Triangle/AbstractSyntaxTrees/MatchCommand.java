/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;
import Triangle.AbstractSyntaxTrees.Expression;
import Triangle.AbstractSyntaxTrees.CaseCommand;
import java.util.List;

/**
 * Estructura del nodo del AST para el comando Match. Se trabaja con una lista
 * de casos en el cual cada caso puede ser una lista (en el caso de un case con
 * multiples constantes) y su respectivo comando a realizar.
 */
public class MatchCommand extends Command {

    public MatchCommand(Expression eAST, List<CaseCommand> cases, Command otherwise, SourcePosition thePosition) {
        super(thePosition);
        E = eAST;
        this.cases = cases;
        COther = otherwise;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitMatchCommand(this, o);
    }

    public Expression E;       // Expresion que se evalua en el match
    public List<CaseCommand> cases;   // Lista de casos
    public Command COther;     //En el caso del command pueder ser null si no tiene Otherwise

}
