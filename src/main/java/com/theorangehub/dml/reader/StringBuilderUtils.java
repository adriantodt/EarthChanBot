package com.theorangehub.dml.reader;

public class StringBuilderUtils {
    public static void clear(StringBuilder builder) {
        builder.setLength(0);
    }

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder("    \naaaaaa\n a ");
        int length = builder.length();
        builder.append("    \n memes \n aaaaaa\n\n\n");
        trim(builder, length);
        System.out.println(builder);
    }

    public static void trim(StringBuilder builder, int start, int end) {
        int first, last;

        for (first = start; first < end; first++)
            if (!Character.isWhitespace(builder.charAt(first)))
                break;

        for (last = end; last > first; last--)
            if (!Character.isWhitespace(builder.charAt(last - 1)))
                break;

        builder.delete(last, end).delete(start, first);
    }

    public static void trim(StringBuilder builder) {
        trim(builder, 0, builder.length());
    }

    public static void trim(StringBuilder builder, int start) {
        trim(builder, start, builder.length());
    }
}
