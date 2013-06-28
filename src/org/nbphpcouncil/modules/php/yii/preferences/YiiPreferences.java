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
package org.nbphpcouncil.modules.php.yii.preferences;

import java.util.prefs.Preferences;
import org.netbeans.modules.php.api.phpmodule.PhpModule;

/**
 *
 * @author junichi11
 */
public class YiiPreferences {

    private static final String ENABLED = "enabled"; // NOI18N
    private static final String USE_AUTO_CREATE_VIEW = "use-auto-create-view"; // NOI18N
    private static final String FALLBACK_TO_DEFAULT_VIEWS = "fallback-to-default-views"; // NOI18N
    private static final String VIEWS_PATH = "views-path"; // NOI18N
    private static final String CONTROLLERS_PATH = "controllers-path"; // NOI18N
    private static final String EXT_PATH = "ext-path"; // NOI18N
    private static final String ZII_PATH = "zii-path"; // NOI18N
    private static final String APPLICATION_PATH = "application-path"; // NOI18N
    private static final String SYSTEM_PATH = "system-path"; // NOI18N
    private static final String THEMES_PATH = "themes-path"; // NOI18N
    private static final String MESSAGES_PATH = "messages-path"; // NOI18N

    public static boolean isEnabled(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(ENABLED, false);
    }

    public static void setEnabled(PhpModule phpModule, boolean isEnabled) {
        getPreferences(phpModule).putBoolean(ENABLED, isEnabled);
    }

    public static boolean useAutoCreateView(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(USE_AUTO_CREATE_VIEW, false);
    }

    public static void setAutoCreateViewFile(PhpModule phpModule, boolean use) {
        getPreferences(phpModule).putBoolean(USE_AUTO_CREATE_VIEW, use);
    }

    public static boolean isFallbackToDefaultViews(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(FALLBACK_TO_DEFAULT_VIEWS, false);
    }

    public static void setFallbackToDefaultViews(PhpModule phpModule, boolean fallback) {
        getPreferences(phpModule).putBoolean(FALLBACK_TO_DEFAULT_VIEWS, fallback);
    }

    public static String getSystemPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(SYSTEM_PATH, ""); // NOI18N
    }

    public static void setSystemPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(SYSTEM_PATH, path);
    }

    public static String getApplicationPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(APPLICATION_PATH, ""); // NOI18N
    }

    public static void setApplicationPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(APPLICATION_PATH, path);
    }

    public static String getZiiPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(ZII_PATH, ""); // NOI18N
    }

    public static void setZiiPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(ZII_PATH, path);
    }

    public static String getExtPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(EXT_PATH, ""); // NOI18N
    }

    public static void setExtPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(EXT_PATH, path);
    }

    public static String getControllersPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(CONTROLLERS_PATH, ""); // NOI18N
    }

    public static void setControllersPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(CONTROLLERS_PATH, path);
    }

    public static String getViewsPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(VIEWS_PATH, ""); // NOI18N
    }

    public static void setViewsPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(VIEWS_PATH, path);
    }

    public static String getThemesPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(THEMES_PATH, ""); // NOI18N
    }

    public static void setThemesPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(THEMES_PATH, path);
    }

    public static String getMessagesPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(MESSAGES_PATH, ""); // NOI18N
    }

    public static void setMessagesPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(MESSAGES_PATH, path);
    }

    private static Preferences getPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(YiiPreferences.class, true);
    }
}
