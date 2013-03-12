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

    /**
     * Test of toFirstCharUpperCase method, of class YiiUtils.
     */
    @Test
    public void testToFirstCharUpperCase() {
        assertEquals("Site", YiiUtils.toFirstCharUpperCase("site"));
        assertEquals("Demo", YiiUtils.toFirstCharUpperCase("demo"));
        assertEquals("BLOG", YiiUtils.toFirstCharUpperCase("BLOG"));
        assertEquals("SomeAction", YiiUtils.toFirstCharUpperCase("someAction"));
        assertEquals("A", YiiUtils.toFirstCharUpperCase("a"));
        assertEquals(null, YiiUtils.toFirstCharUpperCase(""));
        assertEquals(null, YiiUtils.toFirstCharUpperCase(null));
    }
}
