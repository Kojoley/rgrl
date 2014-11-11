package rgrl.grammar;

import com.intellij.lexer.FlexAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: SERPENT
 * Date: 03.11.13
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class SrgLexerAdapter extends FlexAdapter {
    public SrgLexerAdapter() {
        super(new SrgLexer());
    }
}
