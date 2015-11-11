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
package org.nbphpcouncil.modules.php.yii.editor.completion.methods;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.nbphpcouncil.modules.php.yii.YiiModule;
import org.nbphpcouncil.modules.php.yii.YiiModule.PATH_ALIAS;
import org.nbphpcouncil.modules.php.yii.YiiModuleFactory;
import org.nbphpcouncil.modules.php.yii.util.YiiPathAliasSupport;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.nbphpcouncil.modules.php.yii.util.YiiViewPathSupport;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class CompletePathMethod extends Method {

    private static final String SLASH = "/"; // NOI18N
    private static final String DOUBLE_SLASH = "//"; // NOI18N
    private static final String DOT = "."; // NOI18N

    public CompletePathMethod(FileObject currentFile, PhpModule phpModule) {
        super(currentFile, phpModule);
    }

    @Override
    public List<String> getElements(String target) {
        List<String> elements = new LinkedList<>();
        if (useViewPath()) {
            elements.addAll(getViewPathElements(target));
        }
        if (usePathAlias()) {
            elements.addAll(getPathAliasElements(target));
        }
        return elements;
    }

    private List<String> getViewPathElements(String target) {
        List<String> elements = new LinkedList<>();
        if (target.startsWith("///")) { // NOI18N
            return elements;
        }

        // get views directory
        FileObject viewsDirectory = getViewsDirectory(target);
        if (viewsDirectory != null) {
            int lastSeparator = target.lastIndexOf(SLASH);
            String subPath = getSubPathForView(target, lastSeparator);
            String filter = getFilterForView(target, lastSeparator);
            FileObject targetDirectory = null;

            if (YiiViewPathSupport.isAbsoluteViewPath(target)) {
                targetDirectory = viewsDirectory.getFileObject(subPath);
            } else {
                String controllerId = YiiUtils.getViewFolderName(currentFile.getName());
                if (!StringUtils.isEmpty(controllerId)) {
                    targetDirectory = viewsDirectory.getFileObject(controllerId + SLASH + subPath);
                }
            }

            // add element
            if (targetDirectory != null) {
                for (FileObject child : targetDirectory.getChildren()) {
                    String name = child.getName();
                    if (!name.startsWith(filter)) {
                        continue;
                    }
                    if (child.isFolder()) {
                        name = name + SLASH;
                    }
                    addElement(elements, target, subPath, name);
                }
            }
        }
        return elements;
    }

    private FileObject getViewsDirectory(String target) {
        FileObject viewsDirectory = null;

        // check absolute path
        if (YiiViewPathSupport.isAppPath(target)) {
            return YiiUtils.getViewsDirectory(phpModule);
        }

        // check in modules
        if (YiiUtils.isInModules(currentFile)) {
            FileObject currentModuleDirectory = YiiUtils.getCurrentModuleDirectory(currentFile);
            if (currentModuleDirectory != null) {
                return currentModuleDirectory.getFileObject("views"); // NOI18N
            }
        }

        // check theme
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        String themeName = yiiModule.getThemeName();
        if (!StringUtils.isEmpty(themeName)) {
            FileObject themesDirectory = YiiUtils.getThemesDirectory(phpModule);
            if (themesDirectory != null) {
                viewsDirectory = themesDirectory.getFileObject(themeName + "/views"); // NOI18N
            }
        } else {
            viewsDirectory = YiiUtils.getViewsDirectory(phpModule);
        }

        return viewsDirectory;
    }

    private String getSubPathForView(String target, int lastSeparator) {
        String subPath = ""; // NOI18N
        if (lastSeparator > 1) {
            if (YiiViewPathSupport.isAppPath(target)) {
                subPath = target.substring(2, lastSeparator);
            } else if (YiiViewPathSupport.isModulePath(target)) {
                subPath = target.substring(1, lastSeparator);
            } else {
                subPath = target.substring(0, lastSeparator);
            }
        }
        return subPath;
    }

    private String getFilterForView(String target, int lastSeparator) {
        String filter;
        if (lastSeparator > 1) {
            filter = target.substring(lastSeparator + 1);
        } else {
            if (YiiViewPathSupport.isAppPath(target)) {
                filter = target.substring(2);
            } else if (YiiViewPathSupport.isModulePath(target)) {
                filter = target.substring(1);
            } else {
                filter = target;
            }
        }
        return filter;
    }

    private void addElement(List<String> elements, String target, String subPath, String name) {
        StringBuilder sb = new StringBuilder();
        if (YiiViewPathSupport.isAppPath(target)) {
            sb.append(DOUBLE_SLASH);
        } else if (YiiViewPathSupport.isModulePath(target)) {
            sb.append(SLASH);
        }
        if (!StringUtils.isEmpty(subPath)) {
            sb.append(subPath);
            sb.append(SLASH);
        }
        sb.append(name);
        elements.add(sb.toString());
    }

    private List<String> getPathAliasElements(String target) {
        List<String> elements = new LinkedList<>();
        int firstDot = target.indexOf(DOT);
        String pathAliasName = ""; // NOI18N
        if (firstDot > 1) {
            pathAliasName = target.substring(0, firstDot);
        }

        PATH_ALIAS pathAlias = YiiPathAliasSupport.toPathAlias(pathAliasName);
        if (pathAlias != PATH_ALIAS.NONE) {
            String subPath = ""; // NOI18N
            String filter = ""; // NOI18N
            String subPathForFind = ""; // NOI18N

            // get base directory for path alias
            YiiModule yiiModule = YiiModuleFactory.create(phpModule);
            FileObject baseDirectory = yiiModule.getDirectory(pathAlias);

            if (baseDirectory != null) {
                int lastDot = target.lastIndexOf(DOT);
                if (firstDot == lastDot) {
                    filter = target.substring(firstDot + 1);
                } else {
                    subPath = target.substring(firstDot + 1, lastDot);
                    subPathForFind = subPath.replaceAll("\\.", SLASH); // NOI18N
                    filter = target.substring(lastDot + 1);
                }

                // get parent directory for completion
                FileObject targetDirectory = baseDirectory.getFileObject(subPathForFind);
                if (targetDirectory != null) {
                    // add elememts
                    for (FileObject child : targetDirectory.getChildren()) {
                        String name = child.getName();
                        if (!name.startsWith(filter)) {
                            continue;
                        }
                        if (child.isFolder()) {
                            name = name + DOT;
                        }
                        if (StringUtils.isEmpty(subPath)) {
                            elements.add(pathAliasName + DOT + name);
                        } else {
                            elements.add(pathAliasName + DOT + subPath + DOT + name);
                        }
                    }
                }
            }
        }

        if (elements.isEmpty()) {
            // add default path aliases
            addDefaultPathAliases(elements);

            // add classes
            addClasses(target, elements);
        }
        return elements;
    }

    private void addDefaultPathAliases(List<String> elements) {
        for (PATH_ALIAS alias : PATH_ALIAS.values()) {
            if (alias == PATH_ALIAS.NONE) {
                continue;
            }
            elements.add(alias.getName() + DOT); // NOI18N
        }
    }

    private void addClasses(String target, List<String> elements) {
        if (useClasses()) {
            ElementQuery.Index indexQuery = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(phpModule.getSourceDirectory()));
            Set<ClassElement> classElements;
            if (target.isEmpty()) {
                classElements = indexQuery.getClasses();
            } else {
                classElements = indexQuery.getClasses(NameKind.create(target, QuerySupport.Kind.PREFIX));
            }
            for (ClassElement element : classElements) {
                String name = element.getName();
                if (name.startsWith(target)) {
                    elements.add(name);
                }
            }
        }
    }

    public abstract boolean usePathAlias();

    public abstract boolean useViewPath();

    public abstract boolean useClasses();
}
