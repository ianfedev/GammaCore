package net.seocraft.commons.bukkit.old.command;

import com.google.common.collect.Lists;
import me.fixeddev.bcm.AbstractAdvancedCommand;
import me.fixeddev.bcm.CommandContext;
import me.fixeddev.bcm.basic.ArgumentArray;
import me.fixeddev.bcm.basic.Namespace;
import net.seocraft.api.core.redis.messager.Messager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class AdminChatCommand extends AbstractAdvancedCommand {


    public AdminChatCommand(Messager messager) {
        super(new String[]{"adminchat"});

        setExpectedFlags(Lists.newArrayList('i'));
        setUsage("/<command> <message...> [-i]");
        setPermission("commons.admin");
        setMinArguments(1);

    }

    @Override
    public boolean execute(CommandContext commandContext) {
        CommandSender sender = commandContext.getNamespace().getObject(CommandSender.class, "sender");
        String message = commandContext.getJoinedArgs(0);
        boolean important = commandContext.getFlagValue('i');

        return true;
    }

    @Override
    public List<String> getSuggestions(Namespace namespace, ArgumentArray arguments) {
        String getLastArgument = arguments.get(arguments.getPosition() - 1);

        if(getLastArgument.startsWith("@")){

        }


        return new ArrayList<>();
    }


}
