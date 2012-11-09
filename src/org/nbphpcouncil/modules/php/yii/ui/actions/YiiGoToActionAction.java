/*
 * TODO add license
 */
package org.nbphpcouncil.modules.php.yii.ui.actions;

import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class YiiGoToActionAction extends GoToActionAction {

    private static final long serialVersionUID = -6076062544552244116L;
    private FileObject view;

    public YiiGoToActionAction(FileObject view) {
        this.view = view;
    }

    @Override
    public boolean goToAction() {
        FileObject controller = YiiUtils.getController(view);
        if (controller != null) {
            int offset = getActionMethodOffset(controller);
            UiUtils.open(controller, offset);
            return true;
        }
        return false;
    }

    /**
     * Get action method offset
     *
     * @param controller
     * @return offset
     */
    public int getActionMethodOffset(FileObject controller) {
        String actionMethodName = YiiUtils.getActionMethodName(view.getName());
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);

        for (PhpClass phpClass : editorSupport.getClasses(controller)) {
            if (!YiiUtils.isControllerName(phpClass.getName())) {
                continue;
            }
            if (actionMethodName == null) {
                break;
            }
            for (PhpClass.Method method : phpClass.getMethods()) {
                if (method.getName().equals(actionMethodName)) {
                    return method.getOffset();
                }
            }
        }

        return DEFAULT_OFFSET;
    }
}
