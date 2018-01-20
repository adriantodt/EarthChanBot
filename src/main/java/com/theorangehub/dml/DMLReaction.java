package com.theorangehub.dml;

import java.util.regex.Pattern;

public class DMLReaction {
    private static final Pattern isEmoteNotation = Pattern.compile("[a-zA-Z0-9_\\-]+:\\d+");
    private static final Pattern isFullEmote = Pattern.compile("<:[a-zA-Z0-9_\\-]+:\\d+>");
    private final String emote;
    private final String ref;
    private final String text;

    public DMLReaction(String emote, String ref, String text) {

        this.emote = emote;
        this.ref = ref;
        this.text = text;
    }

    @Override
    public String toString() {
        String emote = this.emote;

        if (isEmoteNotation.matcher(emote).matches()) emote = "<:" + emote + ">";

        return emote + " - " + text;
    }

    public String getEmote() {
        if (isFullEmote.matcher(emote).matches()) return emote.substring(2, emote.length() - 1);
        return emote;
    }

    public String getRef() {
        return ref;
    }

    public String getText() {
        return text;
    }
}
