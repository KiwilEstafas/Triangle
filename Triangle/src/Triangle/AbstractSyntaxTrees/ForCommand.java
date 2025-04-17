/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;

/**
 *
 * @author pkvist03
 * Estrucutra del nodo del AST para el For
 * 
 */
import Triangle.SyntacticAnalyzer.SourcePosition;

public class ForCommand extends Command {

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
    public Expression E1;        // Expresión inicial
    public Expression E2;        // Expresión final
    public Command C;            // Cuerpo del ciclo
    public boolean IsDownto;     // true si es 'downto', false si es 'to'

}
