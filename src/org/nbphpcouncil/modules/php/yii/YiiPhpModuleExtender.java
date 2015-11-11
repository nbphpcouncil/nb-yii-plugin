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
package org.nbphpcouncil.modules.php.yii;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.nbphpcouncil.modules.php.yii.commands.YiiScript;
import org.nbphpcouncil.modules.php.yii.preferences.YiiPreferences;
import org.nbphpcouncil.modules.php.yii.ui.options.YiiOptions;
import org.nbphpcouncil.modules.php.yii.ui.wizards.NewProjectConfigurationPanel;
import org.nbphpcouncil.modules.php.yii.util.ProjectPropertiesSupport;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
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
        return !StringUtils.isEmpty(yiic);
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
            YiiScript script = YiiScript.forPhpModule(phpModule, true, true);
            isSuccess = script.initProject(phpModule);
        } catch (InvalidPhpExecutableException ex) {
            Exceptions.printStackTrace(ex);
        }
        boolean usePHPUnit = panel.usePHPUnit();
        HashSet<FileObject> files = new HashSet<>();
        if (isSuccess) {
            // update module
            // some directories may null
            YiiModule yiiModule = YiiModuleFactory.create(phpModule);
            FileUtil.refreshFor(FileUtil.toFile(yiiModule.getWebroot()));
            yiiModule.initDirectories();

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
            // set enabled
            YiiPreferences.setEnabled(phpModule, true);
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
