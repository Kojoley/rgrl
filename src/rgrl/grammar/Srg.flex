package rgrl.grammar;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import rgrl.grammar.psi.SrgTypes;

/*
  import java.util.Stack;
  Stack<Integer> zzStateStack = new Stack<Integer>();

  public final int yypushstate() {
    zzStateStack.push(zzLexicalState);
    return zzLexicalState;
  }

  public final int yypopstate() {
    zzLexicalState = zzStateStack.pop();
    return zzLexicalState;
  }
*/
%%

%{
  public SrgLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class SrgLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

%state METHOD_DESCRIPTOR_START
%state JAVA_TYPE_CLASS_START

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

COMMENT=#.*
IDENTIFIER=[:jletter:][:jletterdigit:]*
//METHOD_DESCRIPTOR=\([^)]*\)[^\ \t\f\r\n]+

%%

<YYINITIAL> {
  {WHITE_SPACE}              { return com.intellij.psi.TokenType.WHITE_SPACE; }

  {COMMENT}                  { return SrgTypes.COMMENT; }

  "PK:"                      { return SrgTypes.RECORD_TYPE_PACKAGE; }
  "CL:"                      { return SrgTypes.RECORD_TYPE_CLASS; }
  "MD:"                      { return SrgTypes.RECORD_TYPE_METHOD; }
  "FD:"                      { return SrgTypes.RECORD_TYPE_FIELD; }

  "."                        { return SrgTypes.DOT; }

  //{METHOD_DESCRIPTOR}        { return SrgTypes.METHOD_DESCRIPTOR; }
  //"("                        { yybegin(METHOD_DESCRIPTOR_START); yypushback(1);/*return SrgTypes.BRACKET_OPEN;*/ }
  "("                        { yybegin(METHOD_DESCRIPTOR_START); return SrgTypes.BRACKET_OPEN; }

  //[^]                        { yybegin(FQN_START); yypushback(1); }

  "/"                        { return SrgTypes.SLASH2; }
  {IDENTIFIER}               { return SrgTypes.IDENTIFIER; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<METHOD_DESCRIPTOR_START> {
  {WHITE_SPACE}              { yybegin(YYINITIAL); return com.intellij.psi.TokenType.WHITE_SPACE; }
  //[.]+                       { return SrgTypes.METHOD_DESCRIPTOR; }

  //L[^;]+;                    { return SrgTypes.JAVA_TYPE_CLASS; }
  "L"                        { yypushstate(); yybegin(JAVA_TYPE_CLASS_START); return SrgTypes.JAVA_TYPE_CLASS; }

  ")"                        { return SrgTypes.BRACKET_CLOSE; }
  "Z"                        { return SrgTypes.JAVA_TYPE_BOOLEAN; }
  "C"                        { return SrgTypes.JAVA_TYPE_CHAR; }
  "B"                        { return SrgTypes.JAVA_TYPE_BYTE; }
  "S"                        { return SrgTypes.JAVA_TYPE_SHORT; }
  "I"                        { return SrgTypes.JAVA_TYPE_INTEGER; }
  "F"                        { return SrgTypes.JAVA_TYPE_FLOAT; }
  "J"                        { return SrgTypes.JAVA_TYPE_LONG; }
  "D"                        { return SrgTypes.JAVA_TYPE_DOUBLE; }
  "V"                        { return SrgTypes.JAVA_TYPE_VOID; }
  "["                        { return SrgTypes.JAVA_TYPE_ARRAY; }

  //{IDENTIFIER}               { return SrgTypes.IDENTIFIER; }
  //"/"                        { return SrgTypes.SLASH2; }
  //";"                        { return SrgTypes.SEMICOLON; }

  [^]                        { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<JAVA_TYPE_CLASS_START> {
  //{WHITE_SPACE}              { return com.intellij.psi.TokenType.BAD_CHARACTER; }

  "/"                        { return SrgTypes.SLASH2; }
  {IDENTIFIER}               { return SrgTypes.IDENTIFIER; }

  ";"                        { yypopstate(); return SrgTypes.SEMICOLON; }

  [^]                        { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

