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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.nbphpcouncil.modules.php.yii.commands.YiiFrameworkCommandSupport;
import org.nbphpcouncil.modules.php.yii.commands.YiiScript;
import org.nbphpcouncil.modules.php.yii.editor.YiiEditorExtender;
import org.nbphpcouncil.modules.php.yii.preferences.YiiPreferences;
import org.nbphpcouncil.modules.php.yii.ui.options.YiiOptions;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.spi.phpmodule.ImportantFilesImplementation;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class YiiPhpFrameworkProvider extends PhpFrameworkProvider {

    protected static final RequestProcessor RP = new RequestProcessor(YiiPhpFrameworkProvider.class);
    private static final YiiPhpFrameworkProvider INSTANCE = new YiiPhpFrameworkProvider();
    private static final String ICON_PATH = "org/nbphpcouncil/modules/php/yii/ui/resources/yii_badge_8.png";
    private final BadgeIcon badgeIcon;

    @NbBundle.Messages({
        "LBL_FrameworkName=Yii PHP Web Framework",
        "LBL_FrameworkDescription=Yii PHP Web Framework"
    })
    private YiiPhpFrameworkProvider() {
        super("Yii PHP Web Framework", Bundle.LBL_FrameworkName(), Bundle.LBL_FrameworkDescription()); // NOI18N
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                YiiPhpFrameworkProvider.class.getResource("/" + ICON_PATH)); // NOI18N
    }

    @PhpFrameworkProvider.Registration(position = 800)
    public static YiiPhpFrameworkProvider getInstance() {
        return INSTANCE;
    }

    /**
     * Get Badge Icon. BadgeIcon size is 8x8 px.
     *
     * @return BadgeIcon yii_badge_8.png
     */
    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    /**
     * Check whether project is Yii Framework.
     *
     * @param pm PhpModule
     * @return
     */
    @Override
    public boolean isInPhpModule(PhpModule pm) {
        // check user settings
        return YiiPreferences.isEnabled(pm);
    }

    /**
     * Get configuration files. Files is displayed on Important Files node.
     * appname/protected/config
     *
     * @param pm PhpModule
     * @return ConfigurationFiles
     */
    @Override
    public ImportantFilesImplementation getConfigurationFiles2(PhpModule phpModule) {
        return new ConfigurationFiles(phpModule);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule pm) {
        return new YiiPhpModuleExtender();
    }

    /**
     * Get PhpModuleProperties. This method is called when only creating new
     * project. Please, notice that properties setXXX() method has return value.
     *
     * @param pm
     * @return
     */
    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule pm) {
        PhpModuleProperties properties = new PhpModuleProperties();
        FileObject sourceDirectory = pm.getSourceDirectory();
        if (sourceDirectory == null) {
            return properties;
        }

        // set index file
        FileObject index = sourceDirectory.getFileObject("index.php"); // NOI18N
        if (index != null) {
            properties = properties.setIndexFile(index);
        }

        // set include path
        String path = YiiOptions.getInstance().getYiiScript();
        if (path != null && !path.isEmpty()) {
            path = path.replace(YiiScript.YII_SCRIPT_NAME_LONG, ""); // NOI18N
            List<String> includePaths = Collections.singletonList(path);
            properties = properties.setIncludePath(includePaths);
        }

        // add unit test and webroot directories
        FileObject tests = sourceDirectory.getFileObject("protected/tests"); // NOI18N
        if (tests != null) {
            properties = properties.setTests(tests);
        }
        return properties;
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule pm) {
        return new YiiActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule pm) {
        return null;
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule pm) {
        return new YiiFrameworkCommandSupport(pm);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return new YiiEditorExtender();
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new YiiPhpModuleCustomizerExtender(phpModule);
    }

    @NbBundle.Messages({
        "# {0} - name",
        "YiiPhpFrameworkProvider.autoditection=Yii autoditection : {0}",
        "YiiPhpFrameworkProvider.autoditection.action=If you want to enable as Yii project, please click here."
    })
    @Override
    public void phpModuleOpened(PhpModule phpModule) {
        // autodetection
        if (!isInPhpModule(phpModule)) {
            RP.schedule(new YiiAutoDetectionTask(phpModule), 1, TimeUnit.MINUTES);
        }
    }

    //~ Inner classes
    private class YiiAutoDetectionTask implements Runnable {

        private final PhpModule phpModule;
        private Notification notification;

        public YiiAutoDetectionTask(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public void run() {
            // automatic search
            YiiModule yiiModule = YiiModuleFactory.create(phpModule);
            FileObject webrootDirectory = yiiModule.getWebroot();
            if (webrootDirectory == null) {
                return;
            }

            FileObject yiic = webrootDirectory.getFileObject("protected/yiic"); // NOI18N
            if (yiic != null && !yiic.isFolder()) {
                notification = NotificationDisplayer.getDefault().notify(
                        Bundle.YiiPhpFrameworkProvider_autoditection(phpModule.getDisplayName()), // title
                        NotificationDisplayer.Priority.LOW.getIcon(), // icon
                        Bundle.YiiPhpFrameworkProvider_autoditection_action(), //detail
                        new AutoDetectionActionListener(), // action
                        NotificationDisplayer.Priority.LOW // priority
                );
            }
        }

        private class AutoDetectionActionListener implements ActionListener {

            public AutoDetectionActionListener() {
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                YiiPreferences.setEnabled(phpModule, true);
                phpModule.notifyPropertyChanged(new PropertyChangeEvent(this, PhpModule.PROPERTY_FRAMEWORKS, null, null));
                notification.clear();
            }
        }
    }
}
