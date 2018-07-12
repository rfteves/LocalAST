package localast

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@CompileStatic
@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
class WithLoggingASTTransformation implements ASTTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        MethodNode method = (MethodNode) nodes[1]

        def existingStatements = ((BlockStatement)method.code).statements

        println method.getProperties()
        def listExpressions = (ListExpression<String>)method.getAnnotations()[0].getMembers().get('properties')
        def gStrings = getVariables(listExpressions)

        Statement first = createStatement('\nStarting AST ', gStrings)
        Statement last = createStatement('\nEnding AST ', gStrings)
        existingStatements.add(0, first)
        existingStatements.add(last)
    }

    private static Statement createStatement(String message, List<String> gString) {
        ClassNode javaLangString = createRedirectClassNode(java.lang.String.class)
        List<ConstantExpression> strings = []
        strings << new ConstantExpression(message)
        for (String string: gString) {
            strings << new ConstantExpression(' ')
        }
        List<Expression> values = []
        for (String string: gString) {
            values << new VariableExpression(string, javaLangString)
        }
        new ExpressionStatement(
                new MethodCallExpression(
                        new VariableExpression("this"),
                        new ConstantExpression("println"),
                        new ArgumentListExpression(
                                new GStringExpression('varbatim', strings, values)
                        )
                )
        )
    }

    static List<String> getVariables(ListExpression listExpressions) {
        def strings = []
        for (Expression expression: listExpressions.getExpressions()) {
            ConstantExpression constantExpression = (ConstantExpression)expression
            strings << "$constantExpression.text"
        }
        strings
    }

    static ClassNode createRedirectClassNode(Class aClass) {
        ClassNode classNode = new ClassNode(aClass)
        classNode.setRedirect(ClassHelper.make(aClass))
        classNode
    }
}