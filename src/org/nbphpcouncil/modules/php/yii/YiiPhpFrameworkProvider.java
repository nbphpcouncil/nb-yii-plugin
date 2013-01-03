/**
 * @todo add license
 */
package org.nbphpcouncil.modules.php.yii;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.nbphpcouncil.modules.php.yii.commands.YiiScript;
import org.nbphpcouncil.modules.php.yii.editor.YiiEditorExtender;
import org.nbphpcouncil.modules.php.yii.ui.options.YiiOptions;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class YiiPhpFrameworkProvider extends PhpFrameworkProvider {

    private static final YiiPhpFrameworkProvider INSTANCE = new YiiPhpFrameworkProvider();
    private static final String ICON_PATH = "org/nbphpcouncil/modules/php/yii/ui/resources/yii_badge_8.png";
    private BadgeIcon badgeIcon;

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
     * Check whether project is Yii Framework. Find
     * WebRoot/appname/protected/yiic.
     *
     * @param pm PhpModule
     * @return boolean true if exist yiic file, otherwise false
     */
    @Override
    public boolean isInPhpModule(PhpModule pm) {
        FileObject sourceDirectory = pm.getSourceDirectory();
        if (sourceDirectory == null) {
            return false;
        }
        FileObject yiic = sourceDirectory.getFileObject("protected/yiic"); // NOI18N
        if (yiic == null || yiic.isFolder()) {
            return false;
        }

        return true;
    }

    /**
     * Get configuration files. Files is displayed on Important Files node.
     * appname/protected/config
     *
     * @param pm PhpModule
     * @return File[]
     */
    @Override
    public File[] getConfigurationFiles(PhpModule pm) {
        FileObject sourceDirectory = pm.getSourceDirectory();
        List<File> configs = new LinkedList<File>();
        if (sourceDirectory == null) {
            return configs.toArray(new File[configs.size()]);
        }
        FileObject config = sourceDirectory.getFileObject("protected/config"); // NOI18N
        for (FileObject child : config.getChildren()) {
            configs.add(FileUtil.toFile(child));
        }

        return configs.toArray(new File[configs.size()]);
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
        // set include path
        String path = YiiOptions.getInstance().getYiiScript();
        if (path != null && !path.isEmpty()) {
            path = path.replace(YiiScript.YII_SCRIPT_NAME_LONG, ""); // NOI18N
            List<String> includePaths = Collections.singletonList(path);
            properties = properties.setIncludePath(includePaths);
        }

        // add unit test and webroot directories
        FileObject tests = YiiUtils.getTestsDirectory(pm);
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
        return null;
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule phpModule) {
        return new YiiEditorExtender();
    }
}
