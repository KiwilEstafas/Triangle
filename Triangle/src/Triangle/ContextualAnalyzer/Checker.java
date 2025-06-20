/*
 * @(#)Checker.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */
package Triangle.ContextualAnalyzer;

import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.*;
import Triangle.SyntacticAnalyzer.SourcePosition;

public final class Checker implements Visitor {

    // Commands
    // Always returns null. Does not use the given object.
    public Object visitAssignCommand(AssignCommand ast, Object o) {
        TypeDenoter vType = (TypeDenoter) ast.V.visit(this, o);
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, o);
        if (!ast.V.variable) {
            reporter.reportError("LHS of assignment is not a variable", "", ast.V.position);
        }
        if (!eType.equals(vType)) {
            reporter.reportError("assignment incompatibilty", "", ast.position);
        }
        return null;
    }

    public Object visitCallCommand(CallCommand ast, Object o) {

        Declaration binding = (Declaration) ast.I.visit(this, null);
        if (binding == null) {
            reportUndeclared(ast.I);
        } else if (binding instanceof ProcDeclaration) {
            ast.APS.visit(this, ((ProcDeclaration) binding).FPS);
        } else if (binding instanceof ProcFormalParameter) {
            ast.APS.visit(this, ((ProcFormalParameter) binding).FPS);
        } else {
            reporter.reportError("\"%\" is not a procedure identifier",
                    ast.I.spelling, ast.I.position);
        }
        return null;
    }

    public Object visitEmptyCommand(EmptyCommand ast, Object o) {
        return null;
    }

    public Object visitIfCommand(IfCommand ast, Object o) {
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        if (!eType.equals(StdEnvironment.booleanType)) {
            reporter.reportError("Boolean expression expected here", "", ast.E.position);
        }
        ast.C1.visit(this, null);
        ast.C2.visit(this, null);
        return null;
    }

    public Object visitLetCommand(LetCommand ast, Object o) {
        idTable.openScope();
        ast.D.visit(this, o);
        ast.C.visit(this, o);
        idTable.closeScope();
        return null;
    }

    public Object visitSequentialCommand(SequentialCommand ast, Object o) {
        ast.C1.visit(this, o);
        ast.C2.visit(this, o);
        return null;
    }

    public Object visitWhileCommand(WhileCommand ast, Object o) {
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        if (!eType.equals(StdEnvironment.booleanType)) {
            reporter.reportError("Boolean expression expected here", "", ast.E.position);
        }
        ast.C.visit(this, null);
        return null;
    }

    /**
     *
     * @param ast
     * @param o
     * @return null Visitor para el AST que se encarga de verificar que los
     * tipos y expresiones sean correctas
     */
    public Object visitForCommand(ForCommand ast, Object o) {
        // Obtiene el tipo de la variable del loop
        TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
        if (!vType.equals(StdEnvironment.integerType)) {
            reporter.reportError("The loop control variable must be of type Integer", "", ast.V.position);
        }

        // Revisa que E1  Integer
        TypeDenoter t1 = (TypeDenoter) ast.E1.visit(this, null);
        if (!t1.equals(StdEnvironment.integerType)) {
            reporter.reportError("The initial expression of the loop must be of type Integer", "", ast.E1.position);
        }

        // Revisa que E2 Integer
        TypeDenoter t2 = (TypeDenoter) ast.E2.visit(this, null);
        if (!t2.equals(StdEnvironment.integerType)) {
            reporter.reportError("The final expression of the loop must be of type Integer", "", ast.E2.position);
        }

        // Revisa el cuerpo del for
        ast.C.visit(this, null);
        return null;
    }

    /**
     * Visita un nodo del AST correspondiente a un comando `repeat`. Su función
     * es verificar que la condición de terminación del bucle sea de tipo
     * booleano.
     *
     * @param ast El nodo del AST que representa el comando `repeat`.
     * @param o Información adicional (no se utiliza en este caso, se pasa
     * `null`).
     * @return null No devuelve ningún valor específico.
     */
    public Object visitRepeatCommand(RepeatCommand ast, Object o) {
        // Visita y analiza el cuerpo del bucle `repeat` (las sentencias que se ejecutan).
        ast.C.visit(this, null);

        // Visita y obtiene el tipo de la expresión de la condición del `repeat`.
        TypeDenoter condType = (TypeDenoter) ast.E.visit(this, null);

        // Verifica si el tipo de la condición es booleano. Si no lo es,
        // reporta un error indicando que la condición del `repeat` debe ser booleana.
        if (!condType.equals(StdEnvironment.booleanType)) {
            reporter.reportError("La condición del Repeat debe ser de tipo Boolean", "", ast.E.position);
        }

        // No se devuelve ningún valor específico después de la verificación.
        return null;
    }

    /**
     * Visita un nodo del AST correspondiente a un comando `until`. Su propósito
     * es asegurar que la condición de terminación del bucle sea de tipo
     * booleano.
     *
     * @param ast El nodo del AST que representa el comando `until`.
     * @param o Información adicional (no se utiliza aquí, se pasa `null`).
     * @return null No retorna ningún valor particular.
     */
    public Object visitUntilCommand(UntilCommand ast, Object o) {
        // Visita y obtiene el tipo de la expresión de la condición del `until`.
        TypeDenoter condType = (TypeDenoter) ast.E.visit(this, null);

        // Verifica si el tipo de la condición es booleano. En caso contrario,
        // notifica un error especificando que la condición del `until` debe ser booleana.
        if (!condType.equals(StdEnvironment.booleanType)) {
            reporter.reportError("La condición del Until debe ser de tipo Boolean", "", ast.E.position);
        }

        // Visita y analiza el cuerpo del bucle `until` (las sentencias a ejecutar).
        ast.C.visit(this, null);

        // No se devuelve ningún valor significativo tras la verificación.
        return null;
    }

    /**
     *
     * @param ast
     * @param o
     * @return null Verifica que el tipo de expresion sea o un numero o un
     * booleano y revisa case por case validando que sus tipos tambien sean
     * validos
     */
    public Object visitMatchCommand(MatchCommand ast, Object o) {
        TypeDenoter exprType = (TypeDenoter) ast.E.visit(this, o);

        if (!(exprType.equals(StdEnvironment.booleanType) || exprType.equals(StdEnvironment.integerType))) {
            reporter.reportError("Type of match expression must be Integer or Boolean", "", ast.position);
        }

        for (CaseCommand cb : ast.cases) {
            cb.visit(this, exprType);
        }

        if (ast.COther != null) {
            ast.COther.visit(this, o);
        }

        return null;
    }

    /**
     *
     * @param ast
     * @param o
     * @return null Revisa el tipo de dato de los cases (int o bool) sean los
     * mismos que el match (Que hablen el mismo idioma) y de paso verifica el
     * comando del case para validar que tambien sea valido.
     */
    public Object visitCase(CaseCommand ast, Object o) {
        for (Expression e : ast.constants) {
            TypeDenoter t = (TypeDenoter) e.visit(this, o);
            if (!t.equals(o)) {
                reporter.reportError("Case constant type does not match match expression type", "", e.position);
            }
        }

        ast.command.visit(this, o); // el comando del case
        return null;
    }

    // Expressions
    // Returns the TypeDenoter denoting the type of the expression. Does
    // not use the given object.
    public Object visitArrayExpression(ArrayExpression ast, Object o) {
        TypeDenoter elemType = (TypeDenoter) ast.AA.visit(this, null);
        IntegerLiteral il = new IntegerLiteral(new Integer(ast.AA.elemCount).toString(),
                ast.position);
        ast.type = new ArrayTypeDenoter(il, elemType, ast.position);
        return ast.type;
    }

    public Object visitBinaryExpression(BinaryExpression ast, Object o) {

        TypeDenoter e1Type = (TypeDenoter) ast.E1.visit(this, o);
        TypeDenoter e2Type = (TypeDenoter) ast.E2.visit(this, o);
        Declaration binding = (Declaration) ast.O.visit(this, o);

        if (binding == null) {
            reportUndeclared(ast.O);
        } else {
            if (!(binding instanceof BinaryOperatorDeclaration)) {
                reporter.reportError("\"%\" is not a binary operator",
                        ast.O.spelling, ast.O.position);
            }
            BinaryOperatorDeclaration bbinding = (BinaryOperatorDeclaration) binding;
            if (bbinding.ARG1 == StdEnvironment.anyType) {
                // this operator must be "=" or "\="
                if (!e1Type.equals(e2Type)) {
                    reporter.reportError("incompatible argument types for \"%\"",
                            ast.O.spelling, ast.position);
                }
            } else if (!e1Type.equals(bbinding.ARG1)) {
                reporter.reportError("wrong argument type for \"%\"",
                        ast.O.spelling, ast.E1.position);
            } else if (!e2Type.equals(bbinding.ARG2)) {
                reporter.reportError("wrong argument type for \"%\"",
                        ast.O.spelling, ast.E2.position);
            }
            ast.type = bbinding.RES;
        }
        return ast.type;
    }

    public Object visitCallExpression(CallExpression ast, Object o) {
        Declaration binding = (Declaration) ast.I.visit(this, null);
        if (binding == null) {
            reportUndeclared(ast.I);
            ast.type = StdEnvironment.errorType;
        } else if (binding instanceof FuncDeclaration) {
            ast.APS.visit(this, ((FuncDeclaration) binding).FPS);
            ast.type = ((FuncDeclaration) binding).T;
        } else if (binding instanceof FuncFormalParameter) {
            ast.APS.visit(this, ((FuncFormalParameter) binding).FPS);
            ast.type = ((FuncFormalParameter) binding).T;
        } else {
            reporter.reportError("\"%\" is not a function identifier",
                    ast.I.spelling, ast.I.position);
        }
        return ast.type;
    }

    public Object visitCharacterExpression(CharacterExpression ast, Object o) {
        ast.type = StdEnvironment.charType;
        return ast.type;
    }

    public Object visitBoolExpression(BoolExpression ast, Object o) {
        ast.type = StdEnvironment.booleanType;
        return ast.type;
    }

    public Object visitEmptyExpression(EmptyExpression ast, Object o) {
        ast.type = null;
        return ast.type;
    }

    public Object visitIfExpression(IfExpression ast, Object o) {
        TypeDenoter e1Type = (TypeDenoter) ast.E1.visit(this, null);
        if (!e1Type.equals(StdEnvironment.booleanType)) {
            reporter.reportError("Boolean expression expected here", "",
                    ast.E1.position);
        }
        TypeDenoter e2Type = (TypeDenoter) ast.E2.visit(this, null);
        TypeDenoter e3Type = (TypeDenoter) ast.E3.visit(this, null);
        if (!e2Type.equals(e3Type)) {
            reporter.reportError("incompatible limbs in if-expression", "", ast.position);
        }
        ast.type = e2Type;
        return ast.type;
    }

    public Object visitIntegerExpression(IntegerExpression ast, Object o) {
        ast.type = StdEnvironment.integerType;
        return ast.type;
    }

    public Object visitLetExpression(LetExpression ast, Object o) {
        idTable.openScope();
        ast.D.visit(this, null);
        ast.type = (TypeDenoter) ast.E.visit(this, null);
        idTable.closeScope();
        return ast.type;
    }

    public Object visitRecordExpression(RecordExpression ast, Object o) {
        FieldTypeDenoter rType = (FieldTypeDenoter) ast.RA.visit(this, null);
        ast.type = new RecordTypeDenoter(rType, ast.position);
        return ast.type;
    }

    public Object visitUnaryExpression(UnaryExpression ast, Object o) {

        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        Declaration binding = (Declaration) ast.O.visit(this, null);
        if (binding == null) {
            reportUndeclared(ast.O);
            ast.type = StdEnvironment.errorType;
        } else if (!(binding instanceof UnaryOperatorDeclaration)) {
            reporter.reportError("\"%\" is not a unary operator",
                    ast.O.spelling, ast.O.position);
        } else {
            UnaryOperatorDeclaration ubinding = (UnaryOperatorDeclaration) binding;
            if (!eType.equals(ubinding.ARG)) {
                reporter.reportError("wrong argument type for \"%\"",
                        ast.O.spelling, ast.O.position);
            }
            ast.type = ubinding.RES;
        }
        return ast.type;
    }

    public Object visitVnameExpression(VnameExpression ast, Object o) {
        ast.type = (TypeDenoter) ast.V.visit(this, o);
        return ast.type;
    }

    /**
     * Visita un nodo del AST correspondiente a una expresión `match`. Su
     * función es verificar la coherencia de tipos dentro de la expresión
     * `match`, asegurando que la expresión a comparar sea de tipo entero o
     * booleano, que las constantes en los `case` coincidan en tipo con la
     * expresión principal, y que todas las expresiones de resultado (tanto en
     * los `case` como en el `otherwise`) tengan el mismo tipo.
     *
     * @param ast El nodo del AST que representa la expresión `match`.
     * @param o Información adicional (se pasa tal cual a las visitas de los
     * sub-nodos).
     * @return TypeDenoter El tipo resultante de la expresión `match` (el tipo
     * común de los resultados de los `case` y el `otherwise`).
     */
    // Match Expression
  public Object visitMatchExpression(MatchExpression ast, Object o) {
   
      TypeDenoter matchType = (TypeDenoter) ast.E1.visit(this, null);
    TypeDenoter caseExpressionType = null;
    // La expresión principal debe ser de tipo Integer o Boolean
    if (!matchType.equals(StdEnvironment.integerType) && !matchType.equals(StdEnvironment.booleanType)) {
        reporter.reportError("Match expression must be of type Integer or Boolean", "", ast.E1.position);
    }

    // Verificar cada caso en la lista de casos
    for (Expression caseLiteral : ast.EList.keySet()) {
        TypeDenoter caseLiteralType = (TypeDenoter) caseLiteral.visit(this, null);
        if (!caseLiteralType.equals(matchType)) {
            reporter.reportError("Case literal type does not match match expression type", "", caseLiteral.position);
        }

        // Verificar el tipo de la expresión asociada al caso
        Expression caseExpression = ast.EList.get(caseLiteral);
        caseExpressionType = (TypeDenoter)caseExpression.visit(this, null);
    }

    // Verificar otherwise
    ast.E2.visit(this, null);
    if (!ast.E2.type.equals(caseExpressionType) && caseExpressionType != null) {
        reporter.reportError("Otherwise expression type does not match match expression type", "", ast.E2.position);
    }
    
    ast.type = StdEnvironment.integerType;
    return ast.type;
  }
    public Object visitCaseExpression(CaseExpression ast, Object o) {
        return null;
    }

    // Declarations
    // Always returns null. Does not use the given object.
    public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object o) {
        return null;
    }

    public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        idTable.enter(ast.I.spelling, ast);
        if (ast.duplicated) {
            reporter.reportError("identifier \"%\" already declared",
                    ast.I.spelling, ast.position);
        }
        return null;
    }

    public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        idTable.enter(ast.I.spelling, ast); // permits recursion
        if (ast.duplicated) {
            reporter.reportError("identifier \"%\" already declared",
                    ast.I.spelling, ast.position);
        }
        idTable.openScope();
        ast.FPS.visit(this, null);
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        idTable.closeScope();
        if (!ast.T.equals(eType)) {
            reporter.reportError("body of function \"%\" has wrong type",
                    ast.I.spelling, ast.E.position);
        }
        return null;
    }

    public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
        idTable.enter(ast.I.spelling, ast); // permits recursion
        if (ast.duplicated) {
            reporter.reportError("identifier \"%\" already declared",
                    ast.I.spelling, ast.position);
        }
        idTable.openScope();
        ast.FPS.visit(this, null);
        ast.C.visit(this, null);
        idTable.closeScope();
        return null;
    }

    public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
        ast.D1.visit(this, null);
        ast.D2.visit(this, null);
        return null;
    }

    public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        idTable.enter(ast.I.spelling, ast);
        if (ast.duplicated) {
            reporter.reportError("identifier \"%\" already declared",
                    ast.I.spelling, ast.position);
        }
        return null;
    }

    public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object o) {
        return null;
    }

    public Object visitVarDeclaration(VarDeclaration ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, o);
        idTable.enter(ast.I.spelling, ast);
        if (ast.duplicated) {
            reporter.reportError("identifier \"%\" already declared",
                    ast.I.spelling, ast.position);
        }

        return null;
    }

    // Array Aggregates
    // Returns the TypeDenoter for the Array Aggregate. Does not use the
    // given object.
    public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object o) {
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        TypeDenoter elemType = (TypeDenoter) ast.AA.visit(this, null);
        ast.elemCount = ast.AA.elemCount + 1;
        if (!eType.equals(elemType)) {
            reporter.reportError("incompatible array-aggregate element", "", ast.E.position);
        }
        return elemType;
    }

    public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
        TypeDenoter elemType = (TypeDenoter) ast.E.visit(this, null);
        ast.elemCount = 1;
        return elemType;
    }

    // Record Aggregates
    // Returns the TypeDenoter for the Record Aggregate. Does not use the
    // given object.
    public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object o) {
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        FieldTypeDenoter rType = (FieldTypeDenoter) ast.RA.visit(this, null);
        TypeDenoter fType = checkFieldIdentifier(rType, ast.I);
        if (fType != StdEnvironment.errorType) {
            reporter.reportError("duplicate field \"%\" in record",
                    ast.I.spelling, ast.I.position);
        }
        ast.type = new MultipleFieldTypeDenoter(ast.I, eType, rType, ast.position);
        return ast.type;
    }

    public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object o) {
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        ast.type = new SingleFieldTypeDenoter(ast.I, eType, ast.position);
        return ast.type;
    }

    // Formal Parameters
    // Always returns null. Does not use the given object.
    public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        idTable.enter(ast.I.spelling, ast);
        if (ast.duplicated) {
            reporter.reportError("duplicated formal parameter \"%\"",
                    ast.I.spelling, ast.position);
        }
        return null;
    }

    public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
        idTable.openScope();
        ast.FPS.visit(this, null);
        idTable.closeScope();
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        idTable.enter(ast.I.spelling, ast);
        if (ast.duplicated) {
            reporter.reportError("duplicated formal parameter \"%\"",
                    ast.I.spelling, ast.position);
        }
        return null;
    }

    public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
        idTable.openScope();
        ast.FPS.visit(this, null);
        idTable.closeScope();
        idTable.enter(ast.I.spelling, ast);
        if (ast.duplicated) {
            reporter.reportError("duplicated formal parameter \"%\"",
                    ast.I.spelling, ast.position);
        }
        return null;
    }

    public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        idTable.enter(ast.I.spelling, ast);
        if (ast.duplicated) {
            reporter.reportError("duplicated formal parameter \"%\"",
                    ast.I.spelling, ast.position);
        }
        return null;
    }

    public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object o) {
        return null;
    }

    public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object o) {
        ast.FP.visit(this, null);
        ast.FPS.visit(this, null);
        return null;
    }

    public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object o) {
        ast.FP.visit(this, null);
        return null;
    }

    // Actual Parameters
    // Always returns null. Uses the given FormalParameter.
    public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
        FormalParameter fp = (FormalParameter) o;
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);

        if (!(fp instanceof ConstFormalParameter)) {
            reporter.reportError("const actual parameter not expected here", "",
                    ast.position);
        } else if (!eType.equals(((ConstFormalParameter) fp).T)) {
            reporter.reportError("wrong type for const actual parameter", "",
                    ast.E.position);
        }
        return null;
    }

    public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
        FormalParameter fp = (FormalParameter) o;

        Declaration binding = (Declaration) ast.I.visit(this, null);
        if (binding == null) {
            reportUndeclared(ast.I);
        } else if (!(binding instanceof FuncDeclaration
                || binding instanceof FuncFormalParameter)) {
            reporter.reportError("\"%\" is not a function identifier",
                    ast.I.spelling, ast.I.position);
        } else if (!(fp instanceof FuncFormalParameter)) {
            reporter.reportError("func actual parameter not expected here", "",
                    ast.position);
        } else {
            FormalParameterSequence FPS = null;
            TypeDenoter T = null;
            if (binding instanceof FuncDeclaration) {
                FPS = ((FuncDeclaration) binding).FPS;
                T = ((FuncDeclaration) binding).T;
            } else {
                FPS = ((FuncFormalParameter) binding).FPS;
                T = ((FuncFormalParameter) binding).T;
            }
            if (!FPS.equals(((FuncFormalParameter) fp).FPS)) {
                reporter.reportError("wrong signature for function \"%\"",
                        ast.I.spelling, ast.I.position);
            } else if (!T.equals(((FuncFormalParameter) fp).T)) {
                reporter.reportError("wrong type for function \"%\"",
                        ast.I.spelling, ast.I.position);
            }
        }
        return null;
    }

    public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
        FormalParameter fp = (FormalParameter) o;

        Declaration binding = (Declaration) ast.I.visit(this, null);
        if (binding == null) {
            reportUndeclared(ast.I);
        } else if (!(binding instanceof ProcDeclaration
                || binding instanceof ProcFormalParameter)) {
            reporter.reportError("\"%\" is not a procedure identifier",
                    ast.I.spelling, ast.I.position);
        } else if (!(fp instanceof ProcFormalParameter)) {
            reporter.reportError("proc actual parameter not expected here", "",
                    ast.position);
        } else {
            FormalParameterSequence FPS = null;
            if (binding instanceof ProcDeclaration) {
                FPS = ((ProcDeclaration) binding).FPS;
            } else {
                FPS = ((ProcFormalParameter) binding).FPS;
            }
            if (!FPS.equals(((ProcFormalParameter) fp).FPS)) {
                reporter.reportError("wrong signature for procedure \"%\"",
                        ast.I.spelling, ast.I.position);
            }
        }
        return null;
    }

    public Object visitVarActualParameter(VarActualParameter ast, Object o) {
        FormalParameter fp = (FormalParameter) o;

        TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
        if (!ast.V.variable) {
            reporter.reportError("actual parameter is not a variable", "",
                    ast.V.position);
        } else if (!(fp instanceof VarFormalParameter)) {
            reporter.reportError("var actual parameter not expected here", "",
                    ast.V.position);
        } else if (!vType.equals(((VarFormalParameter) fp).T)) {
            reporter.reportError("wrong type for var actual parameter", "",
                    ast.V.position);
        }
        return null;
    }

    public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object o) {
        FormalParameterSequence fps = (FormalParameterSequence) o;
        if (!(fps instanceof EmptyFormalParameterSequence)) {
            reporter.reportError("too few actual parameters", "", ast.position);
        }
        return null;
    }

    public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object o) {
        FormalParameterSequence fps = (FormalParameterSequence) o;
        if (!(fps instanceof MultipleFormalParameterSequence)) {
            reporter.reportError("too many actual parameters", "", ast.position);
        } else {
            ast.AP.visit(this, ((MultipleFormalParameterSequence) fps).FP);
            ast.APS.visit(this, ((MultipleFormalParameterSequence) fps).FPS);
        }
        return null;
    }

    public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object o) {
        FormalParameterSequence fps = (FormalParameterSequence) o;
        if (!(fps instanceof SingleFormalParameterSequence)) {
            reporter.reportError("incorrect number of actual parameters", "", ast.position);
        } else {
            ast.AP.visit(this, ((SingleFormalParameterSequence) fps).FP);
        }
        return null;
    }

    // Type Denoters
    // Returns the expanded version of the TypeDenoter. Does not
    // use the given object.
    public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
        return StdEnvironment.anyType;
    }

    public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        if ((Integer.valueOf(ast.IL.spelling).intValue()) == 0) {
            reporter.reportError("arrays must not be empty", "", ast.IL.position);
        }
        return ast;
    }

    public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
        return StdEnvironment.booleanType;
    }

    public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
        return StdEnvironment.charType;
    }

    public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
        return StdEnvironment.errorType;
    }

    public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object o) {
        Declaration binding = (Declaration) ast.I.visit(this, o);
        if (binding == null) {
            reportUndeclared(ast.I);
            return StdEnvironment.errorType;
        } else if (!(binding instanceof TypeDeclaration)) {
            reporter.reportError("\"%\" is not a type identifier",
                    ast.I.spelling, ast.I.position);
            return StdEnvironment.errorType;
        }
        return ((TypeDeclaration) binding).T;
    }

    public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
        return StdEnvironment.integerType;
    }

    public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
        ast.FT = (FieldTypeDenoter) ast.FT.visit(this, null);
        return ast;
    }

    public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        ast.FT.visit(this, null);
        return ast;
    }

    public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object o) {
        ast.T = (TypeDenoter) ast.T.visit(this, null);
        return ast;
    }

    // Literals, Identifiers and Operators
    public Object visitCharacterLiteral(CharacterLiteral CL, Object o) {
        return StdEnvironment.charType;
    }

    public Object visitIdentifier(Identifier I, Object o) {
        Declaration binding = idTable.retrieve(I.spelling);
        if (binding != null) {
            I.decl = binding;
        }
        return binding;
    }

    public Object visitIntegerLiteral(IntegerLiteral IL, Object o) {
        return StdEnvironment.integerType;
    }

    public Object visitOperator(Operator O, Object o) {
        Declaration binding = idTable.retrieve(O.spelling);
        if (binding != null) {
            O.decl = binding;
        }
        return binding;
    }

    public Object visitBoolLiteral(BoolLiteral BL, Object o) {
        return StdEnvironment.booleanType;
    }

    // Value-or-variable names
    // Determines the address of a named object (constant or variable).
    // This consists of a base object, to which 0 or more field-selection
    // or array-indexing operations may be applied (if it is a record or
    // array).  As much as possible of the address computation is done at
    // compile-time. Code is generated only when necessary to evaluate
    // index expressions at run-time.
    // currentLevel is the routine level where the v-name occurs.
    // frameSize is the anticipated size of the local stack frame when
    // the object is addressed at run-time.
    // It returns the description of the base object.
    // offset is set to the total of any field offsets (plus any offsets
    // due to index expressions that happen to be literals).
    // indexed is set to true iff there are any index expressions (other
    // than literals). In that case code is generated to compute the
    // offset due to these indexing operations at run-time.
    // Returns the TypeDenoter of the Vname. Does not use the
    // given object.
    public Object visitDotVname(DotVname ast, Object o) {
        ast.type = null;
        TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
        ast.variable = ast.V.variable;
        if (!(vType instanceof RecordTypeDenoter)) {
            reporter.reportError("record expected here", "", ast.V.position);
        } else {
            ast.type = checkFieldIdentifier(((RecordTypeDenoter) vType).FT, ast.I);
            if (ast.type == StdEnvironment.errorType) {
                reporter.reportError("no field \"%\" in this record type",
                        ast.I.spelling, ast.I.position);
            }
        }
        return ast.type;
    }

    public Object visitSimpleVname(SimpleVname ast, Object o) {
        ast.variable = false;
        ast.type = StdEnvironment.errorType;
        Declaration binding = (Declaration) ast.I.visit(this, o);
        if (binding == null) {
            reportUndeclared(ast.I);
        } else if (binding instanceof ConstDeclaration) {
            ast.type = ((ConstDeclaration) binding).E.type;
            ast.variable = false;
        } else if (binding instanceof VarDeclaration) {
            ast.type = ((VarDeclaration) binding).T;
            ast.variable = true;
        } else if (binding instanceof ConstFormalParameter) {
            ast.type = ((ConstFormalParameter) binding).T;
            ast.variable = false;
        } else if (binding instanceof VarFormalParameter) {
            ast.type = ((VarFormalParameter) binding).T;
            ast.variable = true;
        } else {
            reporter.reportError("\"%\" is not a const or var identifier",
                    ast.I.spelling, ast.I.position);
        }
        return ast.type;
    }

    public Object visitSubscriptVname(SubscriptVname ast, Object o) {
        TypeDenoter vType = (TypeDenoter) ast.V.visit(this, null);
        ast.variable = ast.V.variable;
        TypeDenoter eType = (TypeDenoter) ast.E.visit(this, null);
        if (vType != StdEnvironment.errorType) {
            if (!(vType instanceof ArrayTypeDenoter)) {
                reporter.reportError("array expected here", "", ast.V.position);
            } else {
                if (!eType.equals(StdEnvironment.integerType)) {
                    reporter.reportError("Integer expression expected here", "",
                            ast.E.position);
                }
                ast.type = ((ArrayTypeDenoter) vType).T;
            }
        }
        return ast.type;
    }

    // Programs
    public Object visitProgram(Program ast, Object o) {
        ast.C.visit(this, o);
        return null;
    }

    // Checks whether the source program, represented by its AST, satisfies the
    // language's scope rules and type rules.
    // Also decorates the AST as follows:
    //  (a) Each applied occurrence of an identifier or operator is linked to
    //      the corresponding declaration of that identifier or operator.
    //  (b) Each expression and value-or-variable-name is decorated by its type.
    //  (c) Each type identifier is replaced by the type it denotes.
    // Types are represented by small ASTs.
    public void check(Program ast) {
        ast.visit(this, null);
    }

    /////////////////////////////////////////////////////////////////////////////
    public Checker(ErrorReporter reporter) {
        this.reporter = reporter;
        this.idTable = new IdentificationTable();
        establishStdEnvironment();
    }

    private IdentificationTable idTable;
    private static SourcePosition dummyPos = new SourcePosition();
    private ErrorReporter reporter;

    // Reports that the identifier or operator used at a leaf of the AST
    // has not been declared.
    private void reportUndeclared(Terminal leaf) {
        reporter.reportError("\"%\" is not declared", leaf.spelling, leaf.position);
    }

    private static TypeDenoter checkFieldIdentifier(FieldTypeDenoter ast, Identifier I) {
        if (ast instanceof MultipleFieldTypeDenoter) {
            MultipleFieldTypeDenoter ft = (MultipleFieldTypeDenoter) ast;
            if (ft.I.spelling.compareTo(I.spelling) == 0) {
                I.decl = ast;
                return ft.T;
            } else {
                return checkFieldIdentifier(ft.FT, I);
            }
        } else if (ast instanceof SingleFieldTypeDenoter) {
            SingleFieldTypeDenoter ft = (SingleFieldTypeDenoter) ast;
            if (ft.I.spelling.compareTo(I.spelling) == 0) {
                I.decl = ast;
                return ft.T;
            }
        }
        return StdEnvironment.errorType;
    }

    // Creates a small AST to represent the "declaration" of a standard
    // type, and enters it in the identification table.
    private TypeDeclaration declareStdType(String id, TypeDenoter typedenoter) {

        TypeDeclaration binding;

        binding = new TypeDeclaration(new Identifier(id, dummyPos), typedenoter, dummyPos);
        idTable.enter(id, binding);
        return binding;
    }

    // Creates a small AST to represent the "declaration" of a standard
    // type, and enters it in the identification table.
    private ConstDeclaration declareStdConst(String id, TypeDenoter constType) {

        IntegerExpression constExpr;
        ConstDeclaration binding;

        // constExpr used only as a placeholder for constType
        constExpr = new IntegerExpression(null, dummyPos);
        constExpr.type = constType;
        binding = new ConstDeclaration(new Identifier(id, dummyPos), constExpr, dummyPos);
        idTable.enter(id, binding);
        return binding;
    }

    // Creates a small AST to represent the "declaration" of a standard
    // type, and enters it in the identification table.
    private ProcDeclaration declareStdProc(String id, FormalParameterSequence fps) {

        ProcDeclaration binding;

        binding = new ProcDeclaration(new Identifier(id, dummyPos), fps,
                new EmptyCommand(dummyPos), dummyPos);
        idTable.enter(id, binding);
        return binding;
    }

    // Creates a small AST to represent the "declaration" of a standard
    // type, and enters it in the identification table.
    private FuncDeclaration declareStdFunc(String id, FormalParameterSequence fps,
            TypeDenoter resultType) {

        FuncDeclaration binding;

        binding = new FuncDeclaration(new Identifier(id, dummyPos), fps, resultType,
                new EmptyExpression(dummyPos), dummyPos);
        idTable.enter(id, binding);
        return binding;
    }

    // Creates a small AST to represent the "declaration" of a
    // unary operator, and enters it in the identification table.
    // This "declaration" summarises the operator's type info.
    private UnaryOperatorDeclaration declareStdUnaryOp(String op, TypeDenoter argType, TypeDenoter resultType) {

        UnaryOperatorDeclaration binding;

        binding = new UnaryOperatorDeclaration(new Operator(op, dummyPos),
                argType, resultType, dummyPos);
        idTable.enter(op, binding);
        return binding;
    }

    // Creates a small AST to represent the "declaration" of a
    // binary operator, and enters it in the identification table.
    // This "declaration" summarises the operator's type info.
    private BinaryOperatorDeclaration declareStdBinaryOp(String op, TypeDenoter arg1Type, TypeDenoter arg2type, TypeDenoter resultType) {

        BinaryOperatorDeclaration binding;

        binding = new BinaryOperatorDeclaration(new Operator(op, dummyPos),
                arg1Type, arg2type, resultType, dummyPos);
        idTable.enter(op, binding);
        return binding;
    }

    // Creates small ASTs to represent the standard types.
    // Creates small ASTs to represent "declarations" of standard types,
    // constants, procedures, functions, and operators.
    // Enters these "declarations" in the identification table.
    private final static Identifier dummyI = new Identifier("", dummyPos);

    private void establishStdEnvironment() {

        // idTable.startIdentification();
        StdEnvironment.booleanType = new BoolTypeDenoter(dummyPos);
        StdEnvironment.integerType = new IntTypeDenoter(dummyPos);
        StdEnvironment.charType = new CharTypeDenoter(dummyPos);
        StdEnvironment.anyType = new AnyTypeDenoter(dummyPos);
        StdEnvironment.errorType = new ErrorTypeDenoter(dummyPos);

        StdEnvironment.booleanDecl = declareStdType("Boolean", StdEnvironment.booleanType);
        StdEnvironment.falseDecl = declareStdConst("false", StdEnvironment.booleanType);
        StdEnvironment.trueDecl = declareStdConst("true", StdEnvironment.booleanType);
        StdEnvironment.notDecl = declareStdUnaryOp("\\", StdEnvironment.booleanType, StdEnvironment.booleanType);
        StdEnvironment.andDecl = declareStdBinaryOp("/\\", StdEnvironment.booleanType, StdEnvironment.booleanType, StdEnvironment.booleanType);
        StdEnvironment.orDecl = declareStdBinaryOp("\\/", StdEnvironment.booleanType, StdEnvironment.booleanType, StdEnvironment.booleanType);

        StdEnvironment.integerDecl = declareStdType("Integer", StdEnvironment.integerType);
        StdEnvironment.maxintDecl = declareStdConst("maxint", StdEnvironment.integerType);
        StdEnvironment.addDecl = declareStdBinaryOp("+", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
        StdEnvironment.subtractDecl = declareStdBinaryOp("-", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
        StdEnvironment.multiplyDecl = declareStdBinaryOp("*", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
        StdEnvironment.divideDecl = declareStdBinaryOp("/", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
        StdEnvironment.moduloDecl = declareStdBinaryOp("//", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.integerType);
        StdEnvironment.lessDecl = declareStdBinaryOp("<", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);
        StdEnvironment.notgreaterDecl = declareStdBinaryOp("<=", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);
        StdEnvironment.greaterDecl = declareStdBinaryOp(">", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);
        StdEnvironment.notlessDecl = declareStdBinaryOp(">=", StdEnvironment.integerType, StdEnvironment.integerType, StdEnvironment.booleanType);

        StdEnvironment.charDecl = declareStdType("Char", StdEnvironment.charType);
        StdEnvironment.chrDecl = declareStdFunc("chr", new SingleFormalParameterSequence(
                new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos), StdEnvironment.charType);
        StdEnvironment.ordDecl = declareStdFunc("ord", new SingleFormalParameterSequence(
                new ConstFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos), StdEnvironment.integerType);
        StdEnvironment.eofDecl = declareStdFunc("eof", new EmptyFormalParameterSequence(dummyPos), StdEnvironment.booleanType);
        StdEnvironment.eolDecl = declareStdFunc("eol", new EmptyFormalParameterSequence(dummyPos), StdEnvironment.booleanType);
        StdEnvironment.getDecl = declareStdProc("get", new SingleFormalParameterSequence(
                new VarFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos));
        StdEnvironment.putDecl = declareStdProc("put", new SingleFormalParameterSequence(
                new ConstFormalParameter(dummyI, StdEnvironment.charType, dummyPos), dummyPos));
        StdEnvironment.getintDecl = declareStdProc("getint", new SingleFormalParameterSequence(
                new VarFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
        StdEnvironment.putintDecl = declareStdProc("putint", new SingleFormalParameterSequence(
                new ConstFormalParameter(dummyI, StdEnvironment.integerType, dummyPos), dummyPos));
        StdEnvironment.geteolDecl = declareStdProc("geteol", new EmptyFormalParameterSequence(dummyPos));
        StdEnvironment.puteolDecl = declareStdProc("puteol", new EmptyFormalParameterSequence(dummyPos));
        StdEnvironment.equalDecl = declareStdBinaryOp("=", StdEnvironment.anyType, StdEnvironment.anyType, StdEnvironment.booleanType);
        StdEnvironment.unequalDecl = declareStdBinaryOp("\\=", StdEnvironment.anyType, StdEnvironment.anyType, StdEnvironment.booleanType);

    }
}
