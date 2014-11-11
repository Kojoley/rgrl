package rgrl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import rgrl.grammar.SrgLexer;
import rgrl.grammar.parser.SrgParser;
import rgrl.grammar.psi.SrgFile;
import rgrl.grammar.psi.SrgTypes;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public class SrgParserDefinition implements ParserDefinition {
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(SrgTypes.COMMENT);

    public static final IFileElementType FILE = new IFileElementType(Language.<SrgLanguage>findInstance(SrgLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new FlexAdapter(new SrgLexer((Reader) null));
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new SrgParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new SrgFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        //private static
        //final Logger LOG = Logger.getInstance("#" + SrgParserDefinition.class.getName());
        //LOG.warn("ZZZ: " + left.toString() + " @ " + right.toString());
        //LOG.warn("QQQ: " + left.getPsi().toString() + " @ " + right.getPsi().toString());

        final TokenSet recordTypes = TokenSet.create(SrgTypes.RECORD_TYPE_FIELD, SrgTypes.RECORD_TYPE_METHOD,
                                                     SrgTypes.RECORD_TYPE_CLASS, SrgTypes.RECORD_TYPE_PACKAGE);

        // Before new record must be a new line
        if (recordTypes.contains(right.getElementType()))
            return SpaceRequirements.MUST_LINE_BREAK;

        // After record type token must be space
        if (recordTypes.contains(left.getElementType()))
            return SpaceRequirements.MUST;

        // FIXME: add other token types

        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return SrgTypes.Factory.createElement(node);
    }
}