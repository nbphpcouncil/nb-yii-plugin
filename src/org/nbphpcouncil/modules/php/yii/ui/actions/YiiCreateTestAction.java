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
package org.nbphpcouncil.modules.php.yii.ui.actions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ClassDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author junichi11
 */
@NbBundle.Messages("LBL_YiiCreateTestAction=Create PHPUnit Test")
public class YiiCreateTestAction extends BaseAction {

    private static final long serialVersionUID = 6167073161635127352L;
    private static final YiiCreateTestAction INSTANCE = new YiiCreateTestAction();
    private static final String TEST_CLASS_NAME_SUFFIX = "Test"; // NOI18N
    private String className;
    private String testClassName;
    private String testSuperClassName;
    private List<String> methods;
    private List<String> testMethods;
    private static final Set<String> ignoreMethodList = new HashSet<String>();

    static {
        // XXX more?
        ignoreMethodList.add("model"); // NOI18N
        ignoreMethodList.add("tableName"); // NOI18N
        ignoreMethodList.add("rules"); // NOI18N
        ignoreMethodList.add("relations"); // NOI18N
        ignoreMethodList.add("attributeLabels"); // NOI18N
    }

    private YiiCreateTestAction() {
    }

    public static YiiCreateTestAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return Bundle.LBL_YiiAction(getPureName());
    }

    @Override
    protected String getPureName() {
        return Bundle.LBL_YiiCreateTestAction();
    }

    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // called via shortcut
        if (!YiiUtils.isYii(phpModule)) {
            return;
        }

        // get current selected files
        Lookup context = Utilities.actionsGlobalContext();
        Collection<? extends DataObject> dataObjects = context.lookupAll(DataObject.class);

        for (DataObject target : dataObjects) {
            FileObject currentFile = target.getPrimaryFile();
            if (currentFile == null) {
                continue;
            }

            // get class name and methods
            getClassAndMethods(currentFile);

            // check whether current file is test file
            if (StringUtils.isEmpty(className) || className.endsWith(TEST_CLASS_NAME_SUFFIX)) {
                continue;
            }

            // set test class name
            testClassName = className + TEST_CLASS_NAME_SUFFIX;
            String testFileName = testClassName + ".php"; // NOI18N
            FileObject unitTestDirectory = YiiUtils.getUnitTestDirectory(phpModule);
            if (unitTestDirectory == null) {
                return;
            }

            FileObject testFile = unitTestDirectory.getFileObject(testFileName);

            // ** if already exist file, open the file simply **
            if (testFile != null) {
                // open test file
                UiUtils.open(testFile, 0);
                continue;
            }

            // get super class name
            String superClassName = getSuperClassName(currentFile);

            setTestSuperClassName(superClassName);

            createTestMethods();

            // create Test
            if (createTest(phpModule, testFileName, unitTestDirectory)) {
                // open test file
                testFile = unitTestDirectory.getFileObject(testFileName);
                if (testFile != null) {
                    UiUtils.open(testFile, 0);
                }
            }
        }
    }

    /**
     * Get class name with super class name.
     *
     * @param currentFile
     * @param classNames Map of class name
     */
    private void getClassNames(FileObject currentFile, final Map<String, String> classNames) {
        try {
            ParserManager.parse(Collections.singleton(Source.create(currentFile)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    if (resultIterator == null) {
                        return;
                    }
                    ParserResult parserResult = (ParserResult) resultIterator.getParserResult();
                    ClassNameVisitor classNameVisitor = new ClassNameVisitor();
                    classNameVisitor.scan(Utils.getRoot(parserResult));
                    classNames.putAll(classNameVisitor.getClasssNames());;
                }
            });

        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * Get class name and class methods.
     *
     * @param currentFile
     */
    private void getClassAndMethods(FileObject currentFile) {
        // get EditorSupport for class & method name
        methods = new ArrayList<String>();
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        Collection<PhpClass> phpClasses = editorSupport.getClasses(currentFile);
        for (PhpClass phpClass : phpClasses) {
            className = phpClass.getName();
            for (PhpClass.Method method : phpClass.getMethods()) {
                methods.add(method.getName());
            }
            break;
        }
        Collections.sort(methods);
    }

    /**
     * Get super class name.
     *
     * @param currentFile
     * @return
     */
    private String getSuperClassName(FileObject currentFile) {
        String superClassName = ""; // NOI18N
        final Map<String, String> classNames = new LinkedHashMap<String, String>();
        getClassNames(currentFile, classNames);
        // check whether current file is model
        for (Map.Entry<String, String> entry : classNames.entrySet()) {
            String name = entry.getKey();
            if (className.equals(name)) {
                superClassName = entry.getValue();
                if (superClassName == null) {
                    superClassName = ""; // NOI18N
                }
                break;
            }
        }
        return superClassName;
    }

    /**
     * Create test methods name. e.g. Test method name is testSomeMethod for
     * someMethod.
     */
    private void createTestMethods() {
        testMethods = new ArrayList<String>();
        for (String method : methods) {
            if (ignoreMethodList.contains(method)) {
                continue;
            }
            String testMethod = "test" + YiiUtils.toFirstCharUpperCase(method); // NOI18N
            testMethods.add(testMethod);
        }
    }

    /**
     * Create test file.
     *
     * @param phpModule
     * @param testFileName
     * @param unitTestDirectory
     * @return true if success, otherwise false
     */
    private boolean createTest(PhpModule phpModule, String testFileName, FileObject unitTestDirectory) {
        try {
            OutputStream outputStream = unitTestDirectory.createAndOpen(testFileName);
            try {
                PhpModuleProperties properties = phpModule.getProperties();
                PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, properties.getEncoding()));
                try {
                    printWriter.println("<?php"); // NOI18N
                    printWriter.format("class %s extends %s", testClassName, testSuperClassName); // NOI18N
                    printWriter.println();
                    printWriter.println("{"); // NOI18N
                    for (String method : testMethods) {
                        printWriter.println("\tpublic function " + method + "()"); // NOI18N
                        printWriter.println("\t{"); // NOI18N
                        printWriter.println("\t}"); // NOI18N
                        printWriter.println();
                    }
                    printWriter.println("}"); // NOI18N
                } finally {
                    printWriter.close();
                }
            } finally {
                outputStream.close();
            }

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return true;
    }

    /**
     * Set super class name of Test class. If target class extends CActiveRecord
     * class, super class name is CDbTestCase, otherwise CTestCase.
     *
     * @param superClassName
     */
    private void setTestSuperClassName(String superClassName) {
        // set extends test class name
        // XXX Is this OK?
        if (superClassName.equals("CActiveRecord")) { // NOI18N
            testSuperClassName = "CDbTestCase"; // NOI18N
        } else {
            testSuperClassName = "CTestCase"; // NOI18N
        }
    }

    //~ inner class
    private static class ClassNameVisitor extends DefaultVisitor {

        private final Map<String, String> classNames = new LinkedHashMap<String, String>();

        @Override
        public void visit(ClassDeclaration node) {
            super.visit(node);
            String className = CodeUtils.extractClassName(node);
            String superClassName = CodeUtils.extractUnqualifiedSuperClassName(node);
            classNames.put(className, superClassName);
        }

        public Map<String, String> getClasssNames() {
            return classNames;
        }
    }
}
