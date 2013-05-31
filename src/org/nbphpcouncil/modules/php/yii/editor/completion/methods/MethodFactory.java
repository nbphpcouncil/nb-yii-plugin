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
package org.nbphpcouncil.modules.php.yii.editor.completion.methods;

import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class MethodFactory {

    private static final String IMPORT_METHOD = "import"; // NOI18N
    private static final String RENDER_METHOD = "render"; // NOI18N
    private static final String RENDER_PARTIAL_METHOD = "renderPartial"; // NOI18N
    private static final String BEGIN_CONTENT_METHOD = "beginContent"; // NOI18N
    private static final String BEGIN_CACHE_METHOD = "beginCache"; // NOI18N
    private static final String WIDGET_METHOD = "widget"; // NOI18N
    private static final String BEGIN_WIDGET_METHOD = "beginWidget"; // NOI18N
    private static final String END_WIDGET_METHOD = "endWidget"; // NOI18N
    private static final String CREATE_WIDGET_METHOD = "createWidget"; // NOI18N
    public static final List<String> METHODS = Arrays.asList(
            IMPORT_METHOD,
            RENDER_METHOD,
            RENDER_PARTIAL_METHOD,
            BEGIN_CONTENT_METHOD,
            BEGIN_CACHE_METHOD,
            WIDGET_METHOD,
            BEGIN_WIDGET_METHOD,
            END_WIDGET_METHOD,
            CREATE_WIDGET_METHOD);

    public static Method create(String methodName, FileObject currentFile, PhpModule phpModule) {
        if (StringUtils.isEmpty(methodName)) {
            return null;
        }

        // create method
        Method method = null;
        if (methodName.equals(RENDER_METHOD)
                || methodName.equals(RENDER_PARTIAL_METHOD)
                || methodName.equals(BEGIN_CONTENT_METHOD)) {
            method = new RenderMethod(currentFile, phpModule);
        } else if (methodName.equals(WIDGET_METHOD)
                || methodName.equals(BEGIN_WIDGET_METHOD)
                || methodName.equals(END_WIDGET_METHOD)
                || methodName.equals(CREATE_WIDGET_METHOD)) {
            method = new WidgetMethod(currentFile, phpModule);
        } else if (methodName.equals(IMPORT_METHOD)
                || methodName.equals(BEGIN_CACHE_METHOD)) {
            method = new ImportMethod(currentFile, phpModule);
        }
        return method;
    }
}
