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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbphpcouncil.modules.php.yii.YiiModule;
import org.nbphpcouncil.modules.php.yii.YiiModuleFactory;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public class YiiViewPathSupport {

    private static final String VIEW_PATH_FORMAT = "views%s.php"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(YiiViewPathSupport.class.getName());

    /**
     * Get view file for absolute path. '/' the view will be searched for under
     * the currently active module's view path. '//' application's view path.
     *
     * @param targetPath
     * @param currentFile
     * @see
     * http://www.yiiframework.com/doc/api/1.1/CController#resolveViewFile-detail
     * @return file if file exists, otherwise null.
     */
    public static FileObject getAbsoluteViewFile(String targetPath, FileObject currentFile) {
        PhpModule phpModule = PhpModule.forFileObject(currentFile);
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        // get absolute view within the application
        if (targetPath.startsWith("//")) { // NOI18N
            targetPath = targetPath.replaceFirst("/", ""); // NOI18N
            targetPath = String.format(VIEW_PATH_FORMAT, targetPath);
            return yiiModule.getFileObject(YiiModule.PATH_ALIAS.APPLICATION, targetPath);
        }

        // get absolute view within a module
        if (targetPath.startsWith("/")) { // NOI18N
            FileObject currentModuleDirectory = YiiUtils.getCurrentModuleDirectory(currentFile);
            targetPath = String.format(VIEW_PATH_FORMAT, targetPath);
            FileObject targetFile;
            if (currentModuleDirectory != null) {
                targetFile = currentModuleDirectory.getFileObject(targetPath);
            } else {
                targetFile = yiiModule.getFileObject(YiiModule.PATH_ALIAS.APPLICATION, targetPath);
            }
            return targetFile;
        }
        return null;
    }

    /**
     * Create view file for absolute path.
     *
     * @param targetPath
     * @param currentFile
     * @return file if file was created, otherwise null.
     */
    public static FileObject createAbsoluteViewFile(String targetPath, FileObject currentFile) {
        PhpModule phpModule = PhpModule.forFileObject(currentFile);
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        FileObject application = yiiModule.getApplication();
        // get absolute view within the application
        if (targetPath.startsWith("//")) { // NOI18N
            targetPath = targetPath.replaceFirst("/", ""); // NOI18N
            targetPath = String.format(VIEW_PATH_FORMAT, targetPath);
            if (application == null) {
                return null;
            }

            // create directories and file
            return createFile(application, targetPath);
        }

        // get absolute view within a module
        if (targetPath.startsWith("/")) { // NOI18N
            FileObject currentModuleDirectory = YiiUtils.getCurrentModuleDirectory(currentFile);
            targetPath = String.format(VIEW_PATH_FORMAT, targetPath);
            if (currentModuleDirectory != null) {
                return createFile(currentModuleDirectory, targetPath);
            } else {
                if (application != null) {
                    return createFile(application, targetPath);
                }
            }
        }

        return null;
    }

    /**
     * Create view file for target path.
     *
     * @param baseDirectory
     * @param targetPath
     * @return file if file was created, otherwise null.
     */
    private static FileObject createFile(FileObject baseDirectory, String targetPath) {
        File file = new File(FileUtil.toFile(baseDirectory), targetPath);
        createParents(file);
        try {
            if (file.createNewFile()) {
                return baseDirectory.getFileObject(targetPath);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Fail: can't create file {0}", targetPath);
        }

        return null;
    }

    /**
     * Create parent directories if it doesn't exist.
     *
     * @param file
     * @return true if create parents, otherwise false.
     */
    private static boolean createParents(File file) {
        File parentFile = file.getParentFile();
        return parentFile.mkdirs();
    }

    /**
     * Check whether path is absolute path. start with '/': module, start with
     * '//':application
     *
     * @param path
     * @return true if path starts with '//' or '/', otherwise false.
     */
    public static boolean isAbsoluteViewPath(String path) {
        if (path != null && !path.startsWith("///")) { // NOI18N
            if (path.startsWith("//") || path.startsWith("/")) { // NOI18N
                return true;
            }
        }
        return false;
    }
}
