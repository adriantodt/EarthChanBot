package com.theorangehub.dml;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.LinkedList;
import java.util.List;

public class DMLBuilder {
    private EmbedBuilder embed;
    private MessageBuilder message;
    private List<DMLReaction> reactions;

    public Message build() {
        MessageBuilder message = getMessage();
        if (embed != null) message.setEmbed(buildEmbed());
        return configureMessage(message).build();
    }

    public MessageEmbed buildEmbed() {
        EmbedBuilder embed = getEmbed();
        return configureEmbed(embed).build();
    }

    /**
     * Called right before {@link EmbedBuilder#build()}. Override to configure fields before building.
     */
    protected EmbedBuilder configureEmbed(EmbedBuilder embed) {
        return embed;
    }

    /**
     * Called right before {@link MessageBuilder#build()}. Override to configure fields before building.
     */
    protected MessageBuilder configureMessage(MessageBuilder message) {
        return message;
    }

    public EmbedBuilder getEmbed() {
        if (embed == null) embed = newEmbedBuilder();
        return embed;
    }

    public EmbedBuilder rawGetEmbed() {
        return embed;
    }

    public MessageBuilder rawGetMessage() {
        return message;
    }

    public DMLBuilder setEmbed(EmbedBuilder embed) {
        this.embed = embed;
        return this;
    }

    public MessageBuilder getMessage() {
        if (message == null) message = newMessageBuilder();
        return message;
    }

    public DMLBuilder setMessage(MessageBuilder message) {
        this.message = message;
        return this;
    }

    public List<DMLReaction> getReactions() {
        if (reactions == null) reactions = newReactions();
        return reactions;
    }

    public DMLBuilder setReactions(List<DMLReaction> reactions) {
        this.reactions = reactions;
        return this;
    }

    /**
     * Called when the builder needs a EmbedBuilder. Overrride to pre-configure fields.
     *
     * @return a fresh EmbedBuilder
     */
    protected EmbedBuilder newEmbedBuilder() {
        return new EmbedBuilder();
    }

    /**
     * Called when the builder needs a MessageBuilder. Overrride to pre-configure fields.
     *
     * @return a fresh EmbedBuilder
     */
    protected MessageBuilder newMessageBuilder() {
        return new MessageBuilder();
    }

    protected List<DMLReaction> newReactions() {
        return new LinkedList<>();
    }
}
