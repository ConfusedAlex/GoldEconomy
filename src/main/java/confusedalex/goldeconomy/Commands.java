package confusedalex.goldeconomy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.commandmanager.CommandHook;

public class Commands {
    GoldEconomy plugin;

    public Commands(GoldEconomy plugin) {
        this.plugin = plugin;
    }

    @CommandHook("balance")
    public void balance(CommandSender commandSender){
        Player player = (Player) commandSender;
        player.sendMessage("Your balance is: " + plugin.getEconomyImplementer().getBalance(Bukkit.getOfflinePlayer(player.getUniqueId())));
    }

    @CommandHook("deposit")
    public void deposit(CommandSender commandSender, String nuggets){
        Converter converter = plugin.getConverter();

        if (nuggets == null) converter.depositAll((Player) commandSender);
        converter.deposit((Player) commandSender, Integer.parseInt(nuggets));
    }
}


