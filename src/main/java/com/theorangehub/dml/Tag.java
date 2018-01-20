package com.theorangehub.dml;

import com.theorangehub.dml.parser.lexer.Position;
import com.theorangehub.dml.parser.lexer.Token;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Tag {
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<Object> childs = new LinkedList<>();
    private final String name;
    private final Position position;

    public Tag(Token token) {
        this(token.getString(), token.getPosition());
    }

    public Tag(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $attributes = this.getAttributes();
        result = result * PRIME + ($attributes == null ? 43 : $attributes.hashCode());
        final Object $childs = this.getChilds();
        result = result * PRIME + ($childs == null ? 43 : $childs.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $position = this.getPosition();
        result = result * PRIME + ($position == null ? 43 : $position.hashCode());
        return result;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Tag)) return false;
        final Tag other = (Tag) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$attributes = this.getAttributes();
        final Object other$attributes = other.getAttributes();
        if (this$attributes == null ? other$attributes != null : !this$attributes.equals(other$attributes)) return false;
        final Object this$childs = this.getChilds();
        final Object other$childs = other.getChilds();
        if (this$childs == null ? other$childs != null : !this$childs.equals(other$childs)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$position = this.getPosition();
        final Object other$position = other.getPosition();
        if (this$position == null ? other$position != null : !this$position.equals(other$position)) return false;
        return true;
    }

    public String toString() {
        return "Tag(attributes=" + this.getAttributes() + ", childs=" + this.getChilds() + ", name=" + this.getName() + ", position=" + this
            .getPosition() + ")";
    }

    protected boolean canEqual(Object other) {
        return other instanceof Tag;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public List<Object> getChilds() {
        return this.childs;
    }

    public String getName() {
        return this.name;
    }

    public Position getPosition() {
        return this.position;
    }
}
