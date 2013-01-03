/**
 * @todo add license
 */
package org.nbphpcouncil.modules.php.yii.ui.options;

import java.util.List;
import java.util.prefs.Preferences;
import org.nbphpcouncil.modules.php.yii.commands.YiiScript;
import org.netbeans.modules.php.api.util.FileUtils;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public class YiiOptions {

    private static final YiiOptions INSTANCE = new YiiOptions();
    private static final String PREFERENCES_PATH = "yii"; // NOI18N
    private static final String PARAMS_FOR_PROJECT = "default.params.project"; // NOI18N

    private YiiOptions() {
    }

    public static YiiOptions getInstance() {
        return INSTANCE;
    }

    public synchronized String getYiiScript() {
        String yiiScript = getPreferences().get(PARAMS_FOR_PROJECT, null);
        if (yiiScript == null) {
            List<String> scripts = FileUtils.findFileOnUsersPath(YiiScript.YII_SCRIPT_NAME_LONG);
            if (!scripts.isEmpty()) {
                yiiScript = scripts.get(0);
                setYiiScript(yiiScript);
            }
        }

        return yiiScript;
    }

    public void setYiiScript(String yiiScript) {
        getPreferences().put(PARAMS_FOR_PROJECT, yiiScript);
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(YiiOptions.class).node(PREFERENCES_PATH);
    }
}
