/*
 * add license
 */
package org.nbphpcouncil.modules.php.yii.ui.actions;

import org.nbphpcouncil.modules.php.yii.Yii;
import org.nbphpcouncil.modules.php.yii.util.ProjectPropertiesSupport;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class PHPUnitInitAction extends BaseAction {

    private static final long serialVersionUID = -8653981594740402654L;
    private static PHPUnitInitAction INSTANCE = new PHPUnitInitAction();

    private PHPUnitInitAction() {
    }

    public static PHPUnitInitAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return Bundle.LBL_YiiAction(getPureName());
    }

    @Override
    @NbBundle.Messages("LBL_PHPUnitTestInitAction=PHPUnit Test Init")
    protected String getPureName() {
        return Bundle.LBL_PHPUnitTestInitAction();
    }

    @Override
    @NbBundle.Messages("MSG_PHPUnitInitAction=Complete")
    protected void actionPerformed(PhpModule phpModule) {
        if (!YiiUtils.isYii(phpModule)) {
            // called via shortcut
            return;
        }
        ProjectPropertiesSupport.setPHPUnit(phpModule);
        NotificationDisplayer.getDefault().notify(getFullName(), ImageUtilities.loadImageIcon(Yii.YII_ICON_16, true), Bundle.MSG_PHPUnitInitAction(), null);
    }
}
