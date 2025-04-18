/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;

/**
 *
 * @author pkvist03
 * Estructura del nodo del AST para el Repeat
 * 
 */
import Triangle.SyntacticAnalyzer.SourcePosition;

public class RepeatCommand extends Command {

    public RepeatCommand(Command bodyAST, Expression condAST, SourcePosition thePosition) {
        super(thePosition);
        body = bodyAST;
        condition = condAST;
    }

    public Object visit(Visitor v, Object o) {
        return v.visitRepeatCommand(this, o);
    }

    public Command body;
    public Expression condition;
}
