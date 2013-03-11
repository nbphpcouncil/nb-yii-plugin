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

import java.awt.Dialog;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.nbphpcouncil.modules.php.yii.ui.wizards.YiiRunActionPanel;
import org.nbphpcouncil.modules.php.yii.util.YiiDocUtils;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.php.api.editor.EditorSupport;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.CodeUtils;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.FormalParameter;
import org.netbeans.modules.php.editor.parser.astnodes.FunctionDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.MethodDeclaration;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@Messages("LBL_YiiRunActionAction=Run Action")
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

        Document document = editor.getDocument();
        // get current file
        FileObject controller = YiiDocUtils.getFileObject(document);
        if (controller == null || !YiiUtils.isController(controller)) {
            return;
        }

        // get element for caret positon
        int caretPosition = editor.getCaretPosition();
        EditorSupport editorSupport = Lookup.getDefault().lookup(EditorSupport.class);
        PhpBaseElement element = editorSupport.getElement(controller, caretPosition);

        // get actionId, controllerId
        final String actionId = getActionId(element);
        final String controllerId = getControllerId(controller);
        if (StringUtils.isEmpty(actionId) || StringUtils.isEmpty(controllerId)) {
            return;
        }

        // when more parameters exist
        final String methodName = getMethodName(element);
        final List<FormalParameter> params = new ArrayList<FormalParameter>();
        try {
            ParserManager.parse(Collections.singleton(Source.create(document)), new UserTask() {
                @Override
                public void run(ResultIterator resultIterator) throws Exception {
                    if (resultIterator == null) {
                        return;
                    }
                    ParserResult parserResult = (ParserResult) resultIterator.getParserResult();
                    ControllerMethodVisitor visitor = new ControllerMethodVisitor(methodName);
                    visitor.scan(Utils.getRoot(parserResult));
                    params.addAll(visitor.getParams());
                }
            });
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }

        final PhpModule pm = phpModule;
        runAction(params, pm, controllerId, actionId);

    }

    @Override
    protected String getFullName() {
        return Bundle.LBL_YiiAction(getPureName());
    }

    @Override
    protected String getPureName() {
        return Bundle.LBL_YiiRunActionAction();
    }

    private String getMethodName(PhpBaseElement element) {
        String methodName = ""; // NOI18N
        if (element instanceof PhpClass.Method) {
            PhpClass.Method method = (PhpClass.Method) element;
            methodName = method.getName();
        }
        return methodName;
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
        openBrowser(phpModule, controllerId, actionId, new HashMap<String, String>());
    }

    /**
     * Open Browser.
     *
     * @param phpModule
     * @param controllerId
     * @param actionId
     * @param getRequests
     */
    private void openBrowser(PhpModule phpModule, String controllerId, String actionId, Map<String, String> getRequests) {
        // build url
        StringBuilder sb = new StringBuilder();
        PhpModuleProperties properties = phpModule.getProperties();
        String urlPath = properties.getUrl();
        sb.append(urlPath)
                .append("?r=") // NOI18N
                .append(controllerId)
                .append("/") // NOI18N
                .append(actionId);

        // add GET requests
        for (String key : getRequests.keySet()) {
            String value = getRequests.get(key);
            sb.append("&") // NOI18N
                    .append(key)
                    .append("=") // NOI18N
                    .append(value);
        }

        // open URL
        try {
            URL url = new URL(sb.toString());
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void runAction(final List<FormalParameter> params, final PhpModule pm, final String controllerId, final String actionId) {
        if (params.isEmpty()) {
            openBrowser(pm, controllerId, actionId);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // create dialog
                    YiiRunActionPanel panel = new YiiRunActionPanel(params);
                    DialogDescriptor descriptor = new DialogDescriptor(panel, Bundle.LBL_YiiRunActionAction(), true, null);
                    Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);

                    dialog.pack();
                    dialog.validate();
                    dialog.setVisible(true);
                    // open browser
                    if (descriptor.getValue() == DialogDescriptor.OK_OPTION) {
                        Map<String, String> requests = panel.getGetRequest();
                        openBrowser(pm, controllerId, actionId, requests);
                    }
                }
            });
        }
    }

    private static class ControllerMethodVisitor extends DefaultVisitor {

        private String targetMethodName;
        private final List<FormalParameter> params = new ArrayList<FormalParameter>();

        public ControllerMethodVisitor(String targetMethodName) {
            this.targetMethodName = targetMethodName;
        }

        @Override
        public void visit(MethodDeclaration node) {
            super.visit(node);
            String methodName = CodeUtils.extractMethodName(node);
            if (!methodName.equals(targetMethodName)) {
                return;
            }
            FunctionDeclaration function = node.getFunction();
            List<FormalParameter> formalParameters = function.getFormalParameters();
            params.addAll(formalParameters);
        }

        public List<FormalParameter> getParams() {
            return params;
        }
    }
}
