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

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.nbphpcouncil.modules.php.yii.YiiModule;
import org.nbphpcouncil.modules.php.yii.YiiModuleFactory;
import static org.nbphpcouncil.modules.php.yii.editor.YiiHyperlinkProviderExt.DEFAULT_OFFSET;
import org.nbphpcouncil.modules.php.yii.editor.navi.GoToDefaultItem;
import org.nbphpcouncil.modules.php.yii.editor.navi.GoToItem;
import org.nbphpcouncil.modules.php.yii.editor.navi.GoToPopup;
import org.nbphpcouncil.modules.php.yii.editor.navi.GoToTItem;
import org.nbphpcouncil.modules.php.yii.editor.navi.PopupUtil;
import org.nbphpcouncil.modules.php.yii.util.YiiDocUtils;
import org.nbphpcouncil.modules.php.yii.util.YiiPathAliasSupport;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.nbphpcouncil.modules.php.yii.util.YiiViewPathSupport;
import org.netbeans.api.editor.EditorRegistry;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.parsing.api.ParserManager;
import org.netbeans.modules.parsing.api.ResultIterator;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.api.UserTask;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.ClassElement;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.parser.api.Utils;
import org.netbeans.modules.php.editor.parser.astnodes.ArrayElement;
import org.netbeans.modules.php.editor.parser.astnodes.Expression;
import org.netbeans.modules.php.editor.parser.astnodes.Scalar;
import org.netbeans.modules.php.editor.parser.astnodes.visitors.DefaultVisitor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = HyperlinkProviderExt.class)
public class YiiGoToFileHyperlinkProvider extends YiiHyperlinkProviderExt {

    private static final Set<String> methods = new HashSet<String>();
    private static final String DEFAULT_MESSAGES_DIR_NAME = "messages"; // NOI18N
    private List<GoToItem> goToItems;
    private int paramCount;
    private static final Logger LOGGER = Logger.getLogger(YiiGoToFileHyperlinkProvider.class.getName());

    static {
        methods.add("widget"); // NOI18N
        methods.add("beginWidget"); // NOI18N
        methods.add("endWidget"); // NOI18N
        methods.add("createWidget"); // NOI18N
        methods.add("import"); // NOI18N
        methods.add("beginCache"); // NOI18N
        methods.add("beginContent"); // NOI18N
        methods.add("t"); // NOI18N
    }

    @Override
    public void performClickAction(Document doc, int offset, HyperlinkType type) {
        // open file
        if (goToItems.size() == 1) {
            GoToItem item = goToItems.get(0);
            UiUtils.open(item.getFileObject(), DEFAULT_OFFSET);
        }
        // i18n
        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        // show popup
        if (editor != null && !goToItems.isEmpty()) {
            try {
                Rectangle rectangle = editor.modelToView(offset);
                final Point point = new Point(rectangle.x, rectangle.y + rectangle.height);
                SwingUtilities.convertPointToScreen(point, editor);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        PopupUtil.showPopup(new GoToPopup(" Select LocaleID ", goToItems), " Select LocaleID ", point.x, point.y, true, 0);
                    }
                });
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
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

        // create go to items
        PhpModule phpModule = PhpModule.Factory.inferPhpModule();
        goToItems = createGoToItems(phpModule, doc, offset);
        if (!goToItems.isEmpty()) {
            return true;
        }

        return false;
    }

    @Override
    @NbBundle.Messages({
        "LBL.NotFoundFile=Not found file",
        "LBL.I18N.Message=If you click, go to list is displayed"
    })
    public String getTooltipText(Document doc, int offset, HyperlinkType type) {
        if (goToItems.size() == 1) {
            return goToItems.get(0).getFileObject().getPath();
        }
        if (!goToItems.isEmpty()) {
            return Bundle.LBL_I18N_Message();
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
        paramCount = 1;
        while (ts.movePrevious()) {
            Token<PHPTokenId> token = ts.token();
            String text = token.text().toString();
            PHPTokenId id = token.id();
            if (id == PHPTokenId.PHP_TOKEN && text.equals(",")) { // NOI18N
                // XXX this is not exact
                paramCount++;
                continue;
            }
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
     * Get class file if target is class name.
     *
     * @param phpModule PhpModule
     * @return FileObject for class if file exists, otherwise null.
     */
    private FileObject getClassFileObject(PhpModule phpModule, String target) {
        if (!StringUtils.isEmpty(target) && !target.contains(" ")) { // NOI18N
            ElementQuery.Index indexQuery = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(phpModule.getSourceDirectory()));
            Set<ClassElement> classElements = indexQuery.getClasses(NameKind.create(target, QuerySupport.Kind.EXACT));
            for (ClassElement element : classElements) {
                if (element.getName().equals(target)) {
                    return element.getFileObject();
                }
            }
        }
        return null;
    }

    /**
     * Create GoToItems.
     *
     * @param phpModule
     * @param doc
     * @param offset
     * @return GoToItem list
     */
    private List<GoToItem> createGoToItems(PhpModule phpModule, Document doc, int offset) {
        List<GoToItem> items = new ArrayList<GoToItem>();
        // check whether target is class name
        FileObject classFile = getClassFileObject(phpModule, target);
        if (classFile != null) {
            items.add(new GoToDefaultItem(classFile, DEFAULT_OFFSET));
            return items;
        }

        // get method name
        TokenSequence<PHPTokenId> ts = YiiDocUtils.getTokenSequence(doc);
        ts.move(offset);
        ts.moveNext();
        String methodName = getMethodName(ts);
        if (!methods.contains(methodName)) {
            return items;
        }

        // for i18n
        if (methodName.equals("t")) { // NOI18N
            return createGoToTItems(phpModule, ts, offset);
        }

        // for absolute view path
        FileObject file = null;
        if (YiiViewPathSupport.isAbsoluteViewPath(target)) {
            // for application's view path
            FileObject currentFile = NbEditorUtilities.getFileObject(doc);
            file = YiiViewPathSupport.getAbsoluteViewFile(target, currentFile);
        }

        // for path alias
        if (file == null) {
            file = YiiPathAliasSupport.getFileObject(phpModule, target);
        }
        if (file != null && file.isData()) {
            items.add(new GoToDefaultItem(file, DEFAULT_OFFSET));
        }
        return items;
    }

    /**
     * Create GoToTItems.
     *
     * @param phpModule
     * @return true if item list is not empty, otherwise false.
     */
    private List<GoToItem> createGoToTItems(PhpModule phpModule, TokenSequence<PHPTokenId> ts, int offset) {
        List<GoToItem> items = new ArrayList<GoToItem>();

        YiiModule yiiModule = YiiModuleFactory.create(phpModule);
        FileObject application = yiiModule.getApplication();
        if (application == null || paramCount > 2) {
            return items;
        }

        // move to offset of message for second parameter
        String message = ""; // NOI18N
        if (paramCount == 2) {
            String tFirstParam = getTFirstParam(ts, offset);
            if (tFirstParam == null) {
                return items;
            }
            message = target;
            target = tFirstParam;
        }

        // get messages directory
        FileObject messagesDirectory = null;
        String messagesDirectoryName = DEFAULT_MESSAGES_DIR_NAME;

        // check whether text contains class name
        String className = ""; // NOI18N
        if (target.contains(".")) { // NOI18N
            int dotIndex = target.indexOf("."); // NOI18N
            className = target.substring(0, dotIndex);
            String categoryName = target.substring(dotIndex + 1);
            target = categoryName;
            FileObject classFile = getClassFileObject(phpModule, className);
            if (classFile != null) {
                FileObject parent = classFile.getParent();
                if (parent != null && parent.isFolder()) {
                    messagesDirectory = parent.getFileObject(messagesDirectoryName);
                }
            }
        }

        if (className.isEmpty() && messagesDirectory == null) {
            messagesDirectory = yiiModule.getMessages();
        }
        if (messagesDirectory == null) {
            return items;
        }

        // sort
        FileObject[] children = messagesDirectory.getChildren();
        YiiUtils.sort(children);

        // add item
        for (FileObject child : children) {
            // create GoToItem
            int messageOffset = DEFAULT_OFFSET;
            final Set<Integer> mOffset = new HashSet<Integer>();
            final String messageKey = message;
            FileObject messageFile = child.getFileObject(target + ".php"); // NOI18N
            if (messageFile != null) {
                // click second parameter
                if (!StringUtils.isEmpty(messageKey)) {
                    // get offset for massage
                    try {
                        ParserManager.parse(Collections.singleton(Source.create(messageFile)), new UserTask() {
                            @Override
                            public void run(ResultIterator resultIterator) throws Exception {
                                ParserResult parseResult = (ParserResult) resultIterator.getParserResult();
                                if (parseResult == null) {
                                    return;
                                }
                                final MessageVisitor messageVisitor = new MessageVisitor(messageKey);
                                messageVisitor.scan(Utils.getRoot(parseResult));
                                mOffset.add(messageVisitor.getOffset());
                            }
                        });
                    } catch (ParseException ex) {
                        LOGGER.log(Level.WARNING, null, ex);
                    }
                }

                // set offset
                if (!mOffset.isEmpty()) {
                    for (Integer o : mOffset) {
                        messageOffset = o.intValue();
                        break;
                    }
                }
                items.add(new GoToTItem(messageFile, messageOffset, child.getName()));
            }
        }
        return items;
    }

    /**
     * Get first parameter for t method. It is valid for only second parameter.
     *
     * @param ts TokenSequence
     * @param offset current offset
     * @return first parameter for t method.
     */
    private String getTFirstParam(TokenSequence<PHPTokenId> ts, int offset) {
        if (paramCount == 2) {
            ts.move(offset);
            ts.moveNext();

            // search method
            while (ts.movePrevious()) {
                Token<PHPTokenId> token = ts.token();
                if (token.id() == PHPTokenId.PHP_STRING) {
                    break;
                }
            }

            // search first parameter
            while (ts.moveNext()) {
                Token<PHPTokenId> token = ts.token();
                PHPTokenId id = token.id();
                if (id == PHPTokenId.PHP_SEMICOLON) {
                    break;
                }
                if (id == PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                    String text = token.text().toString();
                    return text.substring(1, text.length() - 1);
                }
            }
        }
        return null;
    }

    //~ inner class
    private class MessageVisitor extends DefaultVisitor {

        private int offset;
        private String message;

        public MessageVisitor(String message) {
            this.message = message;
        }

        @Override
        public void visit(ArrayElement node) {
            super.visit(node);
            Expression key = node.getKey();
            if (key instanceof Scalar) {
                Scalar s = (Scalar) key;
                if (s.getScalarType() == Scalar.Type.STRING) {
                    String keyValue = s.getStringValue();
                    keyValue = keyValue.substring(1, keyValue.length() - 1);
                    if (keyValue.equals(message)) {
                        offset = node.getStartOffset();
                    }
                }
            }
        }

        public int getOffset() {
            return offset;
        }
    }
}
