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

import java.util.Arrays;
import org.junit.Test;
import org.nbphpcouncil.modules.php.yii.YiiModule.PATH_ALIAS;
import org.netbeans.junit.NbTestCase;

/**
 *
 * @author junichi11
 */
public class YiiPathAliasSupportTest extends NbTestCase {

    public YiiPathAliasSupportTest(String name) {
        super(name);
    }

    /**
     * Test of splitAliasPath method, of class YiiPathAliasSupport.
     */
    @Test
    public void testSplitAliasPath() {
        String[] expResult1 = {"application", "component", "MyClass"};
        assertEquals(Arrays.asList(expResult1), Arrays.asList(YiiPathAliasSupport.splitAliasPath("application.component.MyClass")));
        String[] expResult2 = {"system"};
        assertEquals(Arrays.asList(expResult2), Arrays.asList(YiiPathAliasSupport.splitAliasPath("system")));
        assertEquals(1, YiiPathAliasSupport.splitAliasPath("").length);
        assertEquals("", YiiPathAliasSupport.splitAliasPath("")[0]);
        assertEquals(null, YiiPathAliasSupport.splitAliasPath(null));
    }

    /**
     * Test of toPathAlias method, of class YiiPathAliasSupport.
     */
    @Test
    public void testToPathAlias() {
        assertEquals(PATH_ALIAS.APPLICATION, YiiPathAliasSupport.toPathAlias("application"));
        assertEquals(PATH_ALIAS.EXT, YiiPathAliasSupport.toPathAlias("ext"));
        assertEquals(PATH_ALIAS.SYSTEM, YiiPathAliasSupport.toPathAlias("system"));
        assertEquals(PATH_ALIAS.WEBROOT, YiiPathAliasSupport.toPathAlias("webroot"));
        assertEquals(PATH_ALIAS.ZII, YiiPathAliasSupport.toPathAlias("zii"));
        assertEquals(PATH_ALIAS.NONE, YiiPathAliasSupport.toPathAlias("test"));
        assertEquals(PATH_ALIAS.NONE, YiiPathAliasSupport.toPathAlias(""));
        assertEquals(PATH_ALIAS.NONE, YiiPathAliasSupport.toPathAlias(null));
    }
}
