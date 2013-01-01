/*
 * add license
 */
package org.nbphpcouncil.modules.php.yii.util;

import java.util.List;
import javax.swing.DefaultListModel;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.project.PhpProject;
import org.netbeans.modules.php.project.classpath.BasePathSupport.Item;
import org.netbeans.modules.php.project.classpath.IncludePathSupport;
import org.netbeans.modules.php.project.ui.customizer.PhpProjectProperties;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.PropertyEvaluator;
import org.netbeans.spi.project.support.ant.ReferenceHelper;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class ProjectPropertiesSupport {

    /**
     * Set include path
     *
     * @param phpModule
     * @param paths path is relative path
     */
    public static void setIncludePath(PhpModule phpModule, List<String> paths) {
        PhpProject phpProject = getPhpProject(phpModule);
        if (phpProject == null) {
            return;
        }

        PhpProjectProperties properties = createProjectProperties(phpProject);
        DefaultListModel includePathListModel = properties.getIncludePathListModel();
        for (String path : paths) {
            Item item = Item.create(path);
            if (!includePathListModel.contains(item)) {
                includePathListModel.addElement(item);
            }
        }
        properties.save();
    }

    /**
     * Set Yii framework path to include path
     *
     * @param phpModule
     */
    public static void setYiiIncludePath(PhpModule phpModule) {
        FileObject index = phpModule.getSourceDirectory().getFileObject("index.php"); //NOI18N
        if (index == null) {
            return;
        }
        List<String> includePath = YiiUtils.getIncludePath(index);
        setIncludePath(phpModule, includePath);
    }

    /**
     * Get PhpProject
     *
     * @param phpModule
     * @return PhpProject
     */
    public static PhpProject getPhpProject(PhpModule phpModule) {
        OpenProjects projects = OpenProjects.getDefault();
        if (phpModule == null || projects == null) {
            return null;
        }
        PhpProject phpProject = null;
        FileObject projectDirectory = phpModule.getProjectDirectory();
        for (Project project : projects.getOpenProjects()) {
            if (project.getProjectDirectory() == projectDirectory) {
                phpProject = project.getLookup().lookup(PhpProject.class);
            }
            if (phpProject != null) {
                break;
            }
        }
        return phpProject;
    }

    /**
     * Create project properties
     *
     * @param project
     * @return
     */
    private static PhpProjectProperties createProjectProperties(PhpProject project) {
        AntProjectHelper antProjectHelper = project.getHelper();
        ReferenceHelper referenceHelper = project.getRefHelper();
        PropertyEvaluator evaluator = project.getLookup().lookup(PropertyEvaluator.class);
        IncludePathSupport includePathSupport = new IncludePathSupport(evaluator, referenceHelper, antProjectHelper);
        PhpProjectProperties properties = new PhpProjectProperties(project, includePathSupport, null);
        return properties;
    }
}
