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
package org.nbphpcouncil.modules.php.yii.ui.actions;

import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.text.JTextComponent;
import org.nbphpcouncil.modules.php.yii.util.YiiDocUtils;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@Messages("CTL_YiiRunActionAction=Run Action")
public final class YiiRunActionAction extends BaseAction {

    private static final long serialVersionUID = -379960760560724305L;
    private static YiiRunActionAction INSTANCE = new YiiRunActionAction();

    public static YiiRunActionAction getInstance() {
        return INSTANCE;
    }

    private YiiRunActionAction() {
    }

    @Override
    public void actionPerformed(PhpModule phpModule) {
        // called via shortcut
        if (!YiiUtils.isYii(phpModule)) {
            return;
        }

        // get current editor
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor == null) {
            return;
        }

        // get current file
        FileObject controller = YiiDocUtils.getFileObject(editor.getDocument());
        if (controller == null || !YiiUtils.isController(controller)) {
            return;
        }

        // get element for caret positon
        int caretPosition = editor.getCaretPosition();
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement element = editorSupport.getElement(controller, caretPosition);

        // get actionId, controllerId
        String actionId = getActionId(element);
        String controllerId = getControllerId(controller);
        if (StringUtils.isEmpty(actionId) || StringUtils.isEmpty(controllerId)) {
            return;
        }
        // TODO when more parameters exist
        // open browser
        openBrowser(phpModule, controllerId, actionId);
    }

    @Override
    protected String getFullName() {
        return Bundle.LBL_YiiAction(getPureName());
    }

    @Override
    protected String getPureName() {
        return Bundle.CTL_YiiRunActionAction();
    }

    /**
     * Get actionId.
     *
     * @param element
     * @return
     */
    private String getActionId(PhpBaseElement element) {
        String actionId = ""; //NOI18N
        if (element instanceof PhpClass.Method) {
            PhpClass.Method method = (PhpClass.Method) element;
            actionId = YiiUtils.getViewName(method);
        }
        return actionId;
    }

    /**
     * Get controllerId.
     *
     * @param controller
     * @return
     */
    private String getControllerId(FileObject controller) {
        return YiiUtils.getViewFolderName(controller.getName());
    }

    /**
     * Open Browser.
     *
     * @param phpModule
     * @param controllerId
     * @param actionId
     */
    private void openBrowser(PhpModule phpModule, String controllerId, String actionId) {
        // build url
        StringBuilder sb = new StringBuilder();
        PhpModuleProperties properties = phpModule.getProperties();
        String urlPath = properties.getUrl();
        sb.append(urlPath)
                .append("?r=") // NOI18N
                .append(controllerId)
                .append("/") // NOI18N
                .append(actionId);

        // open URL
        try {
            URL url = new URL(sb.toString());
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
