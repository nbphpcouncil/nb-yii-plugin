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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;

/**
 *
 * @author junichi11
 */
public class ProjectPropertiesSupport {

    public static final String INCLUDE_PATH = "include.path"; // NOI18N
    public static final String PHP_UNIT_BOOTSTRAP = "phpunit.bootstrap"; // NOI18N
    public static final String PHP_UNIT_BOOTSTRAP_FOR_CREATE_TESTS = "phpunit.bootstrap.create.tests"; // NOI18N
    public static final String PHP_UNIT_CONFIGURATION = "phpunit.configuration"; // NOI18N
    private static final String BOOTSTRAP_PHP = "bootstrap.php"; //NOI18N
    private static final String PHPUNIT_XML = "phpunit.xml"; //NOI18N

    /**
     * Set include path
     *
     * @param phpModule
     * @param paths path is relative path
     */
    public static void setIncludePath(final PhpModule phpModule, final List<String> paths) {
        try {
            // store properties
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    Project project = getProject(phpModule);
                    AntProjectHelper helper = getAntProjectHelper(project);
                    if (helper == null) {
                        return null;
                    }
                    EditableProperties properties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    List<String> currentIncludePaths = Arrays.asList(getIncludePaths(project));
                    List<String> includePaths = new LinkedList<String>();
                    boolean isAdded = false;
                    for (String path : paths) {
                        FileObject sourceDirectory = phpModule.getSourceDirectory();
                        FileObject target = null;
                        if (sourceDirectory != null) {
                            target = sourceDirectory.getFileObject(path);
                        }
                        for (String currentPath : currentIncludePaths) {
                            currentPath = currentPath + ":"; //NOI18N
                            includePaths.add(currentPath);
                        }
                        if (target != null) {
                            File file = FileUtil.toFile(target);
                            String absolutePath = file.getAbsolutePath();
                            if (!currentIncludePaths.contains(absolutePath)) {
                                if (paths.size() > 1) {
                                    absolutePath = absolutePath + ":"; // NOI18N
                                }
                                includePaths.add(absolutePath); //NOI18N
                                isAdded = true;
                            }
                        }
                    }
                    if (isAdded) {
                        properties.setProperty(INCLUDE_PATH, includePaths.toArray(new String[0]));
                        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, properties);
                    }
                    ProjectManager.getDefault().saveProject(project);
                    return null;
                }
            });
        } catch (MutexException e) {
            Exceptions.printStackTrace((IOException) e.getException());
        }

    }

    /**
     * Get include path from project.properties.
     *
     * @param project
     * @return
     */
    public static String getIncludePath(Project project) {
        String includePath = ""; // NOI18N
        if (project != null) {
            AntProjectHelper helper = getAntProjectHelper(project);
            EditableProperties properties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
            includePath = properties.getProperty(INCLUDE_PATH);
        }
        return includePath;
    }

    /**
     * Get includle paths as array from project properties.
     *
     * @param project
     * @return
     */
    public static String[] getIncludePaths(Project project) {
        String includePath = getIncludePath(project);
        return PropertyUtils.tokenizePath(includePath);
    }

    /**
     * Set Yii framework path to include path
     *
     * @param phpModule
     */
    public static void setYiiIncludePath(PhpModule phpModule) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return;
        }
        FileObject index = sourceDirectory.getFileObject("index.php"); //NOI18N
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
    public static void setPHPUnit(final PhpModule phpModule) {
        try {
            // store properties
            ProjectManager.mutex().writeAccess(new Mutex.ExceptionAction<Void>() {
                @Override
                public Void run() throws IOException {
                    Project project = getProject(phpModule);
                    AntProjectHelper helper = getAntProjectHelper(project);
                    if (helper == null) {
                        return null;
                    }
                    EditableProperties properties = helper.getProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH);
                    if (properties != null) {
                        FileObject testsDirectory = YiiUtils.getTestsDirectory(phpModule);
                        if (testsDirectory == null) {
                            return null;
                        }
                        FileObject bootstrap = testsDirectory.getFileObject(BOOTSTRAP_PHP);
                        if (bootstrap != null) {
                            properties.setProperty(PHP_UNIT_BOOTSTRAP, relativizeFile(project, bootstrap.getPath()));
                            properties.setProperty(PHP_UNIT_BOOTSTRAP_FOR_CREATE_TESTS, "true"); // NOI18N
                        }
                        FileObject phpunitXml = testsDirectory.getFileObject(PHPUNIT_XML);
                        if (phpunitXml != null) {
                            properties.setProperty(PHP_UNIT_CONFIGURATION, relativizeFile(project, phpunitXml.getPath()));
                        }

                        helper.putProperties(AntProjectHelper.PROJECT_PROPERTIES_PATH, properties);
                    }

                    ProjectManager.getDefault().saveProject(project);
                    return null;
                }
            });
        } catch (MutexException e) {
            Exceptions.printStackTrace((IOException) e.getException());
        }
    }

    /**
     * Get Project for PhpModule.
     *
     * @param phpModule
     * @return
     */
    public static Project getProject(PhpModule phpModule) {
        FileObject projectDirectory = phpModule.getProjectDirectory();
        if (projectDirectory == null) {
            return null;
        }
        return getProject(projectDirectory);

    }

    /**
     * Get Project for FileObject.
     *
     * @param fo
     * @return
     */
    public static Project getProject(FileObject fo) {
        return FileOwnerQuery.getOwner(fo);
    }

    /**
     * Get AntProjectHelper.
     *
     * @param project
     * @return
     */
    private static AntProjectHelper getAntProjectHelper(Project project) {
        if (project == null) {
            return null;
        }
        return project.getLookup().lookup(AntProjectHelper.class);
    }

    /**
     * Relativize file.
     *
     * @param project
     * @param filePath
     * @return
     */
    private static String relativizeFile(Project project, String filePath) {
        if (StringUtils.hasText(filePath)) {
            File file = new File(filePath);
            String path = PropertyUtils.relativizeFile(FileUtil.toFile(project.getProjectDirectory()), file);
            if (path == null) {
                // sorry, cannot be relativized
                path = file.getAbsolutePath();
            }
            return path;
        }
        return ""; // NOI18N
    }
}
