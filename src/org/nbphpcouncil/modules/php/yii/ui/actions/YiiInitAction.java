/*
 * add license
 */
package org.nbphpcouncil.modules.php.yii.ui.actions;

import java.io.IOException;
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
public class YiiInitAction extends BaseAction {

    public static YiiInitAction INSTANCE = new YiiInitAction();
    private static final long serialVersionUID = -8225489767443450330L;

    private YiiInitAction() {
    }

    public static YiiInitAction getInstance() {
        return INSTANCE;
    }

    @Override
    @NbBundle.Messages({
        "# {0} - action name",
        "LBL_YiiAction=Yii: {0}"})
    protected String getFullName() {
        return Bundle.LBL_YiiAction(getPureName());
    }

    @Override
    @NbBundle.Messages("LBL_YiiInitAction=Init")
    protected String getPureName() {
        return Bundle.LBL_YiiInitAction();
    }

    @Override
    @NbBundle.Messages({
        "MSG_YiiInitAction=Complete",
        "MSG_YiiInitActionFail=Can't create code completion file"
    })
    protected void actionPerformed(PhpModule phpModule) {
        // called via shortcut
        if (!YiiUtils.isYii(phpModule)) {
            return;
        }
        ProjectPropertiesSupport.setYiiIncludePath(phpModule);
        try {
            // add code completion file
            YiiUtils.createCodeCompletionFile(phpModule);
        } catch (IOException ex) {
            NotificationDisplayer.getDefault().notify(getFullName(), ImageUtilities.loadImageIcon(Yii.YII_ICON_16, true), Bundle.MSG_YiiInitActionFail(), null);
            return;
        }
        NotificationDisplayer.getDefault().notify(getFullName(), ImageUtilities.loadImageIcon(Yii.YII_ICON_16, true), Bundle.MSG_YiiInitAction(), null);
    }
}
