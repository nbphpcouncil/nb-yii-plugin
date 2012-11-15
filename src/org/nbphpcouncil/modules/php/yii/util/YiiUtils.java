/*
 * TODO add license
 */
package org.nbphpcouncil.modules.php.yii.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.util.FileUtils;
import org.openide.filesystems.FileObject;
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
}
