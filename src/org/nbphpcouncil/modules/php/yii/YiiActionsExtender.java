/*
 * TODO add license
 */
package org.nbphpcouncil.modules.php.yii;

import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.nbphpcouncil.modules.php.yii.ui.actions.YiiGoToActionAction;
import org.nbphpcouncil.modules.php.yii.ui.actions.YiiGoToViewAction;
import org.nbphpcouncil.modules.php.yii.ui.actions.YiiInitAction;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.actions.GoToActionAction;
import org.netbeans.modules.php.spi.framework.actions.GoToViewAction;
import org.netbeans.modules.php.spi.framework.actions.RunCommandAction;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class YiiActionsExtender extends PhpModuleActionsExtender {

    @Override
    public String getMenuName() {
        return NbBundle.getMessage(YiiActionsExtender.class, "LBL_MenuName");
    }

    @Override
    public List<? extends Action> getActions() {
        return Collections.singletonList(YiiInitAction.getInstance());
    }

    @Override
    public RunCommandAction getRunCommandAction() {
        return super.getRunCommandAction(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isViewWithAction(FileObject fo) {
        return YiiUtils.isView(fo);
    }

    @Override
    public boolean isActionWithView(FileObject fo) {
        return YiiUtils.isController(fo);
    }

    @Override
    public GoToActionAction getGoToActionAction(FileObject fo, int offset) {
        return new YiiGoToActionAction(fo);
    }

    @Override
    public GoToViewAction getGoToViewAction(FileObject fo, int offset) {
        return new YiiGoToViewAction(fo, offset);
    }
}
