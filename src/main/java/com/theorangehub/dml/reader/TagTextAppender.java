package com.theorangehub.dml.reader;

import com.theorangehub.dml.DMLBuilder;
import com.theorangehub.dml.Tag;

public class TagTextAppender implements TagProcessor {
    public static TagTextAppender INSTANCE = new TagTextAppender();

    protected TagTextAppender() {
    }

    @Override
    public void accept(TagResolver resolver, DMLBuilder builder, Tag tag, StringBuilder input) {
        for (Object obj : tag.getChilds()) {
            int length = input.length();

            if (obj instanceof Tag) {
                resolver.process(builder, (Tag) obj, input);
            } else {
                input.append(obj == null ? "" : obj.toString());

                if (input.length() > 1 && length > 0 && input.length() > length) {
                    if (Character.isWhitespace(input.charAt(length - 1)) && Character.isWhitespace(input.charAt(length))) {
                        input.deleteCharAt(length);
                    }
                }
            }
        }
    }
}
