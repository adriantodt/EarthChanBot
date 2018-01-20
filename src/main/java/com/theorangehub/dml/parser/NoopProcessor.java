package com.theorangehub.dml.parser;

import com.theorangehub.dml.DMLBuilder;
import com.theorangehub.dml.Tag;
import com.theorangehub.dml.reader.TagProcessor;
import com.theorangehub.dml.reader.TagResolver;

public class NoopProcessor implements TagProcessor {
    public static NoopProcessor INSTANCE = new NoopProcessor();

    protected NoopProcessor() {
    }

    @Override
    public void accept(TagResolver resolver, DMLBuilder builder, Tag tag, StringBuilder input) {
    }
}
