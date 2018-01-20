package com.theorangehub.dml.parser;

import com.theorangehub.dml.parser.lexer.Position;
import com.theorangehub.dml.parser.lexer.Token;

public class ClosingTag {
    private final String name;
    private final Position position;

    public ClosingTag(Token token) {
        this(token.getString(), token.getPosition());
    }

    public ClosingTag(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $position = this.getPosition();
        result = result * PRIME + ($position == null ? 43 : $position.hashCode());
        return result;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ClosingTag)) return false;
        final ClosingTag other = (ClosingTag) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$position = this.getPosition();
        final Object other$position = other.getPosition();
        if (this$position == null ? other$position != null : !this$position.equals(other$position)) return false;
        return true;
    }

    public String toString() {
        return "ClosingTag(name=" + this.getName() + ", position=" + this.getPosition() + ")";
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClosingTag;
    }

    public String getName() {
        return this.name;
    }

    public Position getPosition() {
        return this.position;
    }
}
