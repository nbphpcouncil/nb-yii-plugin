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
package org.nbphpcouncil.modules.php.yii.editor.completion;

import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.nbphpcouncil.modules.php.yii.editor.completion.methods.Method;
import org.nbphpcouncil.modules.php.yii.editor.completion.methods.MethodFactory;
import org.nbphpcouncil.modules.php.yii.util.YiiDocUtils;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = CompletionProvider.class)
public class YiiFilePathCompletionProvider extends YiiCompletionProvider {

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component, PhpModule phpModule) {
        TokenSequence<PHPTokenId> ts = YiiDocUtils.getTokenSequence(component.getDocument());
        if (ts == null) {
            return null;
        }

        // get method name
        String methodName = getMethodName(ts, component.getCaretPosition());
        if (StringUtils.isEmpty(methodName)) {
            return null;
        }

        // create task
        FileObject currentFile = NbEditorUtilities.getFileObject(component.getDocument());
        Method method = MethodFactory.create(methodName, currentFile, phpModule);
        if (method == null) {
            return null;
        }
        return new AsyncCompletionTask(new AsyncCompletionQueryImpl(method), component);
    }

    private String getMethodName(TokenSequence<PHPTokenId> ts, int offset) {
        // move to caret position
        ts.move(offset);
        ts.moveNext();
        while (ts.movePrevious()) {
            Token<PHPTokenId> token = ts.token();
            PHPTokenId id = token.id();
            if (id == PHPTokenId.PHP_SEMICOLON) {
                break;
            }
            String text = token.text().toString();
            if (MethodFactory.METHODS.contains(text) && id == PHPTokenId.PHP_STRING) {
                return text;
            }
        }
        return null;
    }

    static class AsyncCompletionQueryImpl extends AsyncCompletionQuery {

        private final Method method;
        private int startOffset;
        private int removeLength;
        private String filter;

        public AsyncCompletionQueryImpl(Method method) {
            this.method = method;
        }

        @Override
        protected void query(CompletionResultSet resultSet, Document document, int caretOffset) {
            try {
                if (method == null) {
                    return;
                }
                TokenSequence<PHPTokenId> ts = YiiDocUtils.getTokenSequence(document);
                ts.move(caretOffset);
                ts.moveNext();
                Token<PHPTokenId> token = ts.token();

                // check whether target is string ('' or "")
                if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                    return;
                }

                // initialize startOffset, removeLength, filter
                init(ts, caretOffset);

                // add items
                for (String element : method.getElements(filter)) {
                    resultSet.addItem(method.createCompletionItem(element, startOffset, removeLength));
                }

            } finally {
                resultSet.finish();
            }
        }

        /**
         * Initialize
         *
         * @param ts token sequence
         * @param caretOffset caret offset
         */
        private void init(TokenSequence<PHPTokenId> ts, int caretOffset) {
            String caretInput = ts.token().text().toString();
            startOffset = ts.offset() + 1;
            removeLength = caretInput.length() - 2;
            if (removeLength < 0) {
                removeLength = 0;
            }
            int endIndex = caretOffset - startOffset + 1;
            if (endIndex < 1) {
                filter = ""; // NOI18N
            } else {
                filter = caretInput.substring(1, endIndex);
            }
        }
    }
}
