package com.malt.aster.commands.utility;

import com.malt.aster.commands.AdminCommand;
import com.malt.aster.commands.SingleParamCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

/**
 * Command that shows all members with the role provided as a parameter. Returns a sorted list.
 */
public class CheckRolesCommand extends SingleParamCommand implements AdminCommand {

    public CheckRolesCommand() {
        super("checkroles");
        addAlias("cr").addAlias("rolelist");
    }

    @Override
    public void execute(GuildMessageReceivedEvent evt, @Nullable String params) {
        if (params == null)
            evt.getChannel()
                    .sendMessage("You must provide the name of the role to this command")
                    .queue();
        else {
            var embedBuilder = new EmbedBuilder();

            embedBuilder.setTitle("Members with role " + params);
            var rolesWithName = evt.getGuild().getRolesByName(params, true);

            if (rolesWithName.isEmpty()) {
                evt.getChannel()
                        .sendMessage("There is no role with the name " + params)
                        .queue();
            } else {
                Role roleToSearch = evt.getGuild().getRolesByName(params, true).get(0);

                List<Member> membersWithRole = evt.getGuild().getMembersWithRoles(roleToSearch);

                membersWithRole.stream()
                        .sorted(Comparator.comparing(Member::getEffectiveName))
                        .forEach(member -> embedBuilder
                                .appendDescription(member.getEffectiveName())
                                .appendDescription("\n"));

                evt.getChannel().sendMessage(embedBuilder.build()).queue();
            }
        }
    }

    @Override
    public String getDescription() {
        return "Returns a message displaying all members with the provided role";
    }
}
