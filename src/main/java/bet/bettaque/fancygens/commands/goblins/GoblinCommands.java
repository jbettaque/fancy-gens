package bet.bettaque.fancygens.commands.goblins;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.itemutils.ItemUtils;

public class GoblinCommands {

    @CommandHook("givescroll")
    public void giveSummoningScroll(CommandSender sender, GoblinTier stier, Player player){
        giveSummoningScrollBackend(player, stier);
    }

    public void giveSummoningScrollBackend(Player player, GoblinTier tier){
        ItemUtils.give(player, tier.getOraxenItem());
    }
}
