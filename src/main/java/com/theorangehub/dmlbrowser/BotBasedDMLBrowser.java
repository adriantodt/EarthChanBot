package com.theorangehub.dmlbrowser;

import com.theorangehub.dml.DMLBuilder;
import com.theorangehub.dml.DMLReaction;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import xyz.cuteclouds.earthchan.core.listeners.operations.ReactionOperations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class BotBasedDMLBrowser extends DMLBrowser {
    protected final GuildMessageReceivedEvent event;
    protected Message message;

    public BotBasedDMLBrowser(GuildMessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    protected void sendMessage(DMLBuilder builder) {
        if (message != null) {
            try {
                message = message.editMessage(builder.build()).complete();
            } catch (Exception e) {
                message = null;
            }
        }

        if (message == null) {
            message = event.getChannel().sendMessage(builder.build()).complete();
        }

        List<DMLReaction> reactions = builder.getReactions();
        if (reactions.isEmpty()) return;

        Map<String, DMLReaction> map = reactions.stream().collect(Collectors.toMap(DMLReaction::getEmote, o -> o));

        ReactionOperations.INSTANCE.builder()
            .message(message)
            .timeout(2, TimeUnit.MINUTES)
            .addReactions(map.keySet().toArray(new String[0]))
            .onReaction(e -> {
                if (!e.getUser().equals(event.getAuthor()))
                    return false;

                ReactionEmote emote = e.getReactionEmote();

                DMLReaction reaction = map.get(
                    emote.isEmote() ? emote.getName() + ":" + emote.getId() : emote.getName()
                );

                if (reaction == null) return false;
                handle(reaction.getRef());

                return true;
            })
            .forceCreate();
    }
}
