/*
 * add license
 */
package org.nbphpcouncil.modules.php.yii;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.nbphpcouncil.modules.php.yii.commands.YiiScript;
import org.nbphpcouncil.modules.php.yii.ui.options.YiiOptions;
import org.nbphpcouncil.modules.php.yii.ui.wizards.NewProjectConfigurationPanel;
import org.nbphpcouncil.modules.php.yii.util.ProjectPropertiesSupport;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

/**
 *
 * @author junichi11
 */
public class YiiPhpModuleExtender extends PhpModuleExtender {

    private NewProjectConfigurationPanel panel;
    private static final Logger LOGGER = Logger.getLogger(YiiPhpModuleExtender.class.getName());

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
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        String yiic = YiiOptions.getInstance().getYiiScript();
        if (yiic == null || yiic.isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public String getWarningMessage() {
        return null;
    }

    @Override
    public Set<FileObject> extend(PhpModule phpModule) throws ExtendingException {
        boolean isSuccess = false;
        try {
            YiiScript script = YiiScript.forPhpModule(phpModule, true);
            isSuccess = script.initProject(phpModule);
        } catch (InvalidPhpExecutableException ex) {
            Exceptions.printStackTrace(ex);
        }
        boolean usePHPUnit = panel.usePHPUnit();
        HashSet<FileObject> files = new HashSet<FileObject>();
        if (isSuccess) {
            // set PHPUnit Test
            if (usePHPUnit) {
                ProjectPropertiesSupport.setPHPUnit(phpModule);
            }
            try {
                YiiUtils.createCodeCompletionFile(phpModule);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            FileObject sourceDirectory = phpModule.getSourceDirectory();
            FileObject config = null;
            FileObject index = null;
            if (sourceDirectory != null) {
                sourceDirectory.refresh();
                index = sourceDirectory.getFileObject("index.php"); // NOI18N
                config = sourceDirectory.getFileObject("protected/config/main.php"); // NOI18N
            }
            if (index != null) {
                files.add(index);
            }
            if (config != null) {
                files.add(config);
            }
        }
        return files;
    }

    private synchronized NewProjectConfigurationPanel getPanel() {
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
        }
        return panel;
    }
}
