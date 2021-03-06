/* The following code was generated by JFlex 1.4.3 on 01.05.16 21:11 */

package com.aopphp.go.parser;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static com.aopphp.go.psi.PointcutTypes.*;


/**
 * This class is a scanner generated by
 * <a href="http://www.jflex.de/">JFlex</a> 1.4.3
 * on 01.05.16 21:11 from the specification file
 * <tt>/Users/lisachenko/Work/idea-go-aop-plugin/src/com/aopphp/go/parser/PointcutLexer.flex</tt>
 */
public class PointcutLexer implements FlexLexer {
  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0, 0
  };

  /**
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED =
    "\11\0\1\1\1\2\1\0\1\1\1\1\22\0\1\1\1\17\2\0"+
    "\1\22\1\0\1\20\1\0\1\10\1\11\1\15\1\21\1\0\1\12"+
    "\1\0\1\3\12\5\1\14\3\0\1\13\1\0\1\7\10\4\1\46"+
    "\21\4\1\0\1\6\2\0\1\4\1\0\1\27\1\42\1\30\1\43"+
    "\1\31\1\41\1\4\1\24\1\25\2\4\1\37\1\45\1\35\1\34"+
    "\1\50\1\4\1\47\1\26\1\23\1\33\1\51\1\36\1\32\1\44"+
    "\1\40\1\0\1\16\2\0\201\4\uff00\0";

  /**
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\1\1\3\1\4\1\5\1\6"+
    "\1\7\2\1\1\10\1\11\1\12\1\1\1\13\1\1"+
    "\12\3\1\14\1\15\1\16\1\17\1\20\1\21\1\0"+
    "\13\3\1\0\14\3\1\0\14\3\1\22\6\3\1\23"+
    "\7\3\1\24\2\3\1\25\2\3\1\26\6\3\1\27"+
    "\1\3\1\30\12\3\1\31\1\3\1\32\2\3\1\33"+
    "\12\3\1\34\1\3\1\35\5\3\1\36";

  private static int [] zzUnpackAction() {
    int [] result = new int[137];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\52\0\124\0\176\0\250\0\52\0\52\0\52"+
    "\0\52\0\322\0\374\0\u0126\0\u0150\0\52\0\u017a\0\52"+
    "\0\u01a4\0\u01ce\0\u01f8\0\u0222\0\u024c\0\u0276\0\u02a0\0\u02ca"+
    "\0\u02f4\0\u031e\0\u0348\0\u0372\0\52\0\52\0\52\0\52"+
    "\0\52\0\u039c\0\u03c6\0\u03f0\0\u041a\0\u0444\0\u046e\0\u0498"+
    "\0\u04c2\0\u04ec\0\u0516\0\u0540\0\u056a\0\u0594\0\u05be\0\u05e8"+
    "\0\u0612\0\u063c\0\u0666\0\u0690\0\u06ba\0\u06e4\0\u070e\0\u0738"+
    "\0\u0762\0\u078c\0\u07b6\0\u07e0\0\u080a\0\u0834\0\u085e\0\u0888"+
    "\0\u08b2\0\u08dc\0\u0906\0\u0930\0\u095a\0\u0984\0\u09ae\0\52"+
    "\0\u09d8\0\u0a02\0\u0a2c\0\u0a56\0\u0a80\0\u0aaa\0\250\0\u0ad4"+
    "\0\u0afe\0\u0b28\0\u0b52\0\u0b7c\0\u0ba6\0\u0bd0\0\250\0\u0bfa"+
    "\0\u0c24\0\250\0\u0c4e\0\u0c78\0\250\0\u0ca2\0\u0ccc\0\u0cf6"+
    "\0\u0d20\0\u0d4a\0\u0d74\0\250\0\u0d9e\0\250\0\u0dc8\0\u0df2"+
    "\0\u0e1c\0\u0e46\0\u0e70\0\u0e9a\0\u0ec4\0\u0eee\0\u0f18\0\u0f42"+
    "\0\250\0\u0f6c\0\250\0\u0f96\0\u0fc0\0\250\0\u0fea\0\u1014"+
    "\0\u103e\0\u1068\0\u1092\0\u10bc\0\u10e6\0\u1110\0\u113a\0\u1164"+
    "\0\250\0\u118e\0\250\0\u11b8\0\u11e2\0\u120c\0\u1236\0\u1260"+
    "\0\250";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[137];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\2\3\1\4\1\5\1\2\1\6\1\7\1\10"+
    "\1\11\1\12\1\2\1\13\1\14\1\15\1\16\1\17"+
    "\1\20\1\21\2\5\1\22\1\23\1\24\1\25\1\26"+
    "\4\5\1\27\2\5\1\30\1\5\1\31\1\5\1\32"+
    "\2\5\1\33\1\5\53\0\2\3\52\0\1\34\52\0"+
    "\2\5\15\0\27\5\13\0\1\35\52\0\1\36\52\0"+
    "\1\37\52\0\1\40\53\0\1\41\54\0\1\42\32\0"+
    "\2\5\15\0\12\5\1\43\14\5\4\0\2\5\15\0"+
    "\1\44\26\5\4\0\2\5\15\0\5\5\1\45\21\5"+
    "\4\0\2\5\15\0\16\5\1\46\10\5\4\0\2\5"+
    "\15\0\7\5\1\47\17\5\4\0\2\5\15\0\2\5"+
    "\1\50\24\5\4\0\2\5\15\0\2\5\1\51\24\5"+
    "\4\0\2\5\15\0\21\5\1\52\5\5\4\0\2\5"+
    "\15\0\4\5\1\53\22\5\4\0\2\5\15\0\10\5"+
    "\1\54\13\5\1\55\2\5\2\34\1\0\47\34\24\0"+
    "\1\56\31\0\2\5\15\0\2\5\1\57\24\5\4\0"+
    "\2\5\15\0\4\5\1\60\22\5\4\0\2\5\15\0"+
    "\5\5\1\61\21\5\4\0\2\5\15\0\14\5\1\62"+
    "\12\5\4\0\2\5\15\0\6\5\1\63\20\5\4\0"+
    "\2\5\15\0\1\64\26\5\4\0\2\5\15\0\12\5"+
    "\1\65\14\5\4\0\2\5\15\0\12\5\1\66\14\5"+
    "\4\0\2\5\15\0\1\67\26\5\4\0\2\5\15\0"+
    "\17\5\1\70\7\5\4\0\2\5\15\0\2\5\1\71"+
    "\6\5\1\72\15\5\25\0\1\73\30\0\2\5\15\0"+
    "\1\74\26\5\4\0\2\5\15\0\1\75\26\5\4\0"+
    "\2\5\15\0\6\5\1\76\20\5\4\0\2\5\15\0"+
    "\11\5\1\77\15\5\4\0\2\5\15\0\5\5\1\100"+
    "\21\5\4\0\2\5\15\0\1\5\1\101\25\5\4\0"+
    "\2\5\15\0\4\5\1\102\22\5\4\0\2\5\15\0"+
    "\4\5\1\103\22\5\4\0\2\5\15\0\5\5\1\104"+
    "\21\5\4\0\2\5\15\0\14\5\1\105\12\5\4\0"+
    "\2\5\15\0\26\5\1\106\4\0\2\5\15\0\1\107"+
    "\26\5\26\0\1\110\27\0\2\5\15\0\2\5\1\111"+
    "\24\5\4\0\2\5\15\0\2\5\1\112\24\5\4\0"+
    "\2\5\15\0\3\5\1\113\23\5\4\0\2\5\15\0"+
    "\13\5\1\114\13\5\4\0\2\5\15\0\10\5\1\115"+
    "\16\5\4\0\2\5\15\0\2\5\1\116\24\5\4\0"+
    "\2\5\15\0\14\5\1\117\12\5\4\0\2\5\15\0"+
    "\22\5\1\120\4\5\4\0\2\5\15\0\1\5\1\121"+
    "\25\5\4\0\2\5\15\0\2\5\1\122\24\5\4\0"+
    "\2\5\15\0\4\5\1\123\22\5\4\0\2\5\15\0"+
    "\6\5\1\124\20\5\4\0\2\5\15\0\4\5\1\125"+
    "\22\5\4\0\2\5\15\0\5\5\1\126\21\5\4\0"+
    "\2\5\15\0\3\5\1\127\23\5\4\0\2\5\15\0"+
    "\17\5\1\130\7\5\4\0\2\5\15\0\1\131\26\5"+
    "\4\0\2\5\15\0\12\5\1\132\14\5\4\0\2\5"+
    "\15\0\2\5\1\133\24\5\4\0\2\5\15\0\23\5"+
    "\1\134\3\5\4\0\2\5\15\0\5\5\1\135\21\5"+
    "\4\0\2\5\15\0\1\136\26\5\4\0\2\5\15\0"+
    "\5\5\1\137\21\5\4\0\2\5\15\0\14\5\1\140"+
    "\12\5\4\0\2\5\15\0\2\5\1\141\24\5\4\0"+
    "\2\5\15\0\6\5\1\142\20\5\4\0\2\5\15\0"+
    "\2\5\1\143\24\5\4\0\2\5\15\0\5\5\1\144"+
    "\21\5\4\0\2\5\15\0\12\5\1\145\14\5\4\0"+
    "\2\5\15\0\6\5\1\146\20\5\4\0\2\5\15\0"+
    "\1\147\26\5\4\0\2\5\15\0\2\5\1\150\24\5"+
    "\4\0\2\5\15\0\12\5\1\151\14\5\4\0\2\5"+
    "\15\0\14\5\1\152\12\5\4\0\2\5\15\0\11\5"+
    "\1\153\15\5\4\0\2\5\15\0\1\5\1\154\25\5"+
    "\4\0\2\5\15\0\6\5\1\155\20\5\4\0\2\5"+
    "\15\0\15\5\1\156\11\5\4\0\2\5\15\0\2\5"+
    "\1\157\24\5\4\0\2\5\15\0\11\5\1\160\15\5"+
    "\4\0\2\5\15\0\12\5\1\161\14\5\4\0\2\5"+
    "\15\0\6\5\1\162\20\5\4\0\2\5\15\0\20\5"+
    "\1\163\6\5\4\0\2\5\15\0\4\5\1\164\22\5"+
    "\4\0\2\5\15\0\1\165\26\5\4\0\2\5\15\0"+
    "\13\5\1\166\13\5\4\0\2\5\15\0\24\5\1\167"+
    "\2\5\4\0\2\5\15\0\1\170\26\5\4\0\2\5"+
    "\15\0\2\5\1\171\24\5\4\0\2\5\15\0\2\5"+
    "\1\172\24\5\4\0\2\5\15\0\2\5\1\173\24\5"+
    "\4\0\2\5\15\0\4\5\1\174\22\5\4\0\2\5"+
    "\15\0\1\175\26\5\4\0\2\5\15\0\11\5\1\176"+
    "\15\5\4\0\2\5\15\0\14\5\1\177\12\5\4\0"+
    "\2\5\15\0\6\5\1\200\20\5\4\0\2\5\15\0"+
    "\12\5\1\201\14\5\4\0\2\5\15\0\2\5\1\202"+
    "\24\5\4\0\2\5\15\0\20\5\1\203\6\5\4\0"+
    "\2\5\15\0\15\5\1\204\11\5\4\0\2\5\15\0"+
    "\4\5\1\205\22\5\4\0\2\5\15\0\1\206\26\5"+
    "\4\0\2\5\15\0\2\5\1\207\24\5\4\0\2\5"+
    "\15\0\11\5\1\210\15\5\4\0\2\5\15\0\12\5"+
    "\1\211\14\5";

  private static int [] zzUnpackTrans() {
    int [] result = new int[4746];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;
  private static final char[] EMPTY_BUFFER = new char[0];
  private static final int YYEOF = -1;
  private static java.io.Reader zzReader = null; // Fake

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\3\1\4\11\4\1\1\11\1\1\1\11"+
    "\14\1\5\11\1\0\13\1\1\0\14\1\1\0\14\1"+
    "\1\11\101\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[137];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** this buffer may contains the current text array to be matched when it is cheap to acquire it */
  private char[] zzBufferArray;

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the textposition at the last state to be included in yytext */
  private int zzPushbackPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /* user code: */
  public PointcutLexer() {
    this((java.io.Reader)null);
  }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public PointcutLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /**
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x10000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 132) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart(){
    return zzStartRead;
  }

  public final int getTokenEnd(){
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end,int initialState){
    zzBuffer = buffer;
    zzBufferArray = com.intellij.util.text.CharArrayUtil.fromSequenceWithoutCopying(buffer);
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzPushbackPos = 0;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBufferArray != null ? zzBufferArray[zzStartRead+pos]:zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;
    char[] zzBufferArrayL = zzBufferArray;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL)
            zzInput = (zzBufferArrayL != null ? zzBufferArrayL[zzCurrentPosL++] : zzBufferL.charAt(zzCurrentPosL++));
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = (zzBufferArrayL != null ? zzBufferArrayL[zzCurrentPosL++] : zzBufferL.charAt(zzCurrentPosL++));
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          int zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 21:
          { return com.aopphp.go.psi.PointcutTypes.WITHIN;
          }
        case 31: break;
        case 13:
          { return com.aopphp.go.psi.PointcutTypes.T_OBJECT_ACCESS;
          }
        case 32: break;
        case 30:
          { return com.aopphp.go.psi.PointcutTypes.STATICINITIALIZATION;
          }
        case 33: break;
        case 16:
          { return com.aopphp.go.psi.PointcutTypes.T_LOGICAL_OR;
          }
        case 34: break;
        case 7:
          { return com.aopphp.go.psi.PointcutTypes.T_RIGHT_PAREN;
          }
        case 35: break;
        case 17:
          { return com.aopphp.go.psi.PointcutTypes.T_LOGICAL_AND;
          }
        case 36: break;
        case 18:
          { return com.aopphp.go.psi.PointcutTypes.T_THIS;
          }
        case 37: break;
        case 22:
          { return com.aopphp.go.psi.PointcutTypes.PUBLIC;
          }
        case 38: break;
        case 14:
          { return com.aopphp.go.psi.PointcutTypes.T_STATIC_ACCESS;
          }
        case 39: break;
        case 1:
          { return com.intellij.psi.TokenType.BAD_CHARACTER;
          }
        case 40: break;
        case 9:
          { return com.aopphp.go.psi.PointcutTypes.T_ALTERNATION;
          }
        case 41: break;
        case 29:
          { return com.aopphp.go.psi.PointcutTypes.MATCHINHERITED;
          }
        case 42: break;
        case 10:
          { return com.aopphp.go.psi.PointcutTypes.T_NEGATION;
          }
        case 43: break;
        case 26:
          { return com.aopphp.go.psi.PointcutTypes.PROTECTED;
          }
        case 44: break;
        case 27:
          { return com.aopphp.go.psi.PointcutTypes.CFLOWBELOW;
          }
        case 45: break;
        case 20:
          { return com.aopphp.go.psi.PointcutTypes.ACCESS;
          }
        case 46: break;
        case 11:
          { return com.aopphp.go.psi.PointcutTypes.T_SUBNAMESPACE_SIGN;
          }
        case 47: break;
        case 4:
          { return com.aopphp.go.psi.PointcutTypes.T_NS_SEPARATOR;
          }
        case 48: break;
        case 6:
          { return com.aopphp.go.psi.PointcutTypes.T_LEFT_PAREN;
          }
        case 49: break;
        case 28:
          { return com.aopphp.go.psi.PointcutTypes.INITIALIZATION;
          }
        case 50: break;
        case 23:
          { return com.aopphp.go.psi.PointcutTypes.DYNAMIC;
          }
        case 51: break;
        case 25:
          { return com.aopphp.go.psi.PointcutTypes.EXECUTION;
          }
        case 52: break;
        case 3:
          { return com.aopphp.go.psi.PointcutTypes.T_NAME_PART;
          }
        case 53: break;
        case 15:
          { return com.aopphp.go.psi.PointcutTypes.T_DOUBLE_ASTERISK;
          }
        case 54: break;
        case 19:
          { return com.aopphp.go.psi.PointcutTypes.FINAL;
          }
        case 55: break;
        case 5:
          { return com.aopphp.go.psi.PointcutTypes.T_ANNOTATION;
          }
        case 56: break;
        case 8:
          { return com.aopphp.go.psi.PointcutTypes.T_ASTERISK;
          }
        case 57: break;
        case 24:
          { return com.aopphp.go.psi.PointcutTypes.PRIVATE;
          }
        case 58: break;
        case 2:
          { return com.intellij.psi.TokenType.WHITE_SPACE;
          }
        case 59: break;
        case 12:
          { return com.aopphp.go.psi.PointcutTypes.T_COMMENT;
          }
        case 60: break;
        default:
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            return null;
          }
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
