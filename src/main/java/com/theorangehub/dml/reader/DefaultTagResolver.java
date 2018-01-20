package com.theorangehub.dml.reader;

import com.theorangehub.dml.DMLReaction;
import com.theorangehub.dml.parser.NoopProcessor;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

public enum DefaultTagResolver implements TagResolver {
    INSTANCE;

    private final TagProcessor defaultProcessor;
    private final Map<String, TagProcessor> processors;

    {
        defaultProcessor = TagTextAppender.INSTANCE;
        processors = new LinkedHashMap<>();

        //<page>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                Map<String, String> attrs = tag.getAttributes();

                if (attrs.containsKey("color")) {
                    String color = attrs.get("color").toLowerCase();
                    Color c = null;
                    if (color.matches("(#|0x)[0123456789abcdef]{1,6}")) {
                        try {
                            c = Color.decode(color);
                        } catch (Exception ignored) {}
                    }

                    if (c == null) {
                        try {
                            c = (Color) Color.class.getField(color).get(null);
                        } catch (Exception ignored) {}
                    }

                    if (c != null) {
                        builder.getEmbed().setColor(c);
                    }
                }

                StringBuilder newBuilder = builder.getEmbed().getDescriptionBuilder();
                defaultProcessor.accept(resolver, builder, tag, newBuilder);
            };
            processors.put("page", processor);
            processors.put("body", processor);
            processors.put("embed", processor);
        }

        //<author>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                Map<String, String> attrs = tag.getAttributes();
                String url = attrs.get("url");
                String imgUrl = attrs.get("imgUrl");

                String name = defaultProcessor.accept(resolver, builder, tag);

                builder.getEmbed().setAuthor(name, url, imgUrl);
            };
            processors.put("author", processor);
        }

        //<img> <image>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                Map<String, String> attrs = tag.getAttributes();
                String src = attrs.get("src");
                String type = attrs.getOrDefault("type", "image");

                switch (type) {
                    case "thumbnail": {
                        builder.getEmbed().setThumbnail(src);
                        return;
                    }

                    case "image":
                    default: {
                        builder.getEmbed().setImage(src);
                    }
                }

            };
            processors.put("img", processor);
            processors.put("image", processor);
        }

        //<description>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                StringBuilder newBuilder = builder.getEmbed().getDescriptionBuilder();
                StringBuilderUtils.clear(newBuilder);
                defaultProcessor.accept(resolver, builder, tag, newBuilder);
            };
            processors.put("description", processor);
        }

        //<p>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                defaultProcessor.accept(resolver, builder, tag, input);
                input.append('\n');
            };
            processors.put("p", processor);
        }

        //<h1> <h2> <h3> <h4> <h5>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                input.append("**");
                defaultProcessor.accept(resolver, builder, tag, input);
                input.append("**\n");
            };
            processors.put("h1", processor);
            processors.put("h2", processor);
            processors.put("h3", processor);
            processors.put("h4", processor);
            processors.put("h5", processor);
        }

        //<br>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                input.append('\n');
            };
            processors.put("br", processor);
        }

        //<title>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                builder.getEmbed().setTitle(defaultProcessor.accept(resolver, builder, tag), tag.getAttributes().get("href"));
            };
            processors.put("title", processor);
        }

        //<footer>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                Map<String, String> attrs = tag.getAttributes();
                String imgUrl = attrs.get("imgUrl");

                String text = defaultProcessor.accept(resolver, builder, tag);

                builder.getEmbed().setFooter(text, imgUrl);
            };
            processors.put("footer", processor);
        }

        //<field>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                Map<String, String> attrs = tag.getAttributes();

                boolean inline = !attrs.getOrDefault("inline", "false").equals("false");

                if (!attrs.getOrDefault("blank", "false").equals("false")) {
                    builder.getEmbed().addBlankField(inline);
                    return;
                }

                String title = Objects.requireNonNull(attrs.get("title"), "title");
                String text = defaultProcessor.accept(resolver, builder, tag);

                builder.getEmbed().addField(title, text, inline);
            };
            processors.put("field", processor);
        }

        //<i> <em>
        {
            TagProcessor processor = new TagTextDecorator("*");
            processors.put("i", processor);
            processors.put("em", processor);
        }

        //<b> <strong>
        {
            TagProcessor processor = new TagTextDecorator("**");
            processors.put("b", processor);
            processors.put("strong", processor);
        }

        //<a>
        {
            TagProcessor processor = (resolver, builder, tag, input) -> {
                String href = tag.getAttributes().get("href");
                String text = defaultProcessor.accept(resolver, builder, tag);

                if (href == null || href.isEmpty()) {
                    input.append(text);
                } else {
                    input.append("[").append(href).append("](").append(text).append(")");
                }
            };
            processors.put("a", processor);
        }

        //<reactions>
        {
            processors.put("reaction", NoopProcessor.INSTANCE);

            Map<String, TagProcessor> reaction = Collections.singletonMap("reaction", (resolver, builder, tag, input) -> {
                Map<String, String> attrs = tag.getAttributes();
                String emote = attrs.get("emote");
                String ref = attrs.get("ref");

                if (emote == null || ref == null) {
                    return;
                }

                String text = defaultProcessor.accept(resolver, builder, tag);

                builder.getReactions().add(new DMLReaction(emote, ref, text));
            });

            TagProcessor processor = (resolver, builder, tag, input) -> {
                Map<String, String> attrs = tag.getAttributes();

                List<DMLReaction> list = new LinkedList<>();
                List<DMLReaction> parentList = builder.getReactions();

                builder.setReactions(list);
                defaultProcessor.accept(
                    new OverrideTagResolver().setParent(resolver).setNameResolver(reaction::get),
                    builder, tag, input
                );
                builder.setReactions(parentList);
                parentList.addAll(list);

                if (!attrs.getOrDefault("hidden", "false").equals("false")) return;

                boolean inline = !attrs.getOrDefault("inline", "false").equals("false");

                String name = attrs.getOrDefault("name", "");

                builder.getEmbed().addField(
                    name,
                    list.stream().map(Object::toString).collect(Collectors.joining("\n")),
                    inline
                );
            };
            processors.put("reactions", processor);
        }
    }

    @Nonnull
    @Override
    public TagProcessor defaultProcessor() {
        return defaultProcessor;
    }

    @Nonnull
    @Override
    public TagProcessor get(String tagName) {
        return processors.getOrDefault(tagName, defaultProcessor);
    }

    public Map<String, TagProcessor> getProcessors() {
        return processors;
    }
}
