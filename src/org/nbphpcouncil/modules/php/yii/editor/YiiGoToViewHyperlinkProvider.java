/*
 * TODO add license
 */
package org.nbphpcouncil.modules.php.yii.editor;

import javax.swing.text.Document;
import org.nbphpcouncil.modules.php.yii.util.YiiUtils;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProvider;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = "text/x-php5", service = HyperlinkProvider.class)
public class YiiGoToViewHyperlinkProvider implements HyperlinkProvider {

    private static final int DEFAULT_OFFSET = 0;
    private FileObject view = null;
    private String target = ""; // NOI18N
    private int targetStart;
    private int targetEnd;

    @Override
    public boolean isHyperlinkPoint(Document doc, int offset) {
        return verifyState(doc, offset);
    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int offset) {
        if (view != null) {
            return new int[]{targetStart, targetEnd};
        }
        return null;
    }

    @Override
    public void performClickAction(Document doc, int offset) {
        // Open view file
        if (view != null) {
            UiUtils.open(view, DEFAULT_OFFSET);
        }
    }

    /**
     * Verify whether caret position is on view file name string.
     *
     * @param doc
     * @param offset
     * @return true if view file exsits, otherwise false.
     */
    private boolean verifyState(Document doc, int offset) {
        // get FileObject
        Source source = Source.create(doc);
        FileObject targetFile = source.getFileObject();
        if (!YiiUtils.isController(targetFile)) {
            return false;
        }

        TokenHierarchy hierarchy = TokenHierarchy.get(doc);
        TokenSequence<PHPTokenId> ts = hierarchy.tokenSequence(PHPTokenId.language());
        if (ts == null) {
            return false;
        }
        ts.move(offset);
        ts.moveNext();
        int newOffset = ts.offset();

        Token<PHPTokenId> token = ts.token();
        setTarget(token);

        if (target.isEmpty() || !isRenderMethod(ts)) {
            return false;
        }
        // set view file
        view = YiiUtils.getView(targetFile, target);
        if (view != null) {
            targetStart = newOffset + 1;
            targetEnd = targetStart + target.length();
            return true;
        }

        return false;
    }

    /**
     * Verify whether method name is "render".
     *
     * @param ts TokenSequence
     * @return true if render method, otherwise false
     */
    private boolean isRenderMethod(TokenSequence<PHPTokenId> ts) {
        ts.movePrevious();
        ts.movePrevious();
        Token<PHPTokenId> render = ts.token();
        if (render.text().toString().equals("render")) { // NOI18N
            return true;
        }
        return false;
    }

    /**
     * Set target string. If token is string on carret position, set stirng to
     * target, otherwise set empty string to it.
     *
     * @param token
     */
    private void setTarget(Token<PHPTokenId> token) {
        target = token.text().toString();
        PHPTokenId id = token.id();
        if (id == PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
            if (target.length() > 2) {
                target = target.substring(1, target.length() - 1);
            } else {
                target = ""; // NOI18N
            }
        } else {
            target = ""; // NOI18N
        }
    }
}
