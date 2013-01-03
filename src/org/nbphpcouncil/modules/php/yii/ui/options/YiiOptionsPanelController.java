/**
 * @todo add license
 */
package org.nbphpcouncil.modules.php.yii.ui.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.nbphpcouncil.modules.php.yii.commands.YiiScript;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

@OptionsPanelController.SubRegistration(
        location = UiUtils.OPTIONS_PATH,
        id = YiiScript.OPTIONS_SUB_PATH,
        displayName = "#AdvancedOption_DisplayName_Yii",
        keywords = "#AdvancedOption_Keywords_Yii",
        keywordsCategory = "org-netbeans-modules-php-project-ui-options-PHPOptionsCategory/Yii")
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_Yii=Yii", "AdvancedOption_Keywords_Yii=yii"})
public final class YiiOptionsPanelController extends OptionsPanelController {

    private YiiOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private YiiOptionsPanel getPanel() {
        if (panel == null) {
            panel = new YiiOptionsPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
