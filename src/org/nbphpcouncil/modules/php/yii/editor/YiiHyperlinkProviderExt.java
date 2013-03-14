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

import java.util.EnumSet;
import java.util.Set;
import javax.swing.text.Document;
import org.nbphpcouncil.modules.php.yii.util.YiiDocUtils;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public abstract class YiiHyperlinkProviderExt implements HyperlinkProviderExt {

    protected static final int DEFAULT_OFFSET = 0;
    protected String target = ""; // NOI18N
    protected int targetStart;
    protected int targetEnd;

    @Override
    public Set<HyperlinkType> getSupportedHyperlinkTypes() {
        return EnumSet.of(HyperlinkType.GO_TO_DECLARATION);
    }

    @Override
    public boolean isHyperlinkPoint(Document doc, int offset, HyperlinkType type) {
        if (!isYii(doc)) {
            return false;
        }
        return verifyState(doc, offset, type);
    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int offset, HyperlinkType type) {
        return new int[]{targetStart, targetEnd};
    }

    @Override
    public abstract void performClickAction(Document doc, int offset, HyperlinkType type);

    @Override
    public String getTooltipText(Document doc, int offset, HyperlinkType type) {
        return null;
    }

    /**
     * Verify whether current position is Hyperlink point.
     *
     * @param doc
     * @param offset
     * @param type
     * @return true if current position is hyperlink, otherwise false.
     */
    protected abstract boolean verifyState(Document doc, int offset, HyperlinkType type);

    /**
     * Check whether current document is Yii.
     *
     * @param doc
     * @return true if document is Yii, otherwise false.
     */
    private boolean isYii(Document doc) {
        FileObject fileObject = YiiDocUtils.getFileObject(doc);
        if (fileObject == null) {
            return false;
        }
        PhpModule phpModule = PhpModule.forFileObject(fileObject);
        return YiiUtils.isYii(phpModule);
    }

    /**
     * Get string for current position.
     *
     * @param ts
     * @param offset
     * @return string
     */
    public String getCurrentPositionString(TokenSequence<PHPTokenId> ts, int offset) {
        ts.move(offset);
        ts.moveNext();
        Token<PHPTokenId> token = ts.token();
        if (token.id() == PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
            String text = token.text().toString();
            int length = text.length();
            if (length > 2) {
                text = text.substring(1, text.length() - 1);
            } else {
                text = ""; // NOI18N
            }
            return text;
        }
        return null;
    }
}
