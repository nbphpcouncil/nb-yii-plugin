/*
 * TODO add license
 */
package org.nbphpcouncil.modules.php.yii.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayCreation;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment;
import org.netbeans.modules.php.editor.parser.astnodes.Assignment.Type;
import org.netbeans.modules.php.editor.parser.astnodes.ClassInstanceCreation;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodInvocation;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.Variable;
import org.netbeans.modules.php.editor.parser.astnodes.VariableBase;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class YiiEditorExtender extends EditorExtender {

    private static final Logger LOGGER = Logger.getLogger(YiiEditorExtender.class.getName());

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {

        if (YiiUtils.isView(fo)) {
            FileObject controller = YiiUtils.getController(fo);
            List<PhpBaseElement> elements = new LinkedList<PhpBaseElement>();
            String controllerName = controller.getName();
            // add controller and variables
            PhpClass controllerClass = new PhpClass(controllerName, controllerName);
            PhpVariable phpVariable = new PhpVariable("$this", controllerClass, controller, 0); // NOI18N
            elements.add(phpVariable);
            elements.addAll(parseAction(fo));

            return elements;
        }
        return Collections.emptyList();
    }

    /**
     * Parse action
     *
     * @param view view file object
     * @return php variables
     */
    private Set<PhpVariable> parseAction(final FileObject view) {
        FileObject controller = YiiUtils.getController(view);
        if (controller == null) {
            return Collections.emptySet();
        }
        final HashSet<PhpVariable> phpVariables = new HashSet<PhpVariable>();

        try {
            ParserManager.parse(Collections.singleton(Source.create(controller)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    ParserResult parserResult = (ParserResult) resultIterator.getParserResult();
                    final YiiControllerVisitor controllerVisitor = new YiiControllerVisitor(view);
                    controllerVisitor.scan(Utils.getRoot(parserResult));
                    phpVariables.addAll(controllerVisitor.getPhpVariables());
                }
            });
        } catch (ParseException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return phpVariables;
    }

    private static final class YiiControllerVisitor extends DefaultVisitor {

        private final Set<PhpVariable> fields = new HashSet<PhpVariable>();
        private String methodName;
        private String viewName;
        private FileObject view;
        private HashMap<String, String> instances = new HashMap<String, String>();

        public YiiControllerVisitor(FileObject fileObject) {
            if (YiiUtils.isView(fileObject)) {
                this.view = fileObject;
                this.viewName = fileObject.getName();
            }
        }

        /**
         * Get php variables.
         *
         * @return php variables
         */
        public Set<PhpVariable> getPhpVariables() {
            Set<PhpVariable> phpVariables = new HashSet<PhpVariable>();
            synchronized (fields) {
                phpVariables.addAll(fields);
            }
            return phpVariables;
        }

        @Override
        public void visit(MethodDeclaration node) {
            methodName = CodeUtils.extractMethodName(node);
            super.visit(node);
        }

        @Override
        public void visit(Assignment node) {
            Type operator = node.getOperator();
            if (methodName.equals(YiiUtils.getActionMethodName(viewName))
                    && operator == Type.EQUAL) {
                Expression rightHandSide = node.getRightHandSide();
                if (rightHandSide instanceof ClassInstanceCreation) {
                    Variable leftVariable = null;
                    VariableBase leftHandSide = node.getLeftHandSide();
                    if (leftHandSide instanceof Variable) {
                        leftVariable = (Variable) leftHandSide;
                    }
                    ClassInstanceCreation instance = (ClassInstanceCreation) rightHandSide;
                    String variable = CodeUtils.extractVariableName(leftVariable);
                    String className = CodeUtils.extractQualifiedName(instance.getClassName().getName());
                    instances.put(variable, className);
                }
            }
            super.visit(node);
        }

        @Override
        public void visit(MethodInvocation node) {
            super.visit(node);
            FunctionInvocation fi = node.getMethod();
            String invokedMethodName = CodeUtils.extractFunctionName(fi);
            if (!invokedMethodName.equals("render")) { // NOI18N
                return;
            }
            if (!methodName.equals(YiiUtils.getActionMethodName(viewName))) {
                return;
            }
            if (!(node.getDispatcher() instanceof Variable)
                    || !"$this".equals(CodeUtils.extractVariableName((Variable) node.getDispatcher()))) { // NOI18N
                return;
            }

            List<Expression> params = fi.getParameters();
            Expression expression = null;

            if (params.size() > 1) {
                expression = params.get(1);
            } else {
                return;
            }

            if (expression instanceof ArrayCreation) {
                ArrayCreation array = (ArrayCreation) expression;
                List<ArrayElement> elements = array.getElements();
                for (ArrayElement element : elements) {
                    Expression key = element.getKey();
                    if (!(key instanceof Scalar)) {
                        continue;
                    }
                    Scalar s = (Scalar) key;
                    String varName = ""; // NOI18N
                    if (s.getScalarType() == Scalar.Type.STRING) {
                        varName = prepareViewVar(s.getStringValue());
                    }
                    if (varName.isEmpty()) {
                        continue;
                    }
                    varName = "$" + varName; // NOI18N
                    String fqn = instances.get(varName);

                    synchronized (fields) {
                        if (fqn != null && fqn.isEmpty()) {
                            PhpClass phpClass = new PhpClass(fqn, fqn);
                            fields.add(new PhpVariable(varName, phpClass, YiiUtils.getController(view), 0));
                        } else {
                            fields.add(new PhpVariable(varName, varName, view));
                        }
                    }
                }
            }
        }

        /**
         * Prepare view variable
         *
         * @param viewVarName
         * @return view variable name
         */
        private String prepareViewVar(String viewVarName) {
            if (!viewVarName.isEmpty()) {
                viewVarName = viewVarName.substring(1, viewVarName.length() - 1).trim();
                if (!viewVarName.matches("[A-Za-z_][A-Za-z0-9_]*")) { // NOI18N
                    viewVarName = ""; // NOI18N
                }
            }
            return viewVarName;
        }
    }
}
