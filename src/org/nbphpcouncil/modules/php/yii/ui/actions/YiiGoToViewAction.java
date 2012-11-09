/*
 * TODO add license
 */
package org.nbphpcouncil.modules.php.yii.ui.actions;

import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

/**
 *
 * @author junichi11
 */
public class YiiGoToViewAction extends GoToViewAction {

    private static final long serialVersionUID = 1722745601120023354L;
    private FileObject controller;
    private int offset;

    public YiiGoToViewAction(FileObject controller, int offset) {
        this.controller = controller;
        this.offset = offset;
    }

    @Override
    public boolean goToView() {
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement element = editorSupport.getElement(controller, offset);
        if (element instanceof PhpClass.Method) {
            PhpClass.Method method = (PhpClass.Method) element;
            FileObject view = YiiUtils.getView(controller, method);
            if (view != null) {
                UiUtils.open(view, DEFAULT_OFFSET);
                return true;
            }
        }
        return false;
    }
}
