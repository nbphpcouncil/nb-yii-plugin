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
package org.nbphpcouncil.modules.php.yii.util;

import java.util.List;
import javax.swing.DefaultListModel;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.project.classpath.BasePathSupport.Item;
import org.netbeans.modules.php.project.classpath.IncludePathSupport;
import org.netbeans.modules.php.project.ui.customizer.PhpProjectProperties;
import org.netbeans.modules.php.project.util.PhpProjectUtils;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class ProjectPropertiesSupport {

    private static final String BOOTSTRAP_PHP = "bootstrap.php"; //NOI18N
    private static final String PHPUNIT_XML = "phpunit.xml"; //NOI18N

    /**
     * Set include path
     *
     * @param phpModule
     * @param paths path is relative path
     */
    public static void setIncludePath(PhpModule phpModule, List<String> paths) {
        PhpProject phpProject = getPhpProject(phpModule);
        if (phpProject == null) {
            return;
        }

        PhpProjectProperties properties = createProjectProperties(phpProject);
        DefaultListModel includePathListModel = properties.getIncludePathListModel();
        for (String path : paths) {
            Item item = Item.create(path);
            if (!includePathListModel.contains(item)) {
                includePathListModel.addElement(item);
            }
        }
        properties.save();
    }

    /**
     * Set Yii framework path to include path
     *
     * @param phpModule
     */
    public static void setYiiIncludePath(PhpModule phpModule) {
        FileObject index = phpModule.getSourceDirectory().getFileObject("index.php"); //NOI18N
        if (index == null) {
            return;
        }
        List<String> includePath = YiiUtils.getIncludePath(index);
        setIncludePath(phpModule, includePath);
    }

    /**
     * Set PHPUnit bootstap.php and phpunit.xml
     *
     * @param phpModule
     */
    public static void setPHPUnit(PhpModule phpModule) {
        PhpProject phpProject = getPhpProject(phpModule);
        if (phpProject == null) {
            return;
        }
        PhpProjectProperties phpProjectProperties = new PhpProjectProperties(phpProject);
        FileObject testsDirectory = YiiUtils.getTestsDirectory(phpModule);
        if (testsDirectory == null) {
            return;
        }
        FileObject bootstrap = testsDirectory.getFileObject(BOOTSTRAP_PHP);
        if (bootstrap != null) {
            phpProjectProperties.setPhpUnitBootstrap(bootstrap.getPath());
            phpProjectProperties.setPhpUnitBootstrapForCreateTests(true);
        }
        FileObject phpunitXml = testsDirectory.getFileObject(PHPUNIT_XML);
        if (phpunitXml != null) {
            phpProjectProperties.setPhpUnitConfiguration(phpunitXml.getPath());
        }
        phpProjectProperties.save();
    }

    /**
     * Get PhpProject
     *
     * @param phpModule
     * @return PhpProject
     */
    public static PhpProject getPhpProject(PhpModule phpModule) {
        if (phpModule == null) {
            return null;
        }
        return PhpProjectUtils.getPhpProject(phpModule.getProjectDirectory());
    }

    /**
     * Create project properties
     *
     * @param project
     * @return
     */
    private static PhpProjectProperties createProjectProperties(PhpProject project) {
        AntProjectHelper antProjectHelper = project.getHelper();
        ReferenceHelper referenceHelper = project.getRefHelper();
        PropertyEvaluator evaluator = project.getLookup().lookup(PropertyEvaluator.class);
        IncludePathSupport includePathSupport = new IncludePathSupport(evaluator, referenceHelper, antProjectHelper);
        PhpProjectProperties properties = new PhpProjectProperties(project, includePathSupport, null);
        return properties;
    }
}
