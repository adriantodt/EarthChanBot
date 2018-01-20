package com.theorangehub.dml.reader;

import com.theorangehub.dml.DMLBuilder;
import com.theorangehub.dml.Tag;

public interface TagProcessor {
    void accept(TagResolver resolver, DMLBuilder builder, Tag tag, StringBuilder input);

    default String accept(TagResolver resolver, DMLBuilder builder, Tag tag) {
        StringBuilder b = new StringBuilder();
        accept(resolver, builder, tag, b);
        return b.toString();
    }
}
