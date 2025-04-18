/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Triangle.AbstractSyntaxTrees;
import Triangle.SyntacticAnalyzer.SourcePosition;

/**
 *
 * @author pkvis03
 * Estructura del comando Match para el AST 
 */
public class MatchCommand extends Command {
    
    public MatchCommand(SourcePosition thePosition){
        super(thePosition);
    }

    public Object visit(Visitor v, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
