package com.malt.aster.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;

import java.util.List;
import java.util.Objects;

public class UnoCommand extends Command implements AdminCommand {
	
	private EventWaiter waiter;

    public UnoCommand() {
        super("uno");
        this.waiter = new EventWaiter();
    }

    @Override
    public void execute(GuildMessageReceivedEvent evt, List<String> params) {
        if(params.size() == 0)
            evt.getChannel().sendMessage("Need to set gambling value... WIP").queue();
        else {
        	User commander = evt.getAuthor();
        	
        	//Send the initial message allowing users to join and the commander to start the game
        	//using reactions
        	Message startUno = evt.getChannel().sendMessage("UNO started! React to this message"
        			+ " with a CHECK to join, the game will begin when @" + 
        			commander.getAsMention() + " reacts with an 'X'").complete();
        	startUno.addReaction("U+2705").queue(); //Check
        	startUno.addReaction("U+274E").queue(); //X
        	
        	this.waiter.waitForEvent(MessageReactionAddEvent.class,
                    e -> Objects.equals(e.getUser(), commander) && e.getReactionEmote().getAsCodepoints().equals("U+274E"),
                    e ->
        	{
        		evt.getChannel().sendMessage("jsjsjasjasjjsak").queue();
        		ReactionPaginationAction players = startUno.retrieveReactionUsers("U+2705");
        		players.forEach((user) ->
        			evt.getChannel().sendMessage(user.getName()).queue()
        		);
        		
        	});
        	
        	
        	
        	//Generate a list of all the players (those who have reacted with a check)
        	
        	
        	
        	
        	
        	
        }
    }

    
    @Override
    public String getDescription() {
        return "Starts and runs a game of UNO";
    }
}
