/*
 * 03/16/2004
 *
 * UnixShellTokenMaker.java - Scanner for UNIX shell scripts.
 *
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE file for details.
 */
package xlogo.kernel;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;

import javax.swing.text.Segment;


/**
 * A token maker that turns text into a linked list of <code>Token</code>s
 * for syntax highlighting Logo.
 *
 * @author J.B. Langston
 * @version 0.1
 */
public class LogoTokenMaker extends AbstractTokenMaker {

    public LogoTokenMaker() {
        super();
    }

    /**
     * Checks the token to give it the exact ID it deserves before
     * being passed up to the super method.
     *
     * @param segment     <code>Segment</code> to get text from.
     * @param start       Start offset in <code>segment</code> of token.
     * @param end         End offset in <code>segment</code> of token.
     * @param tokenType   The token's type.
     * @param startOffset The offset in the document at which the token occurs.
     */
    @Override
    public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        if (tokenType == Token.IDENTIFIER) {
            int value = wordsToHighlight.get(segment, start, end);
            if (value != -1)
                tokenType = value;
        }
        super.addToken(segment, start, end, tokenType, startOffset);
    }

    @Override
    public String[] getLineCommentStartAndEnd(int languageIndex) {
        return new String[]{"#", null};
    }

    /**
     * Returns whether tokens of the specified type should have "mark
     * occurrences" enabled for the current programming language.
     *
     * @param type The token type.
     * @return Whether tokens of this type should have "mark occurrences"
     * enabled.
     */
    @Override
    public boolean getMarkOccurrencesOfTokenType(int type) {
        return type == Token.IDENTIFIER || type == Token.VARIABLE || type == Token.FUNCTION;
    }

    /**
     * Returns the words to highlight for Logo.
     *
     * @return A <code>TokenMap</code> containing the words to highlight for
     * Logo.
     * @see org.fife.ui.rsyntaxtextarea.AbstractTokenMaker#getWordsToHighlight
     */
    @Override
    public TokenMap getWordsToHighlight() {
        TokenMap tokenMap = new TokenMap(true); // ignore case
        tokenMap.put("to", Token.RESERVED_WORD);
        tokenMap.put("end", Token.RESERVED_WORD);
        tokenMap.put("true", Token.LITERAL_BOOLEAN);
        tokenMap.put("false", Token.LITERAL_BOOLEAN);
        for (var p : Interpreter.primitiveMap.keySet()) {
            tokenMap.put(p, Token.FUNCTION);
        }
        return tokenMap;
    }

    private boolean isWordBreak(char c) {
        return " \t[]()+-/*<=>&|#".indexOf(c) > -1;
    }

    private boolean isWhitespace(char c) {
        return c == '\t' || c == ' ';
    }

    private boolean isNumeric(char c) {
        return "0123456789.eE".indexOf(c) > -1;
    }

    private boolean isOperator(char c) {
        return "+-/*<=>&|".indexOf(c) > -1;
    }

    private boolean isSeparator(char c) {
        return "[]()".indexOf(c) > -1;
    }

    /**
     * Returns a list of tokens representing the given text.
     *
     * @param text           The text to break into tokens.
     * @param startTokenType The token with which to start tokenizing.
     * @param startOffset    The offset at which the line of tokens begins.
     * @return A linked list of tokens representing <code>text</code>.
     */
    @Override
    public Token getTokenList(Segment text, int startTokenType, final int startOffset) {

        resetTokenList();

        char[] array = text.array;
        int start = text.offset;
        int end = text.offset + text.count;
        int offset = startOffset - text.offset;
        int type = startTokenType;
        boolean escaped = false;

        for (int i = text.offset; i < end; i++) {
            char c = array[i];

            // End conditions
            if (type == Token.WHITESPACE) {
                if (!isWhitespace(c)) {
                    addToken(text, start, i - 1, Token.WHITESPACE, offset + start);
                    type = Token.NULL;
                }
            } else if (type == Token.LITERAL_NUMBER_FLOAT) {
                if (!escaped && isWordBreak(c)) {
                    addToken(text, start, i - 1, Token.LITERAL_NUMBER_FLOAT, offset + start);
                    type = Token.NULL;
                } else if (!isNumeric(c)) {
                    type = Token.IDENTIFIER;
                }
            } else if (type != Token.NULL) {
                if (!escaped && isWordBreak(c)) {
                    addToken(text, start, i - 1, type, offset + start);
                    type = Token.NULL;
                }
            }

            // Start conditions
            if (type == Token.NULL) {
                start = i;
                if (escaped) {
                    type = Token.IDENTIFIER;
                } else if (isWhitespace(c)) {
                    type = Token.WHITESPACE;
                } else if (isNumeric(c)) {
                    type = Token.LITERAL_NUMBER_FLOAT;
                } else if (c == '#') {
                    type = Token.COMMENT_EOL;
                    break;
                } else if (c == ':') {
                    type = Token.VARIABLE;
                } else if (c == '"') {
                    type = Token.LITERAL_STRING_DOUBLE_QUOTE;
                } else if (isOperator(c)) {
                    addToken(text, i, i, Token.OPERATOR, offset + i);
                } else if (isSeparator(c)) {
                    addToken(text, i, i, Token.SEPARATOR, offset + i);
                } else {
                    type = Token.IDENTIFIER;
                }
            }

            if (c == '\\') {
                escaped = !escaped;
            } else {
                escaped = false;
            }
        }

        if (type != Token.NULL) {
            addToken(text, start, end - 1, type, offset + start);
        }
        addNullToken();

        return firstToken;
    }
}