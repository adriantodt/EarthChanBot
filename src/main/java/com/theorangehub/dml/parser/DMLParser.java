package com.theorangehub.dml.parser;

import com.google.common.collect.Sets;
import com.theorangehub.dml.SyntaxException;
import com.theorangehub.dml.Tag;
import com.theorangehub.dml.parser.lexer.DMLLexer;
import com.theorangehub.dml.parser.lexer.Token;
import com.theorangehub.dml.parser.lexer.TokenType;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DMLParser extends Parser {
    private final Set<String> SIMPLE_TAGS = Sets.newHashSet("br", "img", "image", "mention");

    public DMLParser(DMLLexer lexer) {
        super(lexer);
    }

    public List<Object> parse() {
        List<Object> result = new LinkedList<>();

        while (true) {
            Object obj = parseOuter(true);
            if (obj instanceof ClosingTag) {
                throw new SyntaxException("Unexpected " + obj, ((ClosingTag) obj).getPosition());
            }
            if (obj == null) break;
            result.add(obj);
        }

        return result;
    }

    private Object parseInner() {
        Token t = eat();

        switch (t.getType()) {
            case EOF: {
                return null;
            }

            case SLASH: {
                Token name = eat(TokenType.IDENTIFIER);
                eat(TokenType.GT);
                return new ClosingTag(name);
            }

            case IDENTIFIER: {
                return parseTag(t);
            }

            default: {
                throw new SyntaxException("Unexpected " + t, t.getPosition());
            }
        }
    }

    private Object parseOuter(boolean root) {
        Token t = eat();

        switch (t.getType()) {
            case TEXT: {
                return t.getString();
            }

            case LT: {
                return parseInner();
            }

            case EOF: {
                if (root) return null;
                throw new SyntaxException("Unexpected " + t, t.getPosition());
            }

            default: {
                throw new SyntaxException("Unexpected " + t, t.getPosition());
            }
        }
    }

    private Tag parseTag(Token starting) {
        Tag tag = new Tag(starting);

        boolean isSimple = SIMPLE_TAGS.contains(starting.getString());

        loop:
        while (true) {
            Token t = eat();

            switch (t.getType()) {
                case GT: {
                    if (isSimple) return tag;
                    break loop;
                }

                case SLASH: {
                    eat(TokenType.GT);
                    return tag;
                }

                case IDENTIFIER: {
                    if (nextIs(TokenType.EQUALS)) {
                        eat();
                        tag.getAttributes().put(t.getString(), eat(TokenType.STRING).getString());
                    } else {
                        tag.getAttributes().put(t.getString(), t.getString());
                    }

                    break;
                }

                default: {
                    throw new SyntaxException("Unexpected " + t, t.getPosition());
                }
            }
        }

        while (true) {
            Object child = parseOuter(false);

            if (child instanceof ClosingTag) {
                ClosingTag closingTag = (ClosingTag) child;

                if (!closingTag.getName().equals(tag.getName())) {
                    throw new SyntaxException("Unexpected closing tag " + closingTag, closingTag.getPosition());
                }

                break;
            }

            tag.getChilds().add(child);
        }

        return tag;
    }
}
