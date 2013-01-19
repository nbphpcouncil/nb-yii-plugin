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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.nbphpcouncil.modules.php.yii.Yii;
import org.nbphpcouncil.modules.php.yii.YiiPhpFrameworkProvider;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
public class YiiUtils {

    private static final String YII_INCLUDE_PATH_REGEX = "^\\$yii=.+'(.+/framework)/yii\\.php';$"; // NOI18N
    private static final String CONTROLLER_SUFIX = "Controller"; // NOI18N
    private static final String CONTROLLER_RELATIVE_PATH_FORMAT = "../../../controllers/%s.php"; // NOI18N
    private static final String CONTROLLERS_DIRECTORY_NAME = "controllers"; // NOI18N
    private static final String ACTION_METHOD_PREFIX = "action";
    private static final String VIEW_RELATIVE_PATH_FORMAT = "../../views/%s/%s.php";
    private static final String NBPROJECT = "nbproject";
    private static final String PROTECTED_PATH = "protected/";
    private static final String VIEWS_PATH = PROTECTED_PATH + "views";
    private static final String CONTROLLERS_PATH = PROTECTED_PATH + "controllers";
    private static final String MODELS_PATH = PROTECTED_PATH + "models";
    private static final String TESTS_PATH = PROTECTED_PATH + "tests";

    /**
     * Check whether php module is yii
     *
     * @param phpModule
     * @return true if php module is yii, otherwiser false
     */
    public static boolean isYii(PhpModule phpModule) {
        if (phpModule == null) {
            return false;
        }
        return YiiPhpFrameworkProvider.getInstance().isInPhpModule(phpModule);
    }

    /**
     * Get include path.
     *
     * @param index index.php file
     * @return if exists include path, string. otherwise null.
     */
    public static List<String> getIncludePath(FileObject index) {
        List<String> lines = null;
        List<String> includePath = new ArrayList<String>();
        try {
            lines = index.asLines();
            Pattern pattern = Pattern.compile(YII_INCLUDE_PATH_REGEX);
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String path = matcher.group(1);
                    if (path.startsWith("/")) { // NOI18N
                        path = path.replaceFirst("/", ""); // NOI18N
                    }
                    includePath.add(path);
                    break;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return includePath;
    }

    /**
     * Check view file
     *
     * @param fo
     * @return true if file is view file, otherwise false.
     */
    public static boolean isView(FileObject fo) {
        if (!fo.isData() || !FileUtils.isPhpFile(fo)) {
            return false;
        }

        FileObject viewsDirectory = fo.getFileObject("../../../views"); // NOI18N
        if (viewsDirectory == null || !viewsDirectory.isFolder()) {
            return false;
        }
        if (!fo.getPath().contains(VIEWS_PATH)) {
            return false;
        }
        return true;
    }

    /**
     * Get view file object
     *
     * @param controller
     * @param actionId action name(view file name). e.g actionIndex -> index
     * @return view file object
     */
    public static FileObject getView(FileObject controller, String actionId) {
        String controllerId = getViewFolderName(controller.getName());
        String pathToView = String.format(VIEW_RELATIVE_PATH_FORMAT, controllerId, actionId);
        return controller.getFileObject(pathToView);
    }

    /**
     * Get view file object
     *
     * @param controller
     * @param method
     * @return view file object
     */
    public static FileObject getView(FileObject controller, PhpClass.Method method) {
        String viewFolderName = getViewFolderName(controller.getName());
        String viewName = getViewName(method);
        String pathToView = String.format(VIEW_RELATIVE_PATH_FORMAT, viewFolderName, viewName);
        return controller.getFileObject(pathToView);
    }

    /**
     * Check controller file
     *
     * @param fo
     * @return true if file is controller file, otherwise false.
     */
    public static boolean isController(FileObject fo) {
        return fo.isData() && FileUtils.isPhpFile(fo) && fo.getParent().getNameExt().equals(CONTROLLERS_DIRECTORY_NAME);
    }

    /**
     * Get controller file object
     *
     * @param view
     * @return controller file object
     */
    public static FileObject getController(FileObject view) {
        if (!isView(view)) {
            return null;
        }
        String controllerName = getControllerFileName(view.getParent().getNameExt());
        String pathToController = String.format(CONTROLLER_RELATIVE_PATH_FORMAT, controllerName);
        return view.getFileObject(pathToController);

    }

    /**
     * Get controller file name. e.g. SiteController,DemoController
     *
     * @param viewFolderName each views directory name
     * @return controller file name. ControllerName + Controller
     */
    public static String getControllerFileName(String viewFolderName) {
        viewFolderName = toFirstUpperCase(viewFolderName);
        if (viewFolderName == null) {
            return null;
        }
        return viewFolderName + CONTROLLER_SUFIX;
    }

    /**
     * Check controller class name
     *
     * @param controller
     * @return true if controller class, otherwise false
     */
    public static boolean isControllerName(String controller) {
        if (controller == null) {
            return false;
        }
        return controller.endsWith(CONTROLLER_SUFIX);
    }

    /**
     * Get controller action method name
     *
     * @param view file name
     * @return action method name
     */
    public static String getActionMethodName(String view) {
        if (view == null || view.isEmpty()) {
            return null;
        }
        return ACTION_METHOD_PREFIX + toFirstUpperCase(view);
    }

    /**
     * Get view folder name
     *
     * @param controllerName
     * @return
     */
    public static String getViewFolderName(String controllerName) {
        if (!isControllerName(controllerName)) {
            return null;
        }
        String name = controllerName.replace(CONTROLLER_SUFIX, ""); // NOI18N
        name = name.toLowerCase();
        return name;
    }

    /**
     * Get view name
     *
     * @param method
     * @return
     */
    public static String getViewName(PhpClass.Method method) {
        String name = method.getName();
        if (!isActionMethodName(name)) {
            return null;
        }
        name = name.replace(ACTION_METHOD_PREFIX, ""); // NOI18N
        return name.toLowerCase();
    }

    /**
     * Check action method name
     *
     * @param name
     * @return
     */
    public static boolean isActionMethodName(String name) {
        if (name == null || !name.startsWith(ACTION_METHOD_PREFIX) || name.isEmpty()) {
            return false;
        }
        name = name.replace(ACTION_METHOD_PREFIX, ""); // NOI18N
        String first = name.substring(0, 1);
        if (!first.equals(first.toUpperCase())) {
            return false;
        }
        return true;
    }

    /**
     * Converts the first of characters to upper case.
     *
     * Examples: "apple" -> "Apple", "Banana" -> "Banana", "ORANGE" -> "Orange"
     *
     * @param string
     * @return the converted string
     */
    public static String toFirstUpperCase(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }
        string = string.toLowerCase();
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    /**
     * Create code completion file
     *
     * @param phpModule
     * @throws IOException
     */
    public static void createCodeCompletionFile(PhpModule phpModule) throws IOException {
        if (!isYii(phpModule)) {
            return;
        }
        FileObject codeCompletion = FileUtil.getConfigFile(Yii.YII_CODE_COMPLETION_CONFIG_PATH);
        FileObject nbproject = getNbproject(phpModule);
        if (nbproject != null && codeCompletion != null) {
            codeCompletion.copy(nbproject, codeCompletion.getName(), codeCompletion.getExt());
        }
    }

    /**
     * Get nbproject directory
     *
     * @param phpModule
     * @return
     */
    public static FileObject getNbproject(PhpModule phpModule) {
        FileObject projectDirectory = phpModule.getProjectDirectory();
        FileObject nbproject = null;
        if (projectDirectory != null) {
            nbproject = projectDirectory.getFileObject(NBPROJECT);
        }
        return nbproject;
    }

    /**
     * Get views directory (protected/views)
     *
     * @param phpModule
     * @return
     */
    public static FileObject getViewsDirectory(PhpModule phpModule) {
        return getDirectory(phpModule, VIEWS_PATH);
    }

    /**
     * Get controllers directory (protected/controllers)
     *
     * @param phpModule
     * @return
     */
    public static FileObject getControllersDirectory(PhpModule phpModule) {
        return getDirectory(phpModule, CONTROLLERS_PATH);
    }

    /**
     * Get models directory (protected/models)
     *
     * @param phpModule
     * @return
     */
    public static FileObject getModelsDirectory(PhpModule phpModule) {
        return getDirectory(phpModule, MODELS_PATH);
    }

    /**
     * Get tests directory (protected/tests)
     *
     * @param phpModule
     * @return
     */
    public static FileObject getTestsDirectory(PhpModule phpModule) {
        return getDirectory(phpModule, TESTS_PATH);
    }

    /**
     * Get directory
     *
     * @param phpModule
     * @param path relative path from source directory
     * @return
     */
    public static FileObject getDirectory(PhpModule phpModule, String path) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return null;
        }
        return sourceDirectory.getFileObject(path);
    }
}
