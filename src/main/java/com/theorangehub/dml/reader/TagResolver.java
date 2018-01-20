package com.theorangehub.dml.reader;

import com.theorangehub.dml.DMLBuilder;
import com.theorangehub.dml.Tag;

import javax.annotation.Nonnull;

public interface TagResolver {
    @Nonnull
    TagProcessor defaultProcessor();

    @Nonnull
    TagProcessor get(String tagName);

    default TagProcessor get(Tag tag) {
        return get(tag.getName());
    }

    default String process(DMLBuilder builder, Tag tag) {
        return get(tag).accept(this, builder, tag);
    }

    default void process(DMLBuilder builder, Tag tag, StringBuilder input) {
        get(tag).accept(this, builder, tag, input);
    }
}
