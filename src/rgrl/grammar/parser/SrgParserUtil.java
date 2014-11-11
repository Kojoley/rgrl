package rgrl.grammar.parser;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.lang.WhitespacesAndCommentsBinder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;
import rgrl.RgException;
import rgrl.RgTools;
import rgrl.SrgParserDefinition;
import rgrl.grammar.psi.SrgTypes;

import java.util.List;

public class SrgParserUtil extends GeneratedParserUtilBase {
    /*public static boolean is_new_line(PsiBuilder builder, int level) {
        if (builder.eof()) return true;
        addVariant(builder, "<new-line>");
        for (int i=-1; ; i--) {
            IElementType type = builder.rawLookup(i);
            if (type == TokenType.WHITE_SPACE) continue;
            if (type == JFlexTypes.FLEX_LINE_COMMENT || type == JFlexTypes.FLEX_BLOCK_COMMENT) continue;
            return type == JFlexTypes.FLEX_NEWLINE || type == null;
        }
    }*/

    public static boolean is_java_identifier(PsiBuilder builder, int level) {
        IElementType tokenType = builder.getTokenType();
        if (tokenType != null) PluginManager.getLogger().warn("is_java_identifier: " + tokenType.toString() + " = '"  + builder.getTokenText()+ "'");
        else                   PluginManager.getLogger().warn("is_java_identifier: ? = '"  + builder.getTokenText() + "'");
        //return tokenType != null && isJavaIdentifier(tokenType.toString());

        if (isJavaIdentifier(builder.getTokenText())) {
            builder.advanceLexer();
            return true;
        }

        PluginManager.getLogger().warn("is_java_identifier@false: " + builder.getTokenText());

        return false;
    }

    public static boolean isJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0)))
            return false;

        /*for (char c : s.toCharArray())
            if (!Character.isJavaIdentifierPart(c))
                return false;*/

        for (int i = 1, l = s.length(); i < l; ++i)
            if (!Character.isJavaIdentifierPart(s.charAt(i)))
                return false;

        return true;
    }

    public static boolean is_java_fqn(PsiBuilder builder, int level) {
        IElementType tokenType = builder.getTokenType();
        if (tokenType != null) PluginManager.getLogger().warn("is_java_fqn: " + tokenType.toString() + " = '"  + builder.getTokenText()+ "'");
        else                   PluginManager.getLogger().warn("is_java_fqn: ? = '"  + builder.getTokenText() + "'");

        if (isJavaFqn(builder.getTokenText())) {
            //builder.advanceLexer();
            //return true;

            //IElementType tokenType = builder.getTokenType();
            return tokenType != null && consumeToken(builder, tokenType);
        }

        PluginManager.getLogger().warn("is_java_fqn@false: " + builder.getTokenText());

        return false;
    }

    public static boolean isJavaFqn(String s) {
        if (s.length() == 0)
            return false;
        else if (s.length() == 1 && s.charAt(0) == '.')
            return true;

        String[] names = s.split("(\\.|\\/)");

        //StringBuilder sb = new StringBuilder("");
        //for (String name : names) sb.append(name+";");
        //PluginManager.getLogger().warn("isJavaFqn: " + sb.toString());

        for (String name : names)
            if (!isJavaIdentifier(name))
                return false;

        return true;
    }

    public static boolean is_jni_type(PsiBuilder builder, int level) {
        IElementType tokenType = builder.getTokenType();
        if (tokenType != null) PluginManager.getLogger().warn("is_jni_type: " + tokenType.toString() + " = '"  + builder.getTokenText()+ "'");
        else                   PluginManager.getLogger().warn("is_jni_type: ? = '"  + builder.getTokenText()+ "'");
        //return tokenType != null && isJniType(tokenType.toString());

        if (isJniType(builder.getTokenText())) {
            //builder.advanceLexer();
            //return true;

            return tokenType != null && consumeToken(builder, tokenType);
        }

        PluginManager.getLogger().warn("is_jni_type@false: " + builder.getTokenText());

        return false;
    }

    public static boolean isJniType(String s) {
        switch (s.charAt(0)) {
            case 'Z':
            case 'C':
            case 'B':
            case 'S':
            case 'I':
            case 'F':
            case 'J':
            case 'D':
            case 'V':
                return s.length() == 1;
            case '[':
                return isJniType(s.substring(1));
            case 'L':
                int i = s.length() - 1;
                return s.charAt(i) == ';' && isJavaFqn(s.substring(1, i));
            default:
                return false;
        }
    }

    public static boolean is_method_descriptor(PsiBuilder builder, int level) {
        IElementType tokenType = builder.getTokenType();
        if (tokenType != null) PluginManager.getLogger().warn("is_method_descriptor: " + tokenType.toString() + " = '"  + builder.getTokenText()+ "'");
        else                   PluginManager.getLogger().warn("is_method_descriptor: ? = '"  + builder.getTokenText()+ "'");
        if (tokenType != null && tokenType != SrgTypes.METHOD_DESCRIPTOR)
            return false;

        try {
            RgTools.checkMethodDescriptor(builder.getTokenText());
            PluginManager.getLogger().warn("is_method_descriptor@true");

            builder.advanceLexer();
            return true;

            //return tokenType != null && consumeToken(builder, tokenType);
        }
        catch (RgException e) {
            //builder.error("Error at parsing method descriptor: "+ e.getMessage());
            //PsiBuilder.Marker marker = builder.mark();
            //marker.error("Error: "+ e.getMessage());
            builder.error("Parsing Error: "+ e.getMessage());
            PluginManager.getLogger().warn("is_method_descriptor@false: " + e.getMessage());
            return false;
        }
    }

    /*public static boolean reference_match(PsiBuilder builder, int level, boolean zoi) {
        if (!nextTokenIsFast(builder, SrgTypes.IDENTIFIER))
            return zoi;

        builder.setDebugMode(true);

        PsiBuilder.Marker marker = builder.mark();
        while (true) {
            PsiBuilder.Marker markerLast = marker.precede();
            //PsiBuilder.Marker markerLast = builder.mark();
            if (consumeToken(builder, SrgTypes.IDENTIFIER) && consumeToken(builder, SrgTypes.SLASH2)) {
                markerLast.done(SrgTypes.REFERENCE_ELEMENT);
                //markerLast.drop();
                continue;
            }
            markerLast.rollbackTo();
            marker.done(SrgTypes.REFERENCE_ELEMENT);
            builder.setDebugMode(false);
            return true;
        }
    }*/

    /*public static boolean reference_match(PsiBuilder builder, int level) {
        //if (!nextTokenIsFast(builder, SrgTypes.IDENTIFIER))
        if (builder.getTokenType() != SrgTypes.IDENTIFIER)
            return false;

        builder.setDebugMode(true);

        PsiBuilder.Marker marker = builder.mark();

        while (true) {
            PsiBuilder.Marker markerLast = marker.precede();

            if (consumeToken(builder, SrgTypes.IDENTIFIER)) {
                markerLast.done(SrgTypes.REFERENCE_ELEMENT);
                if (nextTokenIsFast(builder, SrgTypes.SLASH2) && consumeTokenFast(builder, SrgTypes.SLASH2))
                    continue;
            } else
                markerLast.rollbackTo();

            marker.done(SrgTypes.REFERENCE_ELEMENT);
            builder.setDebugMode(false);
            return true;
        }
    }*/

    public static boolean rawNextTokenIsFast(PsiBuilder builder, IElementType token) {
        return nextTokenIsFast(builder, token) && builder.rawLookup(-1) != com.intellij.psi.TokenType.WHITE_SPACE;
    }

    public static boolean reference_match(final PsiBuilder builder, int level) {
        if (!nextTokenIsFast(builder, SrgTypes.IDENTIFIER)) {
            //builder.error("expected SrgTypes.IDENTIFIER");
            return false;
        }

        /*boolean isWhitespaceSkipped = false;
        final Boolean qwe = false;
        builder.setWhitespaceSkippedCallback(new WhitespaceSkippedCallback() {
            @Override
            final public void onSkip(IElementType iElementType, int i, int j) {
                //isWhitespaceSkipped = true;
                PluginManager.getLogger().warn("reference_match(WhitespaceSkippedCallback): " + iElementType + " ("+i+","+j+")");
                builder.error("qazwsx");
                qwe = true;
            }
        });*/

        //PluginManager.getLogger().warn("reference_match: >>> " + level);
        //builder.setDebugMode(true);
        PsiBuilder.Marker marker = builder.mark();
        //enterErrorRecordingSection(builder, level, _SECTION_GENERAL_, "<reference element>");
        //PsiBuilder.Marker marker_0 = builder.mark(); // workaround: final slash
        //IElementType futureToken = builder.getTokenType(); // workaround: whitespace between slash and identifier
        do {
            //if (futureToken != SrgTypes.IDENTIFIER) break; futureToken = builder.rawLookup(1);
            if (/*nextTokenIsFast(builder, SrgTypes.IDENTIFIER) &&*/ consumeToken(builder, SrgTypes.IDENTIFIER)) {
                //marker_0.drop();

                marker.done(SrgTypes.REFERENCE_ELEMENT);
                marker = marker.precede();

                //marker_0 = builder.mark();
            }
            else {
                return false;
            }
            //if (futureToken != SrgTypes.SLASH2) break; futureToken = builder.rawLookup(1);
        } while (consumeTokenFast(builder, SrgTypes.SLASH2));
        //marker_0.rollbackTo();
        marker.drop();
        //builder.setDebugMode(false);
        //PluginManager.getLogger().warn("reference_match: <<< " + level);
        //result = exitErrorRecordingSection(builder, level, result, pinned | false, _SECTION_GENERAL_, null);
        return true;
    }
}

