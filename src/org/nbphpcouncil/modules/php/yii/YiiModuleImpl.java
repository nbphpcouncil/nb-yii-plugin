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

import java.util.List;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class YiiModuleImpl extends YiiModule {

    // default path
    private static final String APPLICATION = "protected"; // NOI18N
    private static final String EXT = APPLICATION + "/extensions"; // NOI18N
    private static final String ZII = "zii"; // NOI18N
    private static final String ENTRY_SCRIPT = "index.php"; // NOI18N
    private final PhpModule phpModule;
    protected String systemPath;
    protected FileObject index;
    protected FileObject config;

    public YiiModuleImpl(PhpModule phpModule) {
        this.phpModule = phpModule;
        systemPath = getSystemPath();
    }

    private String getSystemPath() {
        index = getIndexFile();
        if (index != null) {
            List<String> includePath = YiiUtils.getIncludePath(index);
            for (String system : includePath) {
                return system;
            }
        }
        return null;
    }

    private FileObject getIndexFile() {
        PhpModuleProperties properties = phpModule.getProperties();

        // get index (entry script)
        FileObject indexFile = properties.getIndexFile();
        FileObject webroot = getWebroot();
        if (webroot == null) {
            return null;
        }

        // default
        if (indexFile == null) {
            indexFile = webroot.getFileObject(ENTRY_SCRIPT);
        }
        return indexFile;
    }

    @Override
    public FileObject getWebroot() {
        PhpModuleProperties properties = phpModule.getProperties();
        FileObject webRoot = properties.getWebRoot();
        if (webRoot != null) {
            return webRoot;
        }
        return phpModule.getSourceDirectory();
    }

    @Override
    public FileObject getSystem() {
        FileObject webroot = getWebroot();
        if (webroot == null) {
            return null;
        }
        FileObject system = webroot.getFileObject(systemPath);
        if (system == null) {
            // is path changed?
            systemPath = getSystemPath();
            system = webroot.getFileObject(systemPath);
        }
        return system;
    }

    @Override
    public FileObject getZii() {
        FileObject system = getSystem();
        if (system != null) {
            return system.getFileObject(ZII);
        }
        return null;
    }

    @Override
    public FileObject getApplication() {
        return getWebroot().getFileObject(APPLICATION);
    }

    @Override
    public FileObject getExt() {
        return getWebroot().getFileObject(EXT);
    }
}
