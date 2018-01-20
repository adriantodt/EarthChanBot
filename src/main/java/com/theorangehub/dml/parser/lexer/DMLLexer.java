/*
 *  Copyright 2017 An Tran and Adrian Todt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.theorangehub.dml.parser.lexer;

import com.theorangehub.dml.SyntaxException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DMLLexer {
    private static final class Entry {
        private final char character;
        private final long index;
        private final long line;
        private final long lineIndex;

        private Entry(long index, long line, long lineIndex, char character) {
            this.index = index;
            this.line = line;
            this.lineIndex = lineIndex;
            this.character = character;
        }

        @Override
        public String toString() {
            return String.valueOf(character);
        }
    }

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
    private final Entry[] history;
    private final Reader reader;
    private final List<Token> tokens;
    private char current;
    private boolean eof;
    private long index;
    private long line;
    private long lineIndex;
    private int previous;

    public DMLLexer(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public DMLLexer(String s) {
        this(new StringReader(s));
    }

    public DMLLexer(Reader reader) {
        this(reader, 4);
    }

    public DMLLexer(Reader reader, int historyBuffer) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.eof = false;
        this.tokens = new ArrayList<>();

        history = new Entry[historyBuffer];

        this.current = 0;
        this.index = -1;
        this.lineIndex = 0;
        this.line = 1;

        do {
            readOuter();
            if (!hasNext()) break;
            while (readTokens()) ;
        } while (hasNext());

        if (lastToken().getType() != TokenType.EOF) {
            tokens.add(new Token(getPosition(), TokenType.EOF));
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the readToken character in the source string.
     *
     * @return The readToken character, or 0 if past the end of the source string.
     */
    private char advance() {
        int c;
        if (this.previous != 0) {
            this.previous--;

            Entry entry = history[previous];

            current = entry.character;
            index = entry.index;
            line = entry.line;
            lineIndex = entry.lineIndex;

            return this.current;
        } else {
            try {
                c = this.reader.read();
            } catch (IOException exception) {
                throw new SyntaxException("Exception occurred while lexing", getPosition(), exception);
            }

            if (c <= 0) { // End of stream
                this.eof = true;
                c = 0;
            }
        }

        this.index += 1;
        if (this.current == '\r') {
            this.line += 1;
            this.lineIndex = c == '\n' ? 0 : 1;
        } else if (c == '\n') {
            this.line += 1;
            this.lineIndex = 0;
        } else {
            this.lineIndex += 1;
        }
        this.current = (char) c;

        System.arraycopy(history, 0, history, 1, history.length - 1);

        history[0] = new Entry(index, line, lineIndex, current);

        return this.current;
    }

    /**
     * Consume the next character, and check that
     * it matches a specified character.
     *
     * @param c The character to match.
     * @return The character.
     */
    private char advance(char c) {
        char n = this.advance();
        if (n != c) {
            throw new SyntaxException("Expected '" + c + "' and instead saw '" + n + "'", getPosition());
        }
        return n;
    }

    /**
     * Get the next n characters.
     *
     * @param n The number of characters to take.
     * @return A string of n characters.
     * @throws SyntaxException Substring bounds error if there are not
     *                         n characters remaining in the source string.
     */
    private String advance(int n) {
        if (n == 0) {
            return "";
        }

        char[] chars = new char[n];
        int pos = 0;

        while (pos < n) {
            chars[pos] = this.advance();
            if (!this.hasNext()) {
                throw new SyntaxException("Substring bounds error", getPosition());
            }
            pos += 1;
        }
        return new String(chars);
    }

    /**
     * Get the next char in the string, skipping whitespace.
     *
     * @return A character, or 0 if there are no more characters.
     */
    private char advanceClean() {
        while (true) {
            char c = this.advance();
            if (c == 0 || c > ' ') {
                return c;
            }
        }
    }

    /**
     * Get the text up but not including the specified character or the
     * end of line, whichever comes first.
     *
     * @param delimiter A delimiter character.
     * @return A string.
     */
    private String advanceTo(char delimiter) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = this.advance();
            if (c == delimiter || c == 0 || c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }    /**
     * Make a printable string of this KaiperLexer.
     *
     * @return " at {index} [{character} : {line}]"
     */
    @Override
    public String toString() {
        return getPosition().toString();
    }

    /**
     * Get the text up but not including one of the specified delimiter
     * characters or the end of line, whichever comes first.
     *
     * @param delimiters A set of delimiter characters.
     * @return A string, trimmed.
     */
    private String advanceTo(String delimiters) {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = this.advance();
            if (delimiters.indexOf(c) >= 0 || c == 0 ||
                c == '\n' || c == '\r') {
                if (c != 0) {
                    this.back();
                }
                return sb.toString().trim();
            }
            sb.append(c);
        }
    }

    /**
     * Back up one character. This provides a sort of lookahead capability,
     * so that you can test for a digit or letter before attempting to parse
     * the readToken number or identifier.
     */
    private void back() {
        previous++;
    }

    /**
     * Return the current lexer's positional data.
     *
     * @return A plain object with position data.
     */
    public Position getPosition() {
        return new Position(index, line, lineIndex);
    }

    /**
     * Get the current list of tokens.
     *
     * @return Current list of tokens in the lexer.
     */
    public List<Token> getTokens() {
        return tokens;
    }

    public boolean hasNext() {
        return !(previous == 0 && this.eof);
    }

    private Token lastToken() {
        return tokens.get(tokens.size() - 1);
    }

    private Token make(TokenType type) {
        return make(new Position(index, line, lineIndex), type);
    }

    private Token make(TokenType type, String value) {
        return make(new Position(index - value.length(), line, lineIndex - value.length()), type, value);
    }

    private Token make(Position position, TokenType type) {
        return new Token(position, type);
    }

    private Token make(Position position, TokenType type, String value) {
        return new Token(position, type, value);
    }

    /**
     * Peek and advance if the prompt is the same as the peeked character.
     *
     * @param prompt The character to match.
     * @return if the prompt is the same as the peeked character.
     */
    private boolean match(char prompt) {
        if (advance() == prompt) {
            return true;
        }
        back();
        return false;
    }

    /**
     * Get and remove a token from the tokens list.
     *
     * @return The next token.
     */
    public Token next() {
        return tokens.remove(0);
    }

    private void nextBlockComment() {
        while (hasNext()) {
            if (peek() == '-') {
                advance();
                if (peek() == '-') {
                    advance();
                    if (peek() == '>') {
                        advance();
                        break;
                    }
                    back();
                }
                back();
            }
            advance();
        }
    }

    /**
     * @return The next character.
     */
    private char peek() {
        char c = advance();
        back();
        return c;
    }

    private void push(Token token) {
        if (token == null) throw new IllegalArgumentException();
        tokens.add(token);
    }

    // useful for lexer-phase desugaring
    private void queue(char character) {
        System.arraycopy(history, previous + 1, history, 3, history.length - 3);
        history[previous] = new Entry(index, line, lineIndex, character);
        back();
    }

    private void readName(char init) {
        readName(String.valueOf(init));
    }

    private void readName(String init) {
        StringBuilder sb = new StringBuilder(init);

        char c;
        while (true) {
            c = advance();

            if (Character.isLetterOrDigit(c) || c == ':') {
                sb.append(c);
            } else {
                break;
            }
        }

        back();

        String value = sb.toString();
        push(make(TokenType.IDENTIFIER, value));
    }

    private void readOuter() {
        StringBuilder sb = new StringBuilder();

        char c;
        while (true) {
            c = advance();

            if (c == 0) {
                break;
            } else if (c != '<') {
                sb.append(c);
            } else {
                char c2 = advance();
                if (Character.isAlphabetic(c2) || c2 == '/') {
                    back();
                    break;
                }
                sb.append(c).append(c2);
            }
        }

        back();

        String value = SPACE_PATTERN.matcher(sb.toString()).replaceAll(" ");
        if (!value.isEmpty()) push(make(TokenType.TEXT, value));
    }

    public void readString(char quote) {
        char c;
        StringBuilder sb = new StringBuilder();
        while (true) {
            c = this.advance();
            switch (c) {
                case 0:
                case '\r':
                case '\n':
                    throw new SyntaxException("Unterminated string", new Position(index, line, lineIndex));
                case '\\':
                    c = this.advance();
                    switch (c) {
                        case 'b':
                            sb.append('\b');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 'u':
                            try {
                                sb.append((char) Integer.parseInt(this.advance(4), 16));
                            } catch (NumberFormatException e) {
                                throw new SyntaxException("Illegal escape", new Position(index, line, lineIndex), e);
                            }
                            break;
                        case '"':
                        case '\'':
                        case '\\':
                        case '/':
                            sb.append(c);
                            break;
                        default:
                            throw new SyntaxException("Illegal escape", new Position(index, line, lineIndex));
                    }
                    break;
                default:
                    if (c == quote) {
                        Token token = make(TokenType.STRING, sb.toString());
                        push(token);
                        return;
                    }
                    sb.append(c);
            }
        }
    }

    private boolean readTokens() {
        if (!hasNext()) {
            tokens.add(make(TokenType.EOF));
            return false;
        }

        char c = advance();

        while (Character.isSpaceChar(c) || c == '\t') c = advance();

        switch (c) {
            case '=': {
                push(make(TokenType.EQUALS));
                return true;
            }

            case '>': {
                push(make(TokenType.GT));
                return false;
            }

            case '/': {
                push(make(TokenType.SLASH));
                return true;
            }

            case '<': {
                if (match('!')) {
                    if (match('-')) {
                        if (match('-')) {
                            nextBlockComment();
                            return false;
                        }
                        back();
                    }
                    back();
                }
                push(make(TokenType.LT));
                return true;
            }

            case '"': {
                readString('"');
                return true;
            }
            case '\'': {
                readString('\'');
                return true;
            }

            case '\r':
            case '\n': {
                return true;
            }

            case '\0': {
                push(make(TokenType.EOF));
                return false;
            }

            case (char) -1: {
                push(make(TokenType.EOF));
                return false;
            }

            default: {
                if (Character.isLetter(c)) {
                    readName(c);
                    return true;
                }
                throw new SyntaxException("Unrecognized `" + c + "`", getPosition());
            }
        }
    }

    /**
     * Skip characters until the readToken character is the requested character.
     * If the requested character is not found, no characters are skipped.
     *
     * @param to A character to skip to.
     * @return The requested character, or zero if the requested character
     * is not found.
     */
    private char skipTo(char to) {
        char c;
        try {
            long startIndex = this.index;
            long startCharacter = this.lineIndex;
            long startLine = this.line;
            this.reader.mark(1000000);
            do {
                c = this.advance();
                if (c == 0) {
                    this.reader.reset();
                    this.index = startIndex;
                    this.lineIndex = startCharacter;
                    this.line = startLine;
                    return c;
                }
            } while (c != to);
        } catch (IOException exception) {
            throw new SyntaxException("Exception occurred while lexing", getPosition(), exception);
        }
        this.back();
        return c;
    }



}