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

import org.nbphpcouncil.modules.php.yii.YiiModule;
import org.nbphpcouncil.modules.php.yii.YiiModule.PATH_ALIAS;
import org.nbphpcouncil.modules.php.yii.YiiModuleFactory;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class YiiPathAliasSupport {

    /**
     * Get FileObject for path alias. e.g. application.component.MyComponent
     *
     * @param phpModule
     * @param aliasPath
     * @return
     */
    public static FileObject getFileObject(PhpModule phpModule, String aliasPath) {
        String[] paths = splitAliasPath(aliasPath);
        if (paths == null || paths.length < 1) {
            return null;
        }

        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        FileObject baseDirectory = null;
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            // first time
            if (baseDirectory == null) {
                baseDirectory = yiiModule.getDirectory(toPathAlias(path));
                if (baseDirectory == null) {
                    return null;
                }
                continue;
            }

            // create relative path
            sb.append("/").append(path); // NOI18N
        }
        if (paths.length > 1) {
            sb.deleteCharAt(0);
            sb.append(".php"); // NOI18N
        }

        return baseDirectory.getFileObject(sb.toString());
    }

    /**
     * Split alias path.
     *
     * @param pathAlias
     * @return
     */
    public static String[] splitAliasPath(String pathAlias) {
        if (pathAlias == null) {
            return null;
        }
        return pathAlias.split("\\."); // NOI18N
    }

    /**
     * Convert from String to PATH_ALIAS.
     *
     * @param name
     * @return
     */
    public static PATH_ALIAS toPathAlias(String name) {
        if (name == null) {
            return PATH_ALIAS.NONE;
        }
        if (name.equals(PATH_ALIAS.SYSTEM.getName())) {
            return PATH_ALIAS.SYSTEM;
        } else if (name.equals(PATH_ALIAS.ZII.getName())) {
            return PATH_ALIAS.ZII;
        } else if (name.equals(PATH_ALIAS.APPLICATION.getName())) {
            return PATH_ALIAS.APPLICATION;
        } else if (name.equals(PATH_ALIAS.WEBROOT.getName())) {
            return PATH_ALIAS.WEBROOT;
        } else if (name.equals(PATH_ALIAS.EXT.getName())) {
            return PATH_ALIAS.EXT;
        }
        return PATH_ALIAS.NONE;
    }
}
