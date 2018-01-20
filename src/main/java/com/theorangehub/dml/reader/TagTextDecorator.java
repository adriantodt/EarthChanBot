package com.theorangehub.dml.reader;

import com.theorangehub.dml.DMLBuilder;
import com.theorangehub.dml.Tag;

public class TagTextDecorator extends TagTextAppender {
    private final boolean optionalPrefix;
    private final String prefix;
    private final String suffix;

    public TagTextDecorator(String decoration) {
        this(decoration, true);
    }

    public TagTextDecorator(String decoration, boolean optionalDecoration) {
        this(decoration, decoration, optionalDecoration);
    }

    public TagTextDecorator(String prefix, String suffix) {
        this(prefix, suffix, true);
    }

    public TagTextDecorator(String prefix, String suffix, boolean optionalDecoration) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.optionalPrefix = optionalDecoration;
    }

    @Override
    public void accept(TagResolver resolver, DMLBuilder builder, Tag tag, StringBuilder input) {
        StringBuilder buffer = new StringBuilder();
        super.accept(resolver, builder, tag, buffer);

        if (optionalPrefix && suffix.isEmpty()) return;
        input.append(prefix).append(buffer).append(suffix);
    }
}
