package confusedalex.goldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

public class Commands {
    GoldEconomy plugin;

    public Commands(GoldEconomy plugin) {
        this.plugin = plugin;
    }

    @CommandHook("balance")
    public void balance(CommandSender commandSender){
        Player player = (Player) commandSender;
        player.sendMessage("your balance is:" + plugin.getEconomyImplementer().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())));
    }

    @CommandHook("setbalance")
    public void setbalance(CommandSender commandSender, double adouble){
        Player player = (Player) commandSender;
        plugin.getEconomyImplementer().depositPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()), adouble);
    }
}


