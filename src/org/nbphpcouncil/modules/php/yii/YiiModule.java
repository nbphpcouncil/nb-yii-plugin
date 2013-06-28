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
package org.nbphpcouncil.modules.php.yii;

import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class YiiModule {

    public enum PATH_ALIAS {

        SYSTEM("system"),
        ZII("zii"),
        APPLICATION("application"),
        WEBROOT("webroot"),
        EXT("ext"),
        NONE("");
        private String name;

        private PATH_ALIAS(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public abstract FileObject getWebroot();

    public abstract FileObject getSystem();

    public abstract FileObject getZii();

    public abstract FileObject getApplication();

    public abstract FileObject getExt();

    public abstract FileObject getControllers();

    public abstract FileObject getViews();

    public abstract FileObject getMessages();

    public abstract FileObject getThemes();

    public abstract String getThemeName();

    public abstract void initDirectories();

    public FileObject getDirectory(PATH_ALIAS alias) {
        switch (alias) {
            case SYSTEM:
                return getSystem();
            case ZII:
                return getZii();
            case APPLICATION:
                return getApplication();
            case WEBROOT:
                return getWebroot();
            case EXT:
                return getExt();
            case NONE:
                return null;
            default:
                throw new AssertionError();
        }
    }

    /**
     * Get FileObject for specified path alias and relativepath.
     *
     * @param alias path alias
     * @param relativePath relative path
     * @return FileObject if file exists, otherwise null.
     */
    public FileObject getFileObject(PATH_ALIAS alias, String relativePath) {
        FileObject directory = getDirectory(alias);
        if (directory == null) {
            return null;
        }
        if (relativePath == null) {
            return directory;
        }
        return directory.getFileObject(relativePath);
    }
}
