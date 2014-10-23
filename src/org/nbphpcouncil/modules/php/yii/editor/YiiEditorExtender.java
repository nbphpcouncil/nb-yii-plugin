/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
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
            if (controller != null) {
                List<PhpBaseElement> elements = new LinkedList<PhpBaseElement>();
                String controllerName = controller.getName();
                // add controller and variables
                PhpClass controllerClass = new PhpClass(controllerName, controllerName);
                PhpVariable phpVariable = new PhpVariable("$this", controllerClass, controller, 0); // NOI18N
                elements.add(phpVariable);
                elements.addAll(parseAction(fo));

                return elements;
            }
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
        private final HashMap<String, String> instances = new HashMap<String, String>();

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
            if (invokedMethodName == null || !invokedMethodName.equals("render")) { // NOI18N
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
            Expression expression;

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
