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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nbphpcouncil.modules.php.yii.preferences.YiiPreferences;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class YiiModuleImpl extends YiiModule {

    // default path
    private static final String APPLICATION = "protected"; // NOI18N
    private static final String EXT = "extensions"; // NOI18N
    private static final String ZII = "zii"; // NOI18N
    private static final String ENTRY_SCRIPT = "index.php"; // NOI18N
    private static final String CONFIG = "config"; // NOI18N
    private final PhpModule phpModule;
    private String themeName;
    protected String systemPath;
    protected FileObject index;
    protected FileObject config;
    protected FileObject main;
    private FileObject systemDirectory;
    private FileObject applicationDirectory;
    private FileObject ziiDirectory;
    private FileObject extDirectory;
    private FileObject webrootDirectory;
    private FileObject controllersDirectory;
    private FileObject viewsDirectory;
    private FileObject themesDirectory;
    private FileObject messagesDirectory;
    private static final Logger LOGGER = Logger.getLogger(YiiModuleImpl.class.getName());

    public YiiModuleImpl(PhpModule phpModule) {
        this.phpModule = phpModule;
        systemPath = getSystemPath();
        initDirectories();
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
        PhpModuleProperties properties = phpModule.getLookup().lookup(PhpModuleProperties.class);

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
        return webrootDirectory;
    }

    private void setWebroot() {
        PhpModuleProperties properties = phpModule.getLookup().lookup(PhpModuleProperties.class);
        FileObject webRoot = properties.getWebRoot();
        if (webRoot != null) {
            webrootDirectory = webRoot;
            return;
        }
        webrootDirectory = phpModule.getSourceDirectory();
    }

    @Override
    public FileObject getSystem() {
        return systemDirectory;
    }

    private void setSystem() {
        // from settings
        String path = YiiPreferences.getSystemPath(phpModule);
        if (!path.isEmpty()) {
            systemDirectory = getDirectory(path);
            if (systemDirectory != null) {
                return;
            }
        }

        // default
        FileObject webroot = getWebroot();
        if (webroot == null) {
            return;
        }
        if (systemPath == null) {
            // try again
            systemPath = getSystemPath();
        }
        if (systemPath == null) {
            return;
        }

        FileObject system = webroot.getFileObject(systemPath);

        if (system == null) {
            // is path changed?
            systemPath = getSystemPath();
            system = webroot.getFileObject(systemPath);
        }
        systemDirectory = system;
    }

    @Override
    public FileObject getZii() {
        return ziiDirectory;
    }

    private void setZii() {
        // from settings
        String path = YiiPreferences.getZiiPath(phpModule);
        if (!path.isEmpty()) {
            ziiDirectory = getDirectory(path);
            if (ziiDirectory != null) {
                return;
            }
        }

        // default
        FileObject system = getSystem();
        if (system != null) {
            ziiDirectory = system.getFileObject(ZII);
        }
    }

    @Override
    public FileObject getApplication() {
        return applicationDirectory;
    }

    private void setApplication() {
        // from settings
        String path = YiiPreferences.getApplicationPath(phpModule);
        if (!path.isEmpty()) {
            applicationDirectory = getDirectory(path);
            if (applicationDirectory != null) {
                return;
            }
        }

        // default
        applicationDirectory = getWebroot().getFileObject(APPLICATION);
    }

    @Override
    public FileObject getExt() {
        return extDirectory;
    }

    private void setExt() {
        // from settings
        String path = YiiPreferences.getExtPath(phpModule);
        if (!path.isEmpty()) {
            extDirectory = getDirectory(path);
            if (extDirectory != null) {
                return;
            }
        }

        // default
        FileObject application = getApplication();
        if (application != null) {
            extDirectory = application.getFileObject(EXT);
        }
    }

    @Override
    public FileObject getControllers() {
        return controllersDirectory;
    }

    private void setControllers() {
        // from settings
        String path = YiiPreferences.getControllersPath(phpModule);
        if (!path.isEmpty()) {
            controllersDirectory = getDirectory(path);
            if (controllersDirectory != null) {
                return;
            }
        }

        // default
        FileObject application = getApplication();
        if (application != null) {
            controllersDirectory = application.getFileObject("controllers"); // NOI18N
        }
    }

    @Override
    public FileObject getViews() {
        return viewsDirectory;
    }

    private void setViews() {
        // from settings
        String path = YiiPreferences.getViewsPath(phpModule);
        if (!path.isEmpty()) {
            viewsDirectory = getDirectory(path);
            if (viewsDirectory != null) {
                return;
            }
        }

        // default
        FileObject application = getApplication();
        if (application != null) {
            viewsDirectory = application.getFileObject("views"); // NOI18N
        }
    }

    @Override
    public FileObject getThemes() {
        return themesDirectory;
    }

    private void setThemes() {
        // from settings
        String path = YiiPreferences.getThemesPath(phpModule);
        if (!path.isEmpty()) {
            themesDirectory = getDirectory(path);
            if (themesDirectory != null) {
                return;
            }
        }

        // default
        FileObject webroot = getWebroot();
        if (webroot != null) {
            themesDirectory = webroot.getFileObject("themes"); // NOI18N
        }
    }

    @Override
    public FileObject getMessages() {
        return messagesDirectory;
    }

    private void setMessages() {
        // from settings
        String path = YiiPreferences.getMessagesPath(phpModule);
        if (!path.isEmpty()) {
            messagesDirectory = getDirectory(path);
            if (messagesDirectory != null) {
                return;
            }
        }

        // default
        FileObject application = getApplication();
        if (application != null) {
            messagesDirectory = application.getFileObject("messages"); // NOI18N
        }
    }

    @Override
    public String getThemeName() {
        if (themeName != null) {
            return themeName;
        }
        if (main == null) {
            main = getMain();
        }

        if (main == null) {
            return null;
        }

        // get theme
        themeName = YiiUtils.getThemeName(main);
        return themeName;
    }

    /**
     * Get main file.
     *
     * @return main file if it exists, otherwise null
     */
    private FileObject getMain() {
        // get main.php
        if (main != null) {
            return main;
        }
        FileObject application = getApplication();
        if (application != null) {
            main = application.getFileObject(CONFIG + "/main.php"); // NOI18N
        }
        if (main == null) {
            LOGGER.log(Level.INFO, "Not found main.php");
        } else {
            main.addFileChangeListener(new FileChangeAdapter() {
                @Override
                public void fileChanged(FileEvent fe) {
                    FileObject file = fe.getFile();
                    if (file != null) {
                        themeName = YiiUtils.getThemeName(main);
                    }
                }
            });
        }
        return main;
    }

    @Override
    public final void initDirectories() {
        // don't change order!
        setWebroot();
        setSystem();
        setApplication();
        setZii();
        setExt();
        setControllers();
        setViews();
        setThemes();
        setMessages();
    }

    private FileObject getDirectory(String path) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory != null && !StringUtils.isEmpty(path)) {
            return sourceDirectory.getFileObject(path);
        }
        return null;
    }
}
