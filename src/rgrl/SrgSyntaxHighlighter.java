package rgrl;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import gnu.trove.THashMap;
import rgrl.grammar.SrgLexer;
import rgrl.grammar.psi.SrgTypes;

import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.Map;

public class SrgSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final Map<IElementType, TextAttributesKey> keys1;
    private static final Map<IElementType, TextAttributesKey> keys2;

    public static final TextAttributesKey SRG_KEYWORD = TextAttributesKey.createTextAttributesKey(
            "SRG.KEY",
            DefaultLanguageHighlighterColors.KEYWORD
    );

    public static final TextAttributesKey SRG_STRING = TextAttributesKey.createTextAttributesKey(
            "SRG.VALUE",
            DefaultLanguageHighlighterColors.STRING
    );

    public static final TextAttributesKey SRG_COMMENT = TextAttributesKey.createTextAttributesKey(
            "SRG.LINE_COMMENT",
            DefaultLanguageHighlighterColors.LINE_COMMENT
    );

    static {
        keys1 = new THashMap<IElementType, TextAttributesKey>();
        keys2 = new THashMap<IElementType, TextAttributesKey>();

        keys1.put(SrgTypes.RECORD_TYPE_CLASS, SRG_KEYWORD);
        keys1.put(SrgTypes.RECORD_TYPE_FIELD, SRG_KEYWORD);
        keys1.put(SrgTypes.RECORD_TYPE_METHOD, SRG_KEYWORD);
        keys1.put(SrgTypes.RECORD_TYPE_PACKAGE, SRG_KEYWORD);

        keys1.put(SrgTypes.PACKAGE_NAME, SRG_STRING);
        keys1.put(SrgTypes.CLASS_NAME, SRG_STRING);
        keys1.put(SrgTypes.FIELD_NAME, SRG_STRING);
        keys1.put(SrgTypes.METHOD_SPEC, SRG_STRING);

        keys1.put(SrgTypes.COMMENT, SRG_COMMENT);
    }

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new SrgLexer((Reader) null));
    }

    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(keys1.get(tokenType), keys2.get(tokenType));
    }
}