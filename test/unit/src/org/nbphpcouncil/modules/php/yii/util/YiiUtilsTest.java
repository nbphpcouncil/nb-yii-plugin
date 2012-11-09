/*
 * TODO add license
 */
package org.nbphpcouncil.modules.php.yii.util;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.netbeans.junit.NbTestCase;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author junichi11
 */
public class YiiUtilsTest extends NbTestCase {

    public YiiUtilsTest(String name) {
        super(name);
    }

    /**
     * Test of getIncludePath method, of class YiiUtils.
     */
    @Test
    public void testGetIncludePath() {
        FileObject dataDirectory = FileUtil.toFileObject(getDataDir());
        FileObject index = null;
        if (dataDirectory != null) {
            index = dataDirectory.getFileObject("include_path_test.php");
        }
        List<String> expResult = new ArrayList<String>();
        expResult.add("../../yii/framework");
        List result = YiiUtils.getIncludePath(index);
        assertEquals(expResult, result);
    }

    /**
     * Test of getControllerFileName method, of class YiiUtils.
     */
    @Test
    public void testGetControllerFileName() {
        assertEquals("SiteController", YiiUtils.getControllerFileName("site"));
        assertEquals("SiteController", YiiUtils.getControllerFileName("Site"));
        assertEquals(null, YiiUtils.getControllerFileName(""));
        assertEquals(null, YiiUtils.getControllerFileName(null));
    }

    /**
     * Test of isControllerName method, of class YiiUtils.
     */
    @Test
    public void testIsControllerName() {
        assertTrue(YiiUtils.isControllerName("SiteController"));
        assertFalse(YiiUtils.isControllerName("Demo"));
        assertFalse(YiiUtils.isControllerName(""));
        assertFalse(YiiUtils.isControllerName(null));
    }

    /**
     * Test of getActionMethodName method, of class YiiUtils.
     */
    @Test
    public void testGetActionMethodName() {
        assertEquals("actionIndex", YiiUtils.getActionMethodName("index"));
        assertEquals("actionError", YiiUtils.getActionMethodName("error"));
        assertEquals(null, YiiUtils.getActionMethodName(""));
        assertEquals(null, YiiUtils.getActionMethodName(null));
    }

    /**
     * Test of getViewFolderName method, of class YiiUtils.
     */
    @Test
    public void testGetViewFolderName() {
        assertEquals("site", YiiUtils.getViewFolderName("SiteController"));
        assertEquals(null, YiiUtils.getViewFolderName("SiteControllers"));
        assertEquals(null, YiiUtils.getViewFolderName(""));
        assertEquals(null, YiiUtils.getViewFolderName(null));
    }

    /**
     * Test of getViewName method, of class YiiUtils.
     */
    @Test
    public void testGetViewName() {
        PhpClass phpClass = new PhpClass("SiteController", "SiteController");
        phpClass.addMethod("actionIndex", "actionIndex");
        for (PhpClass.Method method : phpClass.getMethods()) {
            assertEquals("index", YiiUtils.getViewName(method));
        }
        phpClass = new PhpClass("SiteController", "SiteController");
        phpClass.addMethod("actions", "actions");
        for (PhpClass.Method method : phpClass.getMethods()) {
            assertEquals(null, YiiUtils.getViewName(method));
        }
    }

    /**
     * Test of isActionMethodName method, of class YiiUtils.
     */
    @Test
    public void testIsActionMethodName() {
        assertTrue(YiiUtils.isActionMethodName("actionIndex"));
        assertFalse(YiiUtils.isActionMethodName("actionindex"));
        assertFalse(YiiUtils.isActionMethodName(""));
        assertFalse(YiiUtils.isActionMethodName(null));
    }

    /**
     * Test of toFirstUpperCase method, of class YiiUtils.
     */
    @Test
    public void testToFirstUpperCase() {
        assertEquals("Site", YiiUtils.toFirstUpperCase("site"));
        assertEquals("Demo", YiiUtils.toFirstUpperCase("demo"));
        assertEquals("Blog", YiiUtils.toFirstUpperCase("BLOG"));
        assertEquals("A", YiiUtils.toFirstUpperCase("a"));
        assertEquals(null, YiiUtils.toFirstUpperCase(""));
        assertEquals(null, YiiUtils.toFirstUpperCase(null));
    }
}
