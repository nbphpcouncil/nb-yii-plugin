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

import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.nbphpcouncil.modules.php.yii.preferences.YiiPreferences;
import org.nbphpcouncil.modules.php.yii.ui.options.YiiCustomizerPanel;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class YiiPhpModuleCustomizerExtender extends PhpModuleCustomizerExtender {

    private YiiCustomizerPanel component;
    private final boolean isEnabled;
    private final boolean useAutoCreateView;
    private final boolean isFallbackToDefaultViews;
    private final String systemPath;
    private final String applicationPath;
    private final String ziiPath;
    private final String extPath;
    private final String controllersPath;
    private final String viewsPath;
    private final String themesPath;
    private final String messagesPath;

    public YiiPhpModuleCustomizerExtender(PhpModule phpModule) {
        isEnabled = YiiPreferences.isEnabled(phpModule);
        useAutoCreateView = YiiPreferences.useAutoCreateView(phpModule);
        isFallbackToDefaultViews = YiiPreferences.isFallbackToDefaultViews(phpModule);
        systemPath = YiiPreferences.getSystemPath(phpModule);
        applicationPath = YiiPreferences.getApplicationPath(phpModule);
        ziiPath = YiiPreferences.getZiiPath(phpModule);
        extPath = YiiPreferences.getExtPath(phpModule);
        controllersPath = YiiPreferences.getControllersPath(phpModule);
        viewsPath = YiiPreferences.getViewsPath(phpModule);
        themesPath = YiiPreferences.getThemesPath(phpModule);
        messagesPath = YiiPreferences.getMessagesPath(phpModule);
    }

    @NbBundle.Messages("LBL_Yii=Yii")
    @Override
    public String getDisplayName() {
        return Bundle.LBL_Yii();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public EnumSet<Change> save(PhpModule phpModule) {
        EnumSet<Change> change = null;
        // paths
        String systemPathForPanel = getPanel().getSystemPath();
        if (!systemPath.equals(systemPathForPanel)) {
            YiiPreferences.setSystemPath(phpModule, systemPathForPanel);
        }
        String applicationPathForPanel = getPanel().getApplicationPath();
        if (!applicationPath.equals(applicationPathForPanel)) {
            YiiPreferences.setApplicationPath(phpModule, applicationPathForPanel);
        }
        String ziiPathForPanel = getPanel().getZiiPath();
        if (!ziiPath.equals(ziiPathForPanel)) {
            YiiPreferences.setZiiPath(phpModule, ziiPathForPanel);
        }
        String extPathForPanel = getPanel().getExtPath();
        if (!extPath.equals(extPathForPanel)) {
            YiiPreferences.setExtPath(phpModule, extPathForPanel);
        }
        String controllerPathForPanel = getPanel().getControllersPath();
        if (!controllersPath.equals(controllerPathForPanel)) {
            YiiPreferences.setControllersPath(phpModule, controllerPathForPanel);
        }
        String viewsPathForPanel = getPanel().getViewsPath();
        if (!viewsPath.equals(viewsPathForPanel)) {
            YiiPreferences.setViewsPath(phpModule, viewsPathForPanel);
        }
        String themesPathForPanel = getPanel().getThemesPath();
        if (!themesPath.equals(themesPathForPanel)) {
            YiiPreferences.setThemesPath(phpModule, themesPathForPanel);
        }
        String messagesPathForPanel = getPanel().getMessagesPath();
        if (!messagesPath.equals(messagesPathForPanel)) {
            YiiPreferences.setMessagesPath(phpModule, messagesPathForPanel);
        }

        // init directories
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        if (yiiModule != null) {
            yiiModule.initDirectories();
        }

        boolean useAutoCreateViewForPanel = getPanel().useAutoCreateView();
        if (useAutoCreateView != useAutoCreateViewForPanel) {
            YiiPreferences.setAutoCreateViewFile(phpModule, useAutoCreateViewForPanel);
        }
        boolean isFallbackToDefaultViewsForPanel = getPanel().isFallbackToDefaultViews();
        if (isFallbackToDefaultViews != isFallbackToDefaultViewsForPanel) {
            YiiPreferences.setFallbackToDefaultViews(phpModule, isFallbackToDefaultViewsForPanel);
        }
        boolean isEnabledForPanel = getPanel().isEnabledPlugin();
        if (isEnabled != isEnabledForPanel) {
            YiiPreferences.setEnabled(phpModule, isEnabledForPanel);
            change = EnumSet.of(Change.FRAMEWORK_CHANGE);
        }
        return change;
    }

    private YiiCustomizerPanel getPanel() {
        if (component == null) {
            component = new YiiCustomizerPanel();
            component.setEnabledPlugin(isEnabled);
            component.setAutoCreateView(useAutoCreateView);
            component.setFallbackToDefaultViews(isFallbackToDefaultViews);
            component.setSystemPath(systemPath);
            component.setApplicationPath(applicationPath);
            component.setZiiPath(ziiPath);
            component.setExtPath(extPath);
            component.setControllersPath(controllersPath);
            component.setViewsPath(viewsPath);
            component.setThemesPath(themesPath);
            component.setMessagesPath(messagesPath);
        }
        return component;
    }
}
