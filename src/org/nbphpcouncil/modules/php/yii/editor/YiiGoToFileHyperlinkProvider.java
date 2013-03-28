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
package org.nbphpcouncil.modules.php.yii.editor;

import java.util.HashSet;
import java.util.Set;
import javax.swing.text.Document;
import org.nbphpcouncil.modules.php.yii.YiiModule;
import org.nbphpcouncil.modules.php.yii.YiiModuleFactory;
import org.nbphpcouncil.modules.php.yii.util.YiiDocUtils;
import org.nbphpcouncil.modules.php.yii.util.YiiPathAliasSupport;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = HyperlinkProviderExt.class)
public class YiiGoToFileHyperlinkProvider extends YiiHyperlinkProviderExt {

    private static final Set<String> methods = new HashSet<String>();
    private FileObject targetFile;

    static {
        methods.add("widget"); // NOI18N
        methods.add("beginWidget"); // NOI18N
        methods.add("endWidget"); // NOI18N
        methods.add("createWidget"); // NOI18N
        methods.add("import"); // NOI18N
        methods.add("beginCache"); // NOI18N
    }

    @Override
    public void performClickAction(Document doc, int offset, HyperlinkType type) {
        // open file
        if (targetFile != null) {
            UiUtils.open(targetFile, DEFAULT_OFFSET);
        }
    }

    @Override
    protected boolean verifyState(Document doc, int offset, HyperlinkType type) {
        // get TokenSequence
        TokenSequence<PHPTokenId> ts = YiiDocUtils.getTokenSequence(doc);
        if (ts == null) {
            return false;
        }

        // get current positon text
        target = getCurrentPositionString(ts, offset);
        if (target == null) {
            return false;
        }

        // set span
        targetStart = ts.offset() + 1;
        targetEnd = targetStart + target.length();

        // get method name
        String methodName = getMethodName(ts);
        if (methods.contains(methodName)) {
            // get FileObject
            PhpModule phpModule = PhpModule.inferPhpModule();
            targetFile = YiiPathAliasSupport.getFileObject(phpModule, target);
            if (targetFile == null) {
                targetFile = getDefaultFileObject(methodName, phpModule);
            }
            if (targetFile != null && targetFile.isData()) {
                return true;
            }
        }

        return false;
    }

    @Override
    @NbBundle.Messages("LBL_NotFoundFile=Not found file")
    public String getTooltipText(Document doc, int offset, HyperlinkType type) {
        if (targetFile != null) {
            return targetFile.getPath();
        }
        return Bundle.LBL_NotFoundFile();
    }

    /**
     * Get method name of current caret position.
     *
     * @param ts TokenSequence
     * @return method name.
     */
    private String getMethodName(TokenSequence<PHPTokenId> ts) {
        String name = null;
        while (ts.movePrevious()) {
            Token<PHPTokenId> token = ts.token();
            String text = token.text().toString();
            PHPTokenId id = token.id();
            if (id == PHPTokenId.PHP_SEMICOLON || id == PHPTokenId.PHP_FUNCTION || id == PHPTokenId.PHP_OPENTAG) {
                break;
            }
            if (id == PHPTokenId.PHP_STRING) {
                name = text;
                break;
            }
        }
        return name;
    }

    /**
     * Get default file.
     *
     * @param methodName method name
     * @param phpModule PhpModule
     * @return FileObject
     */
    private FileObject getDefaultFileObject(String methodName, PhpModule phpModule) {
        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        if (methodName.contains("widget") || methodName.contains("Widget")) { // NOI18N
            FileObject system = yiiModule.getSystem();
            if (system != null) {
                return system.getFileObject("web/widgets/" + target + ".php"); // NOI18N
            }
        }
        return null;
    }
}
