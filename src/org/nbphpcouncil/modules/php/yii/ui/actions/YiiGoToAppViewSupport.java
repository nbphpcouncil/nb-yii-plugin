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

import org.nbphpcouncil.modules.php.yii.YiiModule;
import org.nbphpcouncil.modules.php.yii.YiiModuleFactory;
import org.nbphpcouncil.modules.php.yii.preferences.YiiPreferences;
import static org.nbphpcouncil.modules.php.yii.util.YiiUtils.createFile;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

public final class YiiGoToAppViewSupport extends YiiGoToViewSupport {

    private static final String PATH_TO_THEME_VIEW_FORMAT = "%s/views/%s%s/%s.php"; // NOI18N
    private static final String PATH_TO_DEFAULT_VIEW_FORMAT = "%s%s/%s.php"; // NOI18N
    private String viewPath;
    private String themeName;
    private boolean isTheme;

    public YiiGoToAppViewSupport(FileObject controller, String actionId) {
        super(controller, actionId);
        init();
    }

    private void init() {
        if (controller == null || phpModule == null) {
            return;
        }

        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        themeName = yiiModule.getThemeName();
        isTheme = !StringUtils.isEmpty(themeName);
        setViewPath(createViewPath(false));
    }

    @Override
    public FileObject getRelativeView() {
        if (controller == null || phpModule == null) {
            return null;
        }
        // get module
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        boolean isFallback = YiiPreferences.isFallbackToDefaultViews(phpModule);

        FileObject view = null;
        if (isTheme) {
            // theme view
            FileObject themes = yiiModule.getThemes();
            if (themes != null) {
                view = themes.getFileObject(viewPath);
            }
        } else {
            // default view
            view = getDefaultView();
        }

        // fall back to default views
        if (view == null && isTheme && isFallback) {
            view = getDefaultView();
        }

        return view;
    }

    @Override
    public FileObject createRelativeView() {
        if (controller == null || phpModule == null) {
            return null;
        }

        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        FileObject view = null;
        if (yiiModule == null) {
            return view;
        }

        if (isTheme) {
            FileObject themes = yiiModule.getThemes();
            if (themes != null) {
                view = createFile(themes, viewPath);
            }
        } else {
            FileObject views = yiiModule.getViews();
            if (views != null) {
                view = createFile(views, viewPath);
            }
        }
        return view;
    }

    /**
     * Create view path.
     *
     * @param isDefaultView force default view file
     * @return view path
     */
    private String createViewPath(boolean isDefaultView) {
        String path = ""; // NOI18N
        if (subPathToController == null) {
            return path;
        }
        if (isTheme && !isDefaultView) {
            path = String.format(PATH_TO_THEME_VIEW_FORMAT, themeName, subPathToController, controllerId, actionId);
        } else {
            path = String.format(PATH_TO_DEFAULT_VIEW_FORMAT, subPathToController, controllerId, actionId);
        }

        return path;
    }

    /**
     * Get default view file.
     *
     * @return default view file
     */
    private FileObject getDefaultView() {
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        String path = createViewPath(true);
        FileObject views = yiiModule.getViews();
        if (views != null) {
            return views.getFileObject(path);
        }
        return null;
    }

    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
}
