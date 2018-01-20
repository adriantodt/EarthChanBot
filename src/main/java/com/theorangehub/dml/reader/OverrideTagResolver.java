package com.theorangehub.dml.reader;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class OverrideTagResolver implements TagResolver {
    private TagProcessor defaultProcessor;
    private Function<String, TagProcessor> nameResolver;
    private TagResolver parent;
    private Function<TagProcessor, TagProcessor> tagResolver;

    @Nonnull
    @Override
    public TagProcessor defaultProcessor() {
        if (defaultProcessor != null) return defaultProcessor;
        if (parent != null) return parent.defaultProcessor();
        throw new IllegalStateException("null default processor");
    }

    @Nonnull
    @Override
    public TagProcessor get(String tagName) {
        TagProcessor processor = null;
        if (nameResolver != null) processor = nameResolver.apply(tagName);
        if (processor != null) return processor;

        if (parent != null) processor = parent.get(tagName);
        if (processor != null && tagResolver != null) processor = tagResolver.apply(processor);

        if (processor != null) return processor;
        throw new IllegalStateException("null processor");
    }

    public TagProcessor getDefaultProcessor() {
        return this.defaultProcessor;
    }

    public OverrideTagResolver setDefaultProcessor(TagProcessor defaultProcessor) {
        this.defaultProcessor = defaultProcessor;
        return this;
    }

    public Function<String, TagProcessor> getNameResolver() {
        return this.nameResolver;
    }

    public OverrideTagResolver setNameResolver(Function<String, TagProcessor> nameResolver) {
        this.nameResolver = nameResolver;
        return this;
    }

    public TagResolver getParent() {
        return this.parent;
    }

    public OverrideTagResolver setParent(TagResolver parent) {
        this.parent = parent;
        return this;
    }

    public Function<TagProcessor, TagProcessor> getTagResolver() {
        return this.tagResolver;
    }

    public OverrideTagResolver setTagResolver(Function<TagProcessor, TagProcessor> tagResolver) {
        this.tagResolver = tagResolver;
        return this;
    }
}
